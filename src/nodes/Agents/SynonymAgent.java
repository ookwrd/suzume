package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;
import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.BaseStatisticsAggregator;
import simulation.RandomGenerator;
import tools.Pair;

public class SynonymAgent extends AbstractAgent {
	
	private enum StatisticsTypes {LEXICON_CAPACITY, LEXICON_SIZE, SEMANTIC_CONVERAGE, PROPORTION_SYNONYMS}
	private enum VisualizationTypes {LexiconCapacity, LexiconSize}
	
	private enum MeaningDistribution {Squared, Uniform}
	private enum WordChoiceStratergy {Random, FirstLearnt, LastLearnt, MostCommon, Probabalistic}
	private enum InventionStratergy {OnePerGeneration, AsNeeded}
	private enum CriticalPeriodStratergy {Fixed, CapacityRelative}
	
	private enum MutationType {Linear, Multiplicative}
	
	private enum FitnessAdjustment {CAPACITY_COST, COVERAGE}
	
	public final String INIT_LEXICAL_CAPACITY = "Initial lexical capacity:";
	public final String MEANING_SPACE_SIZE = "Meaning space size";
	public final String MEANING_DISTRIBUTION = "Meaning distribution";
	public final String WORD_CHOICE_STRATERGY = "Word choice stratergy:";
	public final String INVENTION_STRATERGY = "Invention stratergy:";
	
	public final String CRITICAL_PERIOD_STRATERGY = "Critical period:";
	public final String CRITICAL_PERIOD = "Fixed critical period:";
	public final String RELATIVE_MODIFIER = "Relative critical period:";
	
	public final String FITNESS_ADJUSTMENT = "Fitness adjustment stratergy:";
	public final String LEXICON_CAPACITY_COST = "Cost of lexical capacity:";
	public final String MUTATION_TYPE = "Mutation Type:";
	
	private ArrayList<Pair<Integer, Integer>>[] lexicon;
	
	private int lexiconCapacity;
	private int lexiconSize;
	
	private int utterancesSeen = 0;
	
