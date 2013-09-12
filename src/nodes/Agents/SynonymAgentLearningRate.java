package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;
import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.BaseStatisticsAggregator;
import simulation.RandomGenerator;
import tools.Pair;

import autoconfiguration.Configurable.Describable;

public class SynonymAgentLearningRate extends AbstractAgent implements Describable {
	
	private enum StatisticsTypes {LEARNING_RATE, LEXICON_SIZE, FINAL_SEMANTIC_CONVERAGE, PROPORTION_SYNONYMS, COVERAGE, AGREEMENT}
	private enum VisualizationTypes {LearningRate, LexiconSize}
	
	private enum MeaningDistribution {Squared, SquaredPlus, Gausian, Uniform}
	private enum WordChoiceStratergy {Random, FirstLearnt, LastLearnt, MostCommon, Probabalistic}
	private enum InventionStratergy {OnePerGeneration, AsNeeded, UntilFull}
	private enum CriticalPeriodStratergy {Fixed, CapacityRelative}
	private enum MutationType {Linear, Multiplicative,Disabled}
	private enum FitnessAdjustment {CAPACITY_COST, COVERAGE}
	
	public static final String INIT_LEARNING_RATE = "Initial learning rate:";
	public static final String MEANING_SPACE_SIZE = "Meaning space size";
	public static final String MEANING_DISTRIBUTION = "Meaning distribution";
	public static final String WORD_CHOICE_STRATERGY = "Word choice stratergy:";
	public static final String INVENTION_STRATERGY = "Invention stratergy:";
	
	public static final String CRITICAL_PERIOD_STRATERGY = "Critical period:";
	public static final String CRITICAL_PERIOD = "Fixed critical period:";
	public static final String RELATIVE_MODIFIER = "Relative critical period:";
	
	public static final String FITNESS_ADJUSTMENT = "Fitness adjustment stratergy:";
	public static final String LEARNING_RATE_COST = "Cost of learning rate:";
	public static final String MUTATION_TYPE = "Mutation Type:";
	
	public static final String THESIS_COMPATIBILITY = "Thesis compatibility mode:";
	
	//Array position shows the meaning
	//First is the token, Second is the count
	private ArrayList<Pair<Integer, Integer>>[] lexicon;
	
	private double learningRate;
	private int lexiconSize;
	
	//For statistics, so we don't have to calculate each time
	private int lexicalCoverage = 0;
	
	private double communicativeAgreement = 0;
	private double communcativeDisagreement = 0;
	
	public SynonymAgentLearningRate(){
		setDefaultParameter(VISUALIZATION_TYPE, VisualizationTypes.values(), new Object[]{});
		setDefaultParameter(Node.STATISTICS_TYPE, StatisticsTypes.values(), StatisticsTypes.values());
		
		setDefaultParameter(INIT_LEARNING_RATE, 0.01);
		setDefaultParameter(MEANING_SPACE_SIZE, 100);
		setDefaultParameter(MEANING_DISTRIBUTION, MeaningDistribution.values(), MeaningDistribution.Squared);
		setDefaultParameter(WORD_CHOICE_STRATERGY, WordChoiceStratergy.values(), WordChoiceStratergy.Random);
		setDefaultParameter(INVENTION_STRATERGY, InventionStratergy.values(), InventionStratergy.OnePerGeneration);
		
		setDefaultParameter(CRITICAL_PERIOD_STRATERGY, CriticalPeriodStratergy.values(), CriticalPeriodStratergy.Fixed);
		setDefaultParameter(CRITICAL_PERIOD, 1000);
		setDefaultParameter(RELATIVE_MODIFIER, 5);
		
		setDefaultParameter(MUTATION_TYPE, MutationType.values(),MutationType.Linear);
		setDefaultParameter(FITNESS_ADJUSTMENT, FitnessAdjustment.values(),new Object[]{FitnessAdjustment.CAPACITY_COST});
		setDefaultParameter(LEARNING_RATE_COST, 100.0);
		
		setDefaultParameter(THESIS_COMPATIBILITY, true);
		
		overrideParameter(BASE_FITNESS, new ConfigurationParameter(10));
	}
	
