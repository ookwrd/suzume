package model;
import java.util.ArrayList;
import java.util.Random;
import model.ModelController.Allele;

public class Agent {
	
	private static final int LEARNING_RESOURCE = 24;
	private static final int MATCHING_LEARNING_COST = 1;
	private static final int NON_MATCHING_LEARNING_COST = 4;
	
	private static final int CHROMOSOME_SIZE = 12;
	
	private static final double MUTATION_RATE = 0.00025;
	private static final double INVENTION_PROBABILITY = 0.01;
	
	public ArrayList<Allele> chromosome;
	public ArrayList<Allele> grammar;
	
	public int learningResource;
	public int fitness;
	public int id;
	
	private RandomGenerator randomGenerator = RandomGenerator.getGenerator();
	
	public Agent(int id) {
		this.id = id;
		chromosome = new ArrayList<Allele>(CHROMOSOME_SIZE);
		for (int i = 0; i < CHROMOSOME_SIZE; i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomBoolean()?Allele.ZERO:Allele.ONE);
		}
		grammar = new ArrayList<Allele>(CHROMOSOME_SIZE);
		for (int i = 0; i < CHROMOSOME_SIZE; i++){
			grammar.add(Allele.NULL);
		}
		learningResource = LEARNING_RESOURCE;
		fitness = 0;
	}
	
	/**
	 * Sexual reproduction of a new agent.
	 * 
	 * @param parent1
	 * @param Parent2
	 * @param id
	 */
	public Agent(Agent parent1, Agent parent2, int id){
		
		this.id = id;
		chromosome = new ArrayList<Allele>(CHROMOSOME_SIZE);
		learningResource = LEARNING_RESOURCE;
		fitness = 0;
		
		//Crossover
		int crossoverPoint = randomGenerator.randomInt(CHROMOSOME_SIZE);
		int i = 0;
		while(i < crossoverPoint){
			chromosome.add(parent1.chromosome.get(i));
			i++;
		}
		while(i < CHROMOSOME_SIZE){
			chromosome.add(parent2.chromosome.get(i));
			i++;
		}
		
		grammar = new ArrayList<Allele>(CHROMOSOME_SIZE);
		for (int j = 0; j < CHROMOSOME_SIZE; j++){
			grammar.add(Allele.NULL);
		}
		
		//Mutation
		for(int j = 0; j < CHROMOSOME_SIZE; j++){
			if(randomGenerator.random() < MUTATION_RATE){
				chromosome.set(j, randomGenerator.randomBoolean()?Allele.ZERO:Allele.ONE);
			}
		}
	}
	
	/**
	 * Returns a random utterance from this agents grammar.
	 * 
	 * @return
	 */
	public Utterance getRandomUtterance() {
		int index = randomGenerator.randomInt(chromosome.size());
		Allele value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	/**
	 * Use the remainder of the learning resource to potentially invent parts of the grammar.
	 * The agent has a probability of 0.01 to turn an empty value to a 0 or a 1   
	 */
	public void invent() {
		
		while(grammar.contains(Allele.NULL) && learningResource > 0){
			
			learningResource--;
			if(randomGenerator.random() < INVENTION_PROBABILITY){
				
				//Collect indexes of all null elements
				ArrayList<Integer> nullIndexes = new ArrayList<Integer>();
				for(int i = 0; i < grammar.size(); i++){
					
					Allele allele = grammar.get(i);
					if(allele == Allele.NULL){
						nullIndexes.add(i);
					}
				}
				
				//Choose a random null element to invent a new value for
				Integer index = nullIndexes.get(randomGenerator.randomInt(nullIndexes.size()));
				
				grammar.set(index, randomGenerator.randomBoolean()?Allele.ZERO:Allele.ONE);
			}
		}
	}
	
	/**
	 * The agent teaches an agent an utterance
	 * 
	 * @param learner the agent being taught
	 */
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
	public void learnUtterance(Utterance u) {
		
		//agents agree on value or NULL utterance
		if(u.value == grammar.get(u.index) || u.value == Allele.NULL){
			return;
		}
		
		if(u.value == chromosome.get(u.index)){//Matches this agents UG
			grammar.set(u.index, u.value);
			learningResource -= MATCHING_LEARNING_COST;
		}else{//Doesn't match this agents UG
			//TODO what do we do if we can't afford this anymore? Check with jimmy
			if(learningResource < NON_MATCHING_LEARNING_COST){
				learningResource = 0;
				return;
			}
			
			grammar.set(u.index, u.value);
			learningResource -= NON_MATCHING_LEARNING_COST;
		}
		
	}
}