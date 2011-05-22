package Agents;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.PopulationNode;

import simulation.RandomGenerator;

public class YamauchiHashimoto2010 extends AbstractAgent implements Agent {

	protected static final String[] visualizationTypes = {"numberNulls", "genotype", "phenotype", "singleGene", "singleWord"};
	
	private static final String VISUALIZATION_TYPE = "Visualization Type";
	private static final String INVENTION_PROBABILITY = "Invention Probability";
	private static final String MUTATION_RATE = "Mutation Rate";
	private static final String LEARNING_RESOURCE = "Learning Resource";
	private static final String LEARNING_COST_ON_MATCH = "Learning Resource on Match";
	private static final String LEARNING_COST_ON_MISMATCH = "Learning Resource on MisMatch";
	
	{
		defaultParameters.put(LEARNING_RESOURCE, new ConfigurationParameter(24));
		defaultParameters.put(LEARNING_COST_ON_MATCH, new ConfigurationParameter(1));
		defaultParameters.put(LEARNING_COST_ON_MISMATCH, new ConfigurationParameter(4));
		defaultParameters.put(MUTATION_RATE, new ConfigurationParameter(0.00025));
		defaultParameters.put(INVENTION_PROBABILITY, new ConfigurationParameter(0.01));
		defaultParameters.put(VISUALIZATION_TYPE, new ConfigurationParameter(visualizationTypes));
		//defaultParameters.put("Meaning space size", new ConfigurationParameter(12));
	}

	protected ArrayList<Integer> chromosome;
	
	protected int learningResource;
	protected int matchingLearningCost;
	protected int nonMatchingLearningCost;
	
	protected double mutationRate;
	protected double inventionProbability;

	public YamauchiHashimoto2010(){
	}
	
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);
		
		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		for (int i = 0; i < NUMBER_OF_MEANINGS; i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomBoolean()?0:1);
		}
		
		initializeParameters(config);
	}
	
	private void initializeParameters(NodeConfiguration config){//TODO remove

		learningResource = config.parameters.get(LEARNING_RESOURCE).getInteger();
		matchingLearningCost = config.parameters.get(LEARNING_COST_ON_MATCH).getInteger();
		nonMatchingLearningCost = config.parameters.get(LEARNING_COST_ON_MISMATCH).getInteger();
		
		mutationRate = config.parameters.get(MUTATION_RATE).getDouble();
		inventionProbability = config.parameters.get(INVENTION_PROBABILITY).getDouble();
		
	}
	
	@Override
	public void initializeAgent(PopulationNode parentA, PopulationNode parentB, int id, RandomGenerator randomGenerator){
		
		YamauchiHashimoto2010 parent1 = (YamauchiHashimoto2010)parentA;
		YamauchiHashimoto2010 parent2 = (YamauchiHashimoto2010)parentB;
		
		super.initializeAgent(parent1.getConfiguration(),id,randomGenerator);
		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);

		initializeParameters(config);
		
		//Crossover
		int crossoverPoint = randomGenerator.randomInt(NUMBER_OF_MEANINGS);
		int i = 0;
		while(i < crossoverPoint){
			chromosome.add(parent1.chromosome.get(i));
			i++;
		}
		while(i < NUMBER_OF_MEANINGS){
			chromosome.add(parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < NUMBER_OF_MEANINGS; j++){
			if(randomGenerator.random() < mutationRate){
				chromosome.set(j, randomGenerator.randomBoolean()?0:1);
			}
		}
	}
	
	@Override
	public String getName(){
		return "Yamauchi & Hashimoto 2010 Agent";
	}
	
	/**
	 * Returns a random utterance from this agents grammar.
	 * 
	 * @return
	 */
	@Override
	public Utterance getRandomUtterance() {
		int index = randomGenerator.randomInt(chromosome.size());
		Integer value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	/**
	 * Use the remainder of the learning resource to potentially invent parts of the grammar.
	 * The agent has a probability of 0.01 to turn an empty value to a 0 or a 1   
	 */
	@Override
	public void invent() {
		
		while(grammar.contains(Utterance.SIGNAL_NULL_VALUE) && learningResource > 0){
			
			learningResource--;
			if(randomGenerator.random() < inventionProbability){
				
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
			if(learningResource < matchingLearningCost){
				learningResource = 0;
				return;
			}
			
			grammar.set(u.meaning, u.signal);
			learningResource -= matchingLearningCost;
			
		}else{//Doesn't match this agents UG
			//TODO what do we do if we can't afford this anymore? Check with jimmy
			if(learningResource < nonMatchingLearningCost){
				learningResource = 0;
				return;
			}
			
			grammar.set(u.meaning, u.signal);
			learningResource -= nonMatchingLearningCost;
			
		}
		
	}
	
	@Override
	public void print(){
		
		System.out.println("Agent " + getId() + " has fitness of " + getFitness() + " has learning resource of " + learningResource + " fitness of  " + getFitness() +" match " + geneGrammarMatch());
		System.out.println("P" + grammar);
		System.out.println("G" + chromosome);
		
	}

	@Override
	public boolean canStillLearn() {
		return learningResource > 0;
	}
	
	@Override
	public double geneGrammarMatch(){
		
		int count = 0;
		
		for(int i = 0; i < NUMBER_OF_MEANINGS; i++){
			if(chromosome.get(i).equals(grammar.get(i))){
				count++;
			}
		}
		
		return count;
	}

	@Override
	public int learningIntensity() {
		// TODO Some better more general way of measuring this...
		return learningResource;
	}
	
	
	@Override
	public ArrayList<Integer> getGenotype() {
		return chromosome;
	}
	
	@Override//TODO this should just choose a color
	public void draw(Dimension baseDimension, VisualizationType type, Graphics g){
		
		Color c;
		
		if(config.parameters.get(VISUALIZATION_TYPE).getString().equals("numberNulls")){
			int numberOfNulls = numberOfNulls();
			c = new Color(255, 255-numberOfNulls*16, 255-numberOfNulls*16);
		}else if (config.parameters.get(VISUALIZATION_TYPE).getString().equals("genotype")){
			c = new Color(
					Math.abs(chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16),
					Math.abs(chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16),
					Math.abs(chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)
			);
		}else if (config.parameters.get(VISUALIZATION_TYPE).getString().equals("phenotype")){
			c = new Color(
					Math.abs(grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16),
					Math.abs(grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16),
					Math.abs(grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)
					);
		} else if (config.parameters.get(VISUALIZATION_TYPE).getString().equals("singleWord")) {
		
			if(grammar.get(0) == 0){
				c = Color.WHITE;
			} else if (grammar.get(0) == 1){
				c = Color.BLACK;
			} else{
				c = Color.RED;
			}
			
		}	 else if (config.parameters.get(VISUALIZATION_TYPE).getString().equals("singleGene")) {
		
			if(chromosome.get(0) == 0){
				c = Color.WHITE;
			} else if (chromosome.get(0) == 1){
				c = Color.BLACK;
			} else{
				c = Color.RED;
			}
			
		}			else {
			System.out.println("Unrecognized visualization type");
			return;
		}

		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
		
	}

}