	public SynonymAgentLearningRate(Configurable config, RandomGenerator random){
		super(config, random);
		this.learningRate = getDoubleParameter(INIT_LEARNING_RATE);
		this.lexiconSize = 0;	
		initializeLexicon();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeLexicon(){
		lexicon = new ArrayList[getIntegerParameter(MEANING_SPACE_SIZE)];
		for(int i = 0; i < lexicon.length; i++){
			lexicon[i] = new ArrayList<Pair<Integer,Integer>>();
		}
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB) {
		SynonymAgentLearningRate agent1 = (SynonymAgentLearningRate)parentA;
		SynonymAgentLearningRate agent2 = (SynonymAgentLearningRate)parentB;

		double rate;//TODO rename
		//Heredity
		if(randomGenerator.nextBoolean()){
			rate = agent1.learningRate;
		}else{
			rate = agent2.learningRate;
		}
		
		//Mutation
		switch ((MutationType)getListParameter(MUTATION_TYPE)[0]) {
		case Linear:
			switch(randomGenerator.nextInt(3) - 1){
			case 0:
				rate += 0.001;
				break;
			
			case 1:
				rate -= 0.001;
				break;
			}
			
			if(rate < 0.001){
				rate = 0.001;
			}
			break;
			
		case Multiplicative:
			switch (randomGenerator.nextInt(3)) {
			case 0:
				rate *= 0.8;
				break;
				
			case 2:
				rate *= 1.2;
				break;

			default:
				break;
			}
			break;
			
		case Disabled:
			//Do nothing.
			break;
			

		default:
			System.err.println("Unrecognized mutation type");
			break;
		}
		
		learningRate = rate;
		
		initializeLexicon();
	}
	
	@Override
	public String getName(){
		return "Synonym Agent";
	}

	@Override
	public void learnUtterance(Utterance utterance) {
		if(utterance.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		//Update count if it is already known and count is needed
		for(Pair<Integer,Integer> pair : lexicon[utterance.meaning]){
			if(pair.first == utterance.signal){
				pair.second++;
				return;
			}
		}
		
		//Else update lexicon with new word
		if(randomGenerator.nextDouble() < learningRate){
			lexicon[utterance.meaning].add(new Pair<Integer,Integer>(utterance.signal,1));
			lexiconSize++;
			if(lexicon[utterance.meaning].size()==1){//If the recently added was the first.
				lexicalCoverage++;
			}
		}
	}

	@Override
	public void invent(){
		if(getListParameter(INVENTION_STRATERGY)[0] == InventionStratergy.OnePerGeneration){
			int meaning = getMeaning();
			int signal = randomGenerator.nextInt(10000);//Wow homonyms are possible
			lexicon[meaning].add(new Pair<Integer,Integer>(signal,1));
			lexiconSize++;
			if(lexicon[meaning].size()==1){//If the recently added was the first.
				lexicalCoverage++;
			}
		}
	}
	
	@Override
	public boolean canStillLearn(int utterancesSeen) {
		return utterancesSeen < getIntegerParameter(CRITICAL_PERIOD);
	}

	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
	
		switch ((StatisticsTypes)statisticsKey) {
		case LEXICON_SIZE:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Lexicon Size:") {
				@Override
				protected double getValue(Node agent) {
					return ((SynonymAgentLearningRate)agent).lexiconSize;
				}
			};
			
		case LEARNING_RATE:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Learning Rate:") {
				@Override
				protected double getValue(Node agent) {
					return ((SynonymAgentLearningRate)agent).learningRate;
				}
			}; 
			
		case FINAL_SEMANTIC_CONVERAGE:
			return new BaseStatisticsAggregator(null, "Final Lexical Distribution:") {
				@Override
				public void endRun(Integer run, ArrayList<Agent> agents) {
					for(int i = 0; i < ((SynonymAgentLearningRate)agents.get(0)).lexicon.length; i++){
						double count = 0;
						for(Agent agent : agents){
							SynonymAgentLearningRate synonymAgent = (SynonymAgentLearningRate)agent;
							count += synonymAgent.lexicon[i].size();
						}
						stats.add(new Pair<Double, Double>(new Double(i), count/agents.size()));
					}
				}
			};
			
		case PROPORTION_SYNONYMS:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Synonym Proportion:") {
				@Override
				protected double getValue(Node in) {
					SynonymAgentLearningRate agent = (SynonymAgentLearningRate)in;
					return ((double)agent.lexiconSize)/agent.lexicalCoverage();
				}
			};
			
