package nodes.Agents;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;

import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;
import autoconfiguration.Configurable.Describable;

import simulation.RandomGenerator;

public class YamauchiHashimoto2010 extends AbstractGeneGrammarAgent implements Agent, Describable {

	private enum VisualizationTypes {LEARNING_INTENSITY} 	
	private enum StatisticsTypes {LEFTOVER_LEARNING_RESC}
	
	protected static final String INVENTION_PROBABILITY = "Invention Probability";
	protected static final String MUTATION_RATE = "Mutation Rate";
	protected static final String LEARNING_RESOURCE = "Learning Resource";
	protected static final String CRITICAL_PERIOD = "Critical Period";
	protected static final String LEARNING_COST_ON_MATCH = "Learning Resource on Match";
	protected static final String LEARNING_COST_ON_MISMATCH = "Learning Resource on MisMatch";
	
	protected double learningResource;
	
	public YamauchiHashimoto2010(){
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(), StatisticsTypes.values()));
		setDefaultParameter(LEARNING_RESOURCE, new ConfigurationParameter(24));
		setDefaultParameter(CRITICAL_PERIOD, new ConfigurationParameter(200));
		setDefaultParameter(LEARNING_COST_ON_MATCH, new ConfigurationParameter(1));
		setDefaultParameter(LEARNING_COST_ON_MISMATCH, new ConfigurationParameter(4));
		setDefaultParameter(MUTATION_RATE, new ConfigurationParameter(0.00025));
		setDefaultParameter(INVENTION_PROBABILITY, new ConfigurationParameter(0.01));
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), false));
		
		fixParameter(SYNTACTIC_SPACE_SIZE);
		removeListOptions(VISUALIZATION_TYPE, new Object[]{AbstractAgent.VisualizationTypes.ALIVE});	
	}
	
	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		learningResource = getIntegerParameter(LEARNING_RESOURCE);
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator){
		YamauchiHashimoto2010 parent1 = (YamauchiHashimoto2010)parentA;
		YamauchiHashimoto2010 parent2 = (YamauchiHashimoto2010)parentB;
		
		super.initialize(parent1,id,randomGenerator);
		chromosome = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));

		learningResource = getIntegerParameter(LEARNING_RESOURCE);
		
		//Crossover
		int crossoverPoint = randomGenerator.nextInt(getParameter(NUMBER_OF_MEANINGS).getInteger());
		int i = 0;
		while(i < crossoverPoint){
			chromosome.add(parent1.chromosome.get(i));
			i++;
		}
		while(i < getParameter(NUMBER_OF_MEANINGS).getInteger()){
			chromosome.add(parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < getParameter(NUMBER_OF_MEANINGS).getInteger(); j++){
			if(randomGenerator.nextDouble() < getDoubleParameter(MUTATION_RATE)){
				chromosome.set(j, randomGenerator.nextBoolean()?0:1);
			}
		}
	}
	
	@Override
	public String getName(){
		return "Yamauchi & Hashimoto 2010 Agent";
	}
	
	/**
	 * Use the remainder of the learning resource to potentially invent parts of the grammar.
	 * The agent has a probability of 0.01 to turn an empty value to a 0 or a 1   
	 */
	@Override
	public void invent() {
		
		while(grammar.contains(Utterance.SIGNAL_NULL_VALUE) && learningResource > 0){
			learningResource--;
			if(randomGenerator.nextDouble() < getDoubleParameter(INVENTION_PROBABILITY)){
				
				//Collect indexes of all null elements
				ArrayList<Integer> nullIndexes = new ArrayList<Integer>();
				for(int i = 0; i < grammar.size(); i++){
					
					Integer allele = grammar.get(i);
					if(allele == Utterance.SIGNAL_NULL_VALUE){
						nullIndexes.add(i);
					}
				}
				
				//Choose a random null element to invent a new value for
				Integer index = nullIndexes.get(randomGenerator.nextInt(nullIndexes.size()));
				
				grammar.set(index, randomGenerator.nextBoolean()?0:1);
			}
		}
	}
	
	/**
	 * The agent learns an utterance 
	 * The learning resource is updated
	 * 
	 * @param teacher the agent teaching
	 * @param utterance the utterance taught
	 */
	@Override
	public void learnUtterance(Utterance u) {
		
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(u.signal == chromosome.get(u.meaning)){//Matches this agents UG
			if(learningResource < getIntegerParameter(LEARNING_COST_ON_MATCH)){
				learningResource = 0;
				return;
			}
			
			grammar.set(u.meaning, u.signal);
			learningResource -= getParameter(LEARNING_COST_ON_MATCH).getInteger();
			
		}else{//Doesn't match this agents UG
			if(learningResource < getParameter(LEARNING_COST_ON_MISMATCH).getInteger()){
				learningResource = 0;
				return;
			}
			
			grammar.set(u.meaning, u.signal);
			learningResource -= getParameter(LEARNING_COST_ON_MISMATCH).getInteger();
		}
		
	}

	@Override
	public boolean canStillLearn(int utterancesSeen) {
		return learningResource > 0 && getIntegerParameter(CRITICAL_PERIOD) > utterancesSeen;
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
	
		switch ((StatisticsTypes)statisticsKey) {
		case LEFTOVER_LEARNING_RESC:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Leftover Learning resource") {
				@Override
				protected double getValue(Node agent) {
					return ((YamauchiHashimoto2010)agent).learningResource;
				}
			};

		default:
			System.err.println(YamauchiHashimoto2010.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		switch ((VisualizationTypes)visualizationKey) {
		case LEARNING_INTENSITY:
			int learningIntensity = new Double(learningResource).intValue();
			c = new Color(255, 255-learningIntensity*16, 255-learningIntensity);
			break;
			
		default:
			System.err.println("Unrecognized Visualization type in " + this.getClass().getCanonicalName());
			return;
		}
		
		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
	}

	@Override
	public String getDescription() {
		return "An agent as described in Yamauchi and Hashimoto 2010 \"Relaxation of Selection, Niche Construction, and " +
				"the Baldwin Effect in Language Evolution\" which was designed to study the interaction of biological and " +
				"cultural evolution on the evolution of language. Please see the paper for model description. For a discussion" +
				"of model behaviour please also see also McCrohon and Witkowski 2011 \"Devil in the details: Analysis of a " +
				"coevolutionary model of language evolution via relaxation of selection\".";
	}
}