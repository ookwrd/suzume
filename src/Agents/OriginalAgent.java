package Agents;
import java.util.ArrayList;
import java.util.HashMap;

import model.RandomGenerator;


public class OriginalAgent extends AbstractAgent implements Agent {
	
	protected static final int LEARNING_RESOURCE = 24;
	protected static final int MATCHING_LEARNING_COST = 1;
	protected static final int NON_MATCHING_LEARNING_COST = 4;
	
	protected static final double MUTATION_RATE = 0.00025;
	protected static final double INVENTION_PROBABILITY = 0.01;
	
	protected ArrayList<Integer> chromosome;
	
	protected int learningResource;

	@SuppressWarnings("serial")
	public static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Learning Resource", new ConfigurationParameter(24));
		put("Learning Resource on Match", new ConfigurationParameter(1));
		put("Learning Resource on MisMatch", new ConfigurationParameter(4));
		put("Mutation Rate", new ConfigurationParameter(0.00025));
		put("Invention Probability", new ConfigurationParameter(0.01));
	}};
	
	protected RandomGenerator randomGenerator;
	
	public OriginalAgent(){
		
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
	public void initializeAgent(AgentConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);//TODO push randomGenerator down
		
		this.randomGenerator = randomGenerator;
		
		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		for (int i = 0; i < NUMBER_OF_MEANINGS; i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomBoolean()?0:1);
		}
		learningResource = LEARNING_RESOURCE;
	}
	
	public void initializeAgent(Agent parentA, Agent parentB, int id, RandomGenerator randomGenerator){
		OriginalAgent parent1 = (OriginalAgent)parentA;
		OriginalAgent parent2 = (OriginalAgent)parentB;
		
		super.initializeAgent(parent1.getConfiguration(),id,randomGenerator);
		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		learningResource = LEARNING_RESOURCE;
		
		this.randomGenerator = randomGenerator;
		
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
			if(randomGenerator.random() < MUTATION_RATE){
				chromosome.set(j, randomGenerator.randomBoolean()?0:1);
			}
		}
	}
	
	@Override
	public String getName(){
		return "Original Agent";
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
			if(randomGenerator.random() < INVENTION_PROBABILITY){
				
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
			grammar.set(u.meaning, u.signal);
			learningResource -= MATCHING_LEARNING_COST;
		}else{//Doesn't match this agents UG
			//TODO what do we do if we can't afford this anymore? Check with jimmy
			if(learningResource < NON_MATCHING_LEARNING_COST){
				learningResource = 0;
				return;
			}
			
			grammar.set(u.meaning, u.signal);
			learningResource -= NON_MATCHING_LEARNING_COST;
		}
		
	}
	
	@Override
	public void printAgent(){
		
		System.out.println("Agent " + getId() + " has fitness of " + getFitness() );
		System.out.println(grammar);
		System.out.println(chromosome);
		
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
	public ArrayList getChromosome() {
		return chromosome;
	}

}