		case COVERAGE:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Semantic Coverage") {
				@Override
				protected double getValue(Node in) {
					SynonymAgentLearningRate agent = (SynonymAgentLearningRate)in;
					return agent.lexicalCoverage();
				}
			};
			
		case AGREEMENT: 
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Communicative Agreement") {
				@Override
				protected double getValue(Node in) {
					SynonymAgentLearningRate agent = (SynonymAgentLearningRate)in;
					return agent.communicativeAgreement/(agent.communicativeAgreement + agent.communcativeDisagreement);
				}
			};
		
		default:
			System.err.println(SynonymAgentLearningRate.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	@Override
	public void teach(Node learner) {
		Utterance toTeach = getUtterance();
		
		if(toTeach.isNull() && getListParameter(INVENTION_STRATERGY)[0] == InventionStratergy.AsNeeded){
			toTeach.signal = randomGenerator.nextInt(10000);
			learnUtterance(toTeach);
		}
		
		learner.learnUtterance(toTeach);
	}
	
	private Utterance getUtterance(){
		int meaning = getMeaning();
		return new Utterance(meaning, getWord(meaning));
	}
	
	private int getMeaning(){	
		switch ((MeaningDistribution)getListParameter(MEANING_DISTRIBUTION)[0]) {
		case Squared:
			return (int)(randomGenerator.nextDouble()*randomGenerator.nextDouble()*lexicon.length);

		case SquaredPlus:
			int val = (lexicon.length);
			while(val >= lexicon.length){
				val = (int)(randomGenerator.nextDouble()*randomGenerator.nextDouble()*(lexicon.length*2));
			}
			return val;
			
			
		case Uniform:
			return randomGenerator.nextInt(lexicon.length);
			
		case Gausian:
			double gaussian = randomGenerator.nextGaussian()*lexicon.length/5;
			int retVal = (int)Math.abs(gaussian);
			if(retVal >= lexicon.length){retVal=lexicon.length-1;}
			return retVal;
			
		default:
			System.err.println("Shouldn't be here");
			return -1;
		}
	}
	
	private int getWord(int meaning){

		int tokensForMeaning = lexicon[meaning].size();
		if(tokensForMeaning == 0){
			return Utterance.SIGNAL_NULL_VALUE;
		}
		
		switch((WordChoiceStratergy)getListParameter(WORD_CHOICE_STRATERGY)[0]){
		case FirstLearnt:
			return lexicon[meaning].get(0).first;
			
		case LastLearnt:
			return lexicon[meaning].get(tokensForMeaning-1).first;
			
		case Random:
			return lexicon[meaning].get(randomGenerator.nextInt(tokensForMeaning)).first;
			
		case MostCommon:
			Pair<Integer, Integer> bestSoFar = lexicon[meaning].get(0);
			for(int i = 1; i < lexicon[meaning].size(); i++){
				Pair<Integer, Integer> current = lexicon[meaning].get(i);
				if(current.second > bestSoFar.second){
					bestSoFar = current;
				}
			}
			return bestSoFar.first;
			
		case Probabalistic:
			int encounters = 0;
			for(Pair<Integer, Integer> pair : lexicon[meaning]){
				encounters += pair.second;
			}
			int selectionPoint = randomGenerator.nextInt(encounters);
			int pointer = 0;
			for(Pair<Integer, Integer> pair : lexicon[meaning]){
				pointer += pair.second;
				if(pointer > selectionPoint){
					return pair.first;
				}
			}
			
		default:
			System.err.println("Shouldn't be here");
			return -1;
		}
	}

	@Override
	public void communicate(Node partner) {
		SynonymAgentLearningRate opposite = (SynonymAgentLearningRate)partner;
		
		//TODO add AS Needed invention stratergy here
		
		if(opposite.canYouUnderstand(getUtterance())){
			communicativeAgreement++;
			if(!getBooleanParameter(THESIS_COMPATIBILITY)){
				setFitness(getFitness()+1);
			}
		}else{
			communcativeDisagreement++;
		}
		
	}

	private boolean canYouUnderstand(Utterance utterance){
		boolean retVal = false;
		for(Pair<Integer,Integer> pair : lexicon[utterance.meaning]){
			if(pair.first == utterance.signal){
				retVal = true;
				break;
			}
		}
		if(retVal){
			setFitness(getFitness()+1);
		}
		return retVal;
	}
	
	@Override
	public void finalizeFitnessValue() {
		
		for(Object key : getListParameter(FITNESS_ADJUSTMENT)){
			
			switch ((FitnessAdjustment)key) {
			
			case CAPACITY_COST:
				setFitness(getFitness()-learningRate*getDoubleParameter(LEARNING_RATE_COST));
				break;
				
			case COVERAGE:
				setFitness(getFitness()+lexicalCoverage());
				break;
				
			default:
				System.err.println("Unknown FitnessAdjustment type in " + SynonymAgentLearningRate.class.getName());
				break;
			}
		}
	}
	
	private int lexicalCoverage(){
		return lexicalCoverage;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		switch ((VisualizationTypes)visualizationKey) {
		case LearningRate:
			c = new Color((int)(learningRate*255),(int)(learningRate*255),0);
			break;
			
		case LexiconSize:
			c = new Color(lexiconSize,0,lexiconSize);
			break;
			
		default:
			c = null;
			System.err.println("What happened?");
		}
		
		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
	}

	@Override
	public String getDescription() {
		return "An agent designed to study the evolution of the lexicon. It agent has a lexicon of a fixed size which it fills through" +
				"learning interactions with other agents. The agent can learn multiple words for each meaning so is capable of learning" +
				" synonyms, and equally may never learn a word for significant parts of its possible semantic space. Agents reproduce via" +
				" asexual reproduction with the possibility of mutating the maximum size of their offspring's lexicon (Lexicon Capacity). " +
				"Fitness is determined by taking determining if a random utterance produced by a neighbour is present in the agent's lexi" +
				"con. An increased Communications Per Neighbour is reccomended for this agent." +
				"\n\n" +
				INIT_LEARNING_RATE+ ":\n" +
				"Probability of an agent in the first generation learning a lexeme when heard once.\n\n" +
				MEANING_SPACE_SIZE + "\n" +
				"The number of different meanings which the agent may learn words for.\n\n" +
				MEANING_DISTRIBUTION + "\n" +
				"Method for determining what the agents will talk about during learning and communication. " + MeaningDistribution.Uniform + 
				"; all meanings equally likely. " + MeaningDistribution.Squared + "; Math.random()^2*range. " + MeaningDistribution.Gausian + "" +
				"; normally distributed focused around meaning 0.\n\n" +
				WORD_CHOICE_STRATERGY +"\n" +
				"How the agent chooses words from its lexicon when several exist for the desired meaning. " + WordChoiceStratergy.FirstLearnt + "; " +
				"always use the first word learnt. " + WordChoiceStratergy.LastLearnt + "; always use the last word learnt. " + WordChoiceStratergy.MostCommon
				+ "; use only most commonly encountered word. " + WordChoiceStratergy.Random + "; use a word at random with equal probability. " +
				WordChoiceStratergy.Probabalistic + "; randomly choose a word proportional to how often they have been encountered.\n\n" +
				MUTATION_TYPE + "\n" +
				"Determines how mutations effect the agents lexicon capacity. " + MutationType.Linear + "; Adds or subtracts 1 to the lexicon capacity. " +
				MutationType.Multiplicative +"; Multiplies/devides the lexicon capacity of the lexicon.\n\n" +
				FITNESS_ADJUSTMENT + "\n" +
				"Modifications that should be made to the final fitness value before selection. " + FitnessAdjustment.CAPACITY_COST + "; " +
				"decreases fitness based on lexiconCapacity*" + LEARNING_RATE_COST + " to account for the opportunity costs incured to the agent by" +
				" having a faster learning rate. " + FitnessAdjustment.COVERAGE + "; raises fitness of agents with better coverage of the lexical space.";
	}
	
	/** 
	 * Tester to find the shape of a probability distribution.
	 */
	/*public static void main(String[] args){
		
		SynonymAgent agent = new SynonymAgent();
		agent.setDefaultParameter(MEANING_DISTRIBUTION, MeaningDistribution.values(), MeaningDistribution.Squared);
		
		SynonymAgent agentFinalAgent = new SynonymAgent(agent, new RandomGenerator(System.currentTimeMillis()));
		
		for(int)
		agentFinalAgent.getMeaning()
	}*/
		
}