	public SynonymAgent(){
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(),false));
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(), StatisticsTypes.values()));
		
		setDefaultParameter(INIT_LEXICAL_CAPACITY, new ConfigurationParameter(10));
		setDefaultParameter(MEANING_SPACE_SIZE, new ConfigurationParameter(100));
		setDefaultParameter(MEANING_DISTRIBUTION, new ConfigurationParameter(MeaningDistribution.values(), true));
		setDefaultParameter(WORD_CHOICE_STRATERGY, new ConfigurationParameter(WordChoiceStratergy.values(), true));
		setDefaultParameter(INVENTION_STRATERGY, new ConfigurationParameter(InventionStratergy.values(), true));
		
		setDefaultParameter(CRITICAL_PERIOD_STRATERGY, new ConfigurationParameter(CriticalPeriodStratergy.values(), true));
		setDefaultParameter(CRITICAL_PERIOD, new ConfigurationParameter(1000));
		setDefaultParameter(RELATIVE_MODIFIER, new ConfigurationParameter(5));
		
		setDefaultParameter(MUTATION_TYPE, new ConfigurationParameter(MutationType.values(),true));
		setDefaultParameter(FITNESS_ADJUSTMENT, new ConfigurationParameter(FitnessAdjustment.values(),new Object[]{FitnessAdjustment.CAPACITY_COST}));
		setDefaultParameter(LEXICON_CAPACITY_COST, new ConfigurationParameter(0.1));
	}

	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		this.lexiconCapacity = getIntegerParameter(INIT_LEXICAL_CAPACITY);
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
	public void initializeAgent(Node parentA, Node parentB, int id,
			RandomGenerator randomGenerator) {
		SynonymAgent agent1 = (SynonymAgent)parentA;
		SynonymAgent agent2 = (SynonymAgent)parentB;
		
		super.initialize(agent1, id, randomGenerator);

		int size;
		//Heredity
		if(randomGenerator.randomBoolean()){
			size = agent1.lexiconCapacity;
		}else{
			size = agent2.lexiconCapacity;
		}
		
		//Mutation
		switch ((MutationType)getListParameter(MUTATION_TYPE)[0]) {
		case Linear:
			size += randomGenerator.randomInt(3) - 1;
			if(size < 1){
				size = 1;
			}
			break;
			
		case Multiplicative:
			switch (randomGenerator.randomInt(3)) {
			case 0:
				size = (int) (size * 0.8);
				break;
				
			case 2:
				size = (int) (size * 1.2);
				break;

			default:
				break;
			}
			break;

		default:
			System.err.println("Unrecognized mutation type");
			break;
		}
		
		lexiconCapacity = size;
		
		initializeLexicon();
	}
	
	@Override
	public String getName(){
		return "Synonym Agent";
	}

	@Override
	public void learnUtterance(Utterance utterance) {
		utterancesSeen++;
		
		if(utterance.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		//Update count if it is already known
		for(Pair<Integer,Integer> pair : lexicon[utterance.meaning]){
			if(pair.first == utterance.signal){
				pair.second++;
				return;
			}
		}
		
		//Else update lexicon with new word
		lexicon[utterance.meaning].add(new Pair<Integer,Integer>(utterance.signal,1));
		lexiconSize++;
	}

	@Override
	public void invent(){
		if(getListParameter(INVENTION_STRATERGY)[0] == InventionStratergy.OnePerGeneration){
			int meaning = getMeaning();
			int value = randomGenerator.randomInt(10000);
			learnUtterance(new Utterance(meaning, value));
		}
	}
	
	@Override
	public boolean canStillLearn() {
		if(lexiconSize >= lexiconCapacity){
			return false;
		}
		
		if(getListParameter(CRITICAL_PERIOD_STRATERGY)[0] == CriticalPeriodStratergy.Fixed){
			return utterancesSeen < getIntegerParameter(CRITICAL_PERIOD);
		}else{
			return utterancesSeen < getIntegerParameter(RELATIVE_MODIFIER)*lexiconCapacity;
		}
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
					return ((SynonymAgent)agent).lexiconSize;
				}
			};
			
		case LEXICON_CAPACITY:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Lexicon Capacity:") {
				@Override
				protected double getValue(Node agent) {
					return ((SynonymAgent)agent).lexiconCapacity;
				}
			}; 
			
		case SEMANTIC_CONVERAGE:
			return new BaseStatisticsAggregator(null, "Final Lexical Distribution:") {
				@Override
				public void endRun(Integer run, ArrayList<Agent> agents) {
					for(int i = 0; i < ((SynonymAgent)agents.get(0)).lexicon.length; i++){
						double count = 0;
						for(Agent agent : agents){
							SynonymAgent synonymAgent = (SynonymAgent)agent;
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
					SynonymAgent agent = (SynonymAgent)in;
					return ((double)agent.lexiconSize)/agent.lexicalCoverage();
				}
			};
		
		default:
			System.err.println(SynonymAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	@Override
	public void teach(Node learner) {
		Utterance toTeach = getUtterance();
		
		if(toTeach.isNull() && getListParameter(INVENTION_STRATERGY)[0] == InventionStratergy.AsNeeded){
			toTeach.signal = randomGenerator.randomInt(10000);
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
			return (int)(randomGenerator.random()*randomGenerator.random()*lexicon.length);

		case Uniform:
			return randomGenerator.randomInt(lexicon.length);
			
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
			return lexicon[meaning].get(randomGenerator.randomInt(tokensForMeaning)).first;
			
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
			int selectionPoint = randomGenerator.randomInt(encounters);
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
		SynonymAgent opposite = (SynonymAgent)partner;
		
		if(opposite.canYouUnderstand(getUtterance())){
			setFitness(getFitness()+1);
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
		return false;
	}
	
	@Override
	public void finalizeFitnessValue() {
		
		for(Object key : getListParameter(FITNESS_ADJUSTMENT)){
			
			switch ((FitnessAdjustment)key) {
			
			case CAPACITY_COST:
				setFitness(getFitness()-lexiconCapacity*getDoubleParameter(LEXICON_CAPACITY_COST));
				break;
				
			case COVERAGE:
				setFitness(getFitness()+lexicalCoverage());
				break;
				
			default:
				System.err.println("Unknown FitnessAdjustment type in " + SynonymAgent.class.getName());
				break;
			}
		}
	}
	
	private int lexicalCoverage(){
		int count = 0;
		for(ArrayList<Pair<Integer,Integer>> meaning : lexicon){
			if(meaning.size() > 0){
				count++;
			}
		}
		return count;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		switch ((VisualizationTypes)visualizationKey) {
		case LexiconCapacity:
			c = new Color(lexiconCapacity,lexiconCapacity,0);
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
		
}
