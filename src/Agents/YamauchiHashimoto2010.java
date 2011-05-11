package Agents;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import model.RandomGenerator;


public class YamauchiHashimoto2010 extends AbstractAgent implements Agent {
	
	protected static String[] visualizationTypes = {"numberNulls", "genotype", "phenotype"};
	
	protected ArrayList<Integer> chromosome;
	
	protected int learningResource;
	protected int matchingLearningCost;
	protected int nonMatchingLearningCost;
	
	protected double mutationRate;
	protected double inventionProbability;

	@SuppressWarnings("serial")
	private static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Learning Resource", new ConfigurationParameter(24));
		put("Learning Resource on Match", new ConfigurationParameter(1));
		put("Learning Resource on MisMatch", new ConfigurationParameter(4));
		put("Mutation Rate", new ConfigurationParameter(0.00025));
		put("Invention Probability", new ConfigurationParameter(0.01));
		put("Visualization Type", new ConfigurationParameter(visualizationTypes));
	}};
	
	public YamauchiHashimoto2010(){
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
	public void initializeAgent(AgentConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);
		
		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		for (int i = 0; i < NUMBER_OF_MEANINGS; i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomBoolean()?0:1);
		}
		
		initializeParameters(config);
	}
	
	private void initializeParameters(AgentConfiguration config){

		learningResource = config.parameters.get("Learning Resource").getInteger();
		matchingLearningCost = config.parameters.get("Learning Resource on Match").getInteger();
		nonMatchingLearningCost = config.parameters.get("Learning Resource on MisMatch").getInteger();
		
		mutationRate = config.parameters.get("Mutation Rate").getDouble();
		inventionProbability = config.parameters.get("Invention Probability").getDouble();
		
	}
	
	public void initializeAgent(Agent parentA, Agent parentB, int id, RandomGenerator randomGenerator){
		
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
	 * The agent teaches an agent an utterance
	 * 
	 * @param learner the agent being taught
	 */
	@Override
	public void teach(Agent learner) {
		learner.learnUtterance(getRandomUtterance());
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
	public void printAgent(){
		
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
	
	@Override
	public void draw(Dimension baseDimension, Graphics g){
		
		Color c;
		
		if(config.parameters.get("Visualization Type").getString().equals("numberNulls")){
			int numberOfNulls = numberOfNulls();
			c = new Color(255, 255-numberOfNulls*16, 255-numberOfNulls*16);
		}else if (config.parameters.get("Visualization Type").getString().equals("genotype")){
			c = new Color(
					Math.abs(chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16),
					Math.abs(chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16),
					Math.abs(chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)
			);
		}else if (config.parameters.get("Visualization Type").getString().equals("phenotype")){
			c = new Color(
					Math.abs(grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16),
					Math.abs(grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16),
					Math.abs(grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)
					);
		} else {
			System.out.println("Unrecognized visualization type");
			return;
		}

		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
		
	}

}