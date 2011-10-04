package nodes.Agents;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.Node;
import nodes.NodeConfiguration;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Node.StatisticsCollectionPoint;

import autoconfiguration.ConfigurationParameter;
import autoconfiguration.Configurable.Describable;



import simulation.RandomGenerator;

public class YamauchiHashimoto2010 extends AbstractGeneGrammarAgent implements Agent, Describable {

	private enum VisualizationTypes {NUMBER_NULLS, GENE_GRAMMAR_MATCH, LEARNING_INTENSITY, GENOTYPE, PHENOTYPE, SINGLE_GENE, SINGLE_WORD} 	
	
	protected static final String INVENTION_PROBABILITY = "Invention Probability";
	protected static final String MUTATION_RATE = "Mutation Rate";
	protected static final String LEARNING_RESOURCE = "Learning Resource";
	protected static final String LEARNING_COST_ON_MATCH = "Learning Resource on Match";
	protected static final String LEARNING_COST_ON_MISMATCH = "Learning Resource on MisMatch";
	
	protected double learningResource;
	
	public YamauchiHashimoto2010(){
		setDefaultParameter(LEARNING_RESOURCE, new ConfigurationParameter(24));
		setDefaultParameter(LEARNING_COST_ON_MATCH, new ConfigurationParameter(1));
		setDefaultParameter(LEARNING_COST_ON_MISMATCH, new ConfigurationParameter(4));
		setDefaultParameter(MUTATION_RATE, new ConfigurationParameter(0.00025));
		setDefaultParameter(INVENTION_PROBABILITY, new ConfigurationParameter(0.01));
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), false));
		
		fixParameter(NUMBER_OF_TOKENS);
	}
	
	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		learningResource = getIntegerParameter(LEARNING_RESOURCE);
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator){
		
		YamauchiHashimoto2010 parent1 = (YamauchiHashimoto2010)parentA;
		YamauchiHashimoto2010 parent2 = (YamauchiHashimoto2010)parentB;
		
		super.initialize(parent1,id,randomGenerator);
		chromosome = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));


		learningResource = getParameter(LEARNING_RESOURCE).getInteger();
		
		//Crossover
		int crossoverPoint = randomGenerator.randomInt(getParameter(NUMBER_OF_MEANINGS).getInteger());
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
			if(randomGenerator.random() < getDoubleParameter(MUTATION_RATE)){
				chromosome.set(j, randomGenerator.randomBoolean()?0:1);
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
			if(randomGenerator.random() < getDoubleParameter(INVENTION_PROBABILITY)){
				
				//Collect indexes of all null elements
				ArrayList<Integer> nullIndexes = new ArrayList<Integer>();
				for(int i = 0; i < grammar.size(); i++){
					
					Integer allele = grammar.get(i);
					if(allele == Utterance.SIGNAL_NULL_VALUE){
						nullIndexes.add(i);
					}
				}
				
				//Choose a random null element to invent a new value for
				Integer index = nullIndexes.get(randomGenerator.randomInt(nullIndexes.size()));
				
				grammar.set(index, randomGenerator.randomBoolean()?0:1);
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
	public boolean canStillLearn() {
		return learningResource > 0;
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = super.getStatisticsAggregators();
	
		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication, "Leftover Learning resource") {
			@Override
			protected double getValue(Node agent) {
				return ((YamauchiHashimoto2010)agent).learningResource;
			}
		});
		
		return retVal;
	}
	
	@Override//TODO this should just choose a color
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}
		
		Color c;
		
		switch ((VisualizationTypes)visualizationKey) {
		case NUMBER_NULLS:
			int numberOfNulls = new Double(numberOfNullsInGrammar()).intValue();
			c = new Color(255, 255-numberOfNulls*16, 255-numberOfNulls*16);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;

		case GENE_GRAMMAR_MATCH:
			int geneGrammarMatch = new Double(geneGrammarMatch()).intValue();
			c = new Color(255, 255-geneGrammarMatch*16, 255-geneGrammarMatch);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		case LEARNING_INTENSITY:
			int learningIntensity = new Double(learningResource).intValue();
			c = new Color(255, 255-learningIntensity*16, 255-learningIntensity);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		case GENOTYPE:
			c = new Color(
					Math.abs(chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16),
					Math.abs(chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16),
					Math.abs(chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)
			);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		case PHENOTYPE:
			c = new Color(
					Math.abs(grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16),
					Math.abs(grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16),
					Math.abs(grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)
					);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		case SINGLE_WORD:
			if(grammar.get(0) == 0){
				c = Color.WHITE;
			} else if (grammar.get(0) == 1){
				c = Color.BLACK;
			} else{
				c = Color.RED;
			}
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		case SINGLE_GENE:
			if(chromosome.get(0) == 0){
				c = Color.WHITE;
			} else if (chromosome.get(0) == 1){
				c = Color.BLACK;
			} else{
				c = Color.RED;
			}
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		default:
			System.err.println("Unrecognized Visualization type in YamauchiHashimoto2010");
		}
		
	}

	@Override
	public String getDescription() {
		return "Agent from Yamauchi and Hashimoto 2010 designed to study the interaction of .... See also McCrohon and Witkowski 2011.";
	}
}