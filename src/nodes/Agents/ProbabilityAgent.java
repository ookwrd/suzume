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

	private enum VisualizationTypes {NUMBER_NULLS,GENOTYPE,PHENOTYPE,SINGLE_WORD,SINGLE_GENE};
	private enum StatisticsTypes {GRAMMAR_ADJUST_COUNT}
	
	private static final String LEARNING_PROBABILITY_ON_MATCH = "Learning probability match";
	private static final String LEARNING_PROBABILITY_ON_MISMATCH = "Learning probability mismatch";
	private static final String MUTATION_RATE = "Mutation Rate";
	private static final String INVENTION_PROBABILITY = "Invention Probability";
	private static final String INVENTION_CHANCES = "Invention Chances";
	
	private double grammarAdjustmentCount = 0;
	
	public ProbabilityAgent(){
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),StatisticsTypes.values()));
		setDefaultParameter(LEARNING_PROBABILITY_ON_MATCH, new ConfigurationParameter(0.7));
		setDefaultParameter(LEARNING_PROBABILITY_ON_MISMATCH, new ConfigurationParameter(0.5));
		setDefaultParameter(MUTATION_RATE, new ConfigurationParameter(0.00025));
		setDefaultParameter(INVENTION_PROBABILITY, new ConfigurationParameter(0.01));
		setDefaultParameter(INVENTION_CHANCES, new ConfigurationParameter(5));
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values()));
		
		overrideParameter(SYNTACTIC_SPACE_SIZE, new ConfigurationParameter(8));
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB,
			int id, RandomGenerator randomGenerator) {
		super.initialize(parentA, id, randomGenerator);
		
		ProbabilityAgent parent1 = (ProbabilityAgent)parentA;
		ProbabilityAgent parent2 = (ProbabilityAgent)parentB;
		
		//Crossover
		int crossoverPoint = randomGenerator.nextInt(getParameter(NUMBER_OF_MEANINGS).getInteger());
		int i = 0;
		while(i < crossoverPoint){
			chromosome.set(i, parent1.chromosome.get(i));
			i++;
		}
		while(i < getParameter(NUMBER_OF_MEANINGS).getInteger()){
			chromosome.set(i, parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < getParameter(NUMBER_OF_MEANINGS).getInteger(); j++){
			if(randomGenerator.nextDouble() < getDoubleParameter(MUTATION_RATE)){
				chromosome.set(j, randomGenerator.nextInt(getParameter(SYNTACTIC_SPACE_SIZE).getInteger()));
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
		case NUMBER_NULLS:
			int numberOfNulls = new Double(numberOfNullsInGrammar()).intValue();
			c = new Color(255, 255-numberOfNulls*16, 255-numberOfNulls*16);
			break;
			
		case GENOTYPE:
			c = new Color(
					Math.abs(chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16),
					Math.abs(chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16),
					Math.abs(chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)
			);
			break;
			
		case PHENOTYPE:
			c = new Color(
					Math.abs(grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16),
					Math.abs(grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16),
					Math.abs(grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)
			);
			break;
			
		case SINGLE_WORD:
		case SINGLE_GENE:
			int value;
			if(visualizationKey.equals("singleWord")){
				value = grammar.get(0);
			}else{
				value = chromosome.get(0);
			}		
			if(value == 0){
				c = Color.WHITE;
			} else if (value == 1){
				c = Color.BLACK;
			} else if (value == 2){
				c = Color.BLUE;
			}else if (value == 3){
				c = Color.GREEN;
			}else if (value == 4){
				c = Color.YELLOW;
			}else if (value == 5){
				c = Color.ORANGE;
			}else if (value == 6){
				c = Color.CYAN;
			}else if (value == 7){
				c = Color.DARK_GRAY;
			}else if (value == 8){
				c = Color.GRAY;
			}else if (value == 9){
				c = Color.MAGENTA;
			}else{
				c = Color.RED;
			}
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
			System.err.println(YamauchiHashimoto2010.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	@Override
	public String getName(){
		return "Probability Agent";
	}
	
	@Override
	public String getDescription() {
		return "Agent I cant be bothered to describe that can take more than just 2 different types of values for each bias";
	}

}