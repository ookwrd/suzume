package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import autoconfiguration.ConfigurationParameter;
import autoconfiguration.Configurable.Describable;

import simulation.RandomGenerator;

public class ProbabilityAgent extends AbstractGeneGrammarAgent implements Describable {

	private enum VisualizationTypes {ADJUSTMENT_COUNT};
	private enum StatisticsTypes {GRAMMAR_ADJUST_COUNT}
	
	private static final String LEARNING_PROBABILITY_ON_MATCH = "Learning probability match";
	private static final String LEARNING_PROBABILITY_ON_MISMATCH = "Learning probability mismatch";
	private static final String MUTATION_RATE = "Mutation Rate";
	private static final String INVENTION_PROBABILITY = "Invention Probability";
	private static final String INVENTION_CHANCES = "Invention Chances";
	
	private double grammarAdjustmentCount = 0;
	
	public ProbabilityAgent(){
		setDefaultParameter(Node.STATISTICS_TYPE, StatisticsTypes.values(), StatisticsTypes.values());
		setDefaultParameter(LEARNING_PROBABILITY_ON_MATCH, 0.7);
		setDefaultParameter(LEARNING_PROBABILITY_ON_MISMATCH, 0.5);
		setDefaultParameter(MUTATION_RATE, 0.00025);
		setDefaultParameter(INVENTION_PROBABILITY, 0.01);
		setDefaultParameter(INVENTION_CHANCES, 5);
		setDefaultParameter(VISUALIZATION_TYPE, VisualizationTypes.values(), new Object[]{});
		
		overrideParameter(SYNTACTIC_SPACE_SIZE, new ConfigurationParameter(3));
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB,
			int id, RandomGenerator randomGenerator) {
		super.initialize(parentA, id, randomGenerator);
		
		ProbabilityAgent parent1 = (ProbabilityAgent)parentA;
		ProbabilityAgent parent2 = (ProbabilityAgent)parentB;
		
		//Crossover
		int crossoverPoint = randomGenerator.nextInt(getIntegerParameter(SEMANTIC_SPACE_SIZE));
		int i = 0;
		while(i < crossoverPoint){
			chromosome.set(i, parent1.chromosome.get(i));
			i++;
		}
		while(i < getIntegerParameter(SEMANTIC_SPACE_SIZE)){
			chromosome.set(i, parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < getIntegerParameter(SEMANTIC_SPACE_SIZE); j++){
			if(randomGenerator.nextDouble() < getDoubleParameter(MUTATION_RATE)){
				chromosome.set(j, randomGenerator.nextInt(getIntegerParameter(SYNTACTIC_SPACE_SIZE)));
			}
		}
	}
	
	@Override
	public void invent() {
		int chances = getIntegerParameter(INVENTION_CHANCES);
		for(int j = 0; j < chances && grammar.contains(Utterance.SIGNAL_NULL_VALUE); j++){
			
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
				grammar.set(index, randomGenerator.nextInt(getIntegerParameter(SYNTACTIC_SPACE_SIZE)));
			}
		}
	}

	@Override
	public void learnUtterance(Utterance u) {
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(u.signal == chromosome.get(u.meaning)){//Matches this agents UG
			if(randomGenerator.nextDouble() < getDoubleParameter(LEARNING_PROBABILITY_ON_MATCH)){
				grammar.set(u.meaning, u.signal);
				grammarAdjustmentCount++;
			}
		}else{//Doesn't match this agents UG
			if(randomGenerator.nextDouble() < getDoubleParameter(LEARNING_PROBABILITY_ON_MISMATCH)){
				grammar.set(u.meaning, u.signal);
				grammarAdjustmentCount++;
			}
		}
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		switch((VisualizationTypes)visualizationKey){
		case ADJUSTMENT_COUNT:
			c= mapValueToWhiteBlue(grammarAdjustmentCount, 100);
			break;
			
		default:
			System.err.println("Unrecognized visualization type");
			return;
		}

		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
		
		switch((StatisticsTypes)statisticsKey){
		case GRAMMAR_ADJUST_COUNT:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Grammar Adjustment count") {
				@Override
				protected double getValue(Node agent) {
					return ((ProbabilityAgent)agent).grammarAdjustmentCount;
				}
			};
			
		default:
			System.err.println(ProbabilityAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	@Override
	public String getName(){
		return "Probability Agent";
	}
	
	@Override
	public String getDescription() {
		return "Agent that learns encountered items with a probability determined by whether or not they match its internal UG bias or not." +
				"\n\n" +
				LEARNING_PROBABILITY_ON_MATCH + ":\n" +
				"Probability of learning from a token that matches the agent's UG value." +
				"\n\n" +
				LEARNING_PROBABILITY_ON_MISMATCH + ":\n" +
				"Probability of learning from a token that does not match the agent's UG value." +
				"\n\n" +
				SYNTACTIC_SPACE_SIZE + ":\n" +
				"The number of possible syntactic tokens learnable for each meaning.";
	}

}
