package model;
import java.util.ArrayList;
import java.util.Random;

import model.ModelController.Allele;




public class Agent {
	
	private static final int LEARNING_RESOURCE = 24;
	private static final int FITNESS = 1;
	private static final int CHROMOSOME_SIZE = 12;
	
	private static final double MUTATION_RATE = 0.00025;
	
	public ArrayList<Allele> chromosome;
	public ArrayList<Allele> grammar;
	
	public int learningResource;
	public int fitness;
	public int id;
	
	public Agent(int id) {
		this.id = id;
		chromosome = new ArrayList<Allele>(CHROMOSOME_SIZE);
		for (int i = 0; i < CHROMOSOME_SIZE; i++) { // all alleles are initially set to a random value initially
			chromosome.add(random()?Allele.ZERO:Allele.ONE);
		}
		grammar = new ArrayList<Allele>(CHROMOSOME_SIZE);
		for (int i = 0; i < CHROMOSOME_SIZE; i++){
			grammar.add(/*Allele.NULL*/chromosome.get(i));//TODO should set blank
		}
		learningResource = LEARNING_RESOURCE;
		fitness = FITNESS;
	}
	
	/**
	 * Sexual reproduction of a new agent.
	 * 
	 * @param parent1
	 * @param Parent2
	 * @param id
	 */
	public Agent(Agent parent1, Agent parent2, int id){
		
		//TODO
		this.id = id;
		chromosome = new ArrayList<Allele>(CHROMOSOME_SIZE);
		learningResource = LEARNING_RESOURCE;
		fitness = FITNESS;
		
		//Crossover
		int crossoverPoint = (int)(Math.random()*CHROMOSOME_SIZE);
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
			grammar.add(/*Allele.NULL*/chromosome.get(j));//TODO should set blank
		}
		
		//Mutation
		for(int j = 0; j < CHROMOSOME_SIZE; j++){
			if(Math.random() < MUTATION_RATE){
				chromosome.set(j, random()?Allele.ZERO:Allele.ONE);
			}
		}
	}
	
	/**
	 * Returns a random utterance from this agents grammar.
	 * 
	 * @return
	 */
	public Utterance getRandomUtterance() {
		int index = random(chromosome.size() - 1);
		Allele value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	/**
	 * Use the remainder of the learning resource to potentially invent parts of the grammar.
	 * The agent has a probability of 0.01 to turn an empty value to a 0 or a 1   
	 */
	public void invent() {
		//TODO
	}
	
	/**
	 * The agent teaches an agent an utterance
	 * 
	 * @param learner the agent being taught
	 */
	public void teach(Agent learner) {
		//TODO
	}
	
	/**
	 * The agent learns an utterance 
	 * The learning resource is updated
	 * 
	 * @param teacher the agent teaching
	 * @param utterance the utterance taught
	 */
	public void learnUtterance(Utterance u) {
		//TODO
	}
	
	/**
	 * Return a random integer r such that 0 <= r <= limit
	 * 
	 * @param limit
	 */
	public static int random(int limit) { //TODO clean this up.
		Random r = new Random(); //TODO extract to a new class
		return Math.abs(r.nextInt() % (limit+1));
	}
	
	/**
	 * Return a random boolean
	 * 
	 */
	public static boolean random() {
		return (random(1) == 0); 
	}
	
	public static void main(String[] args) {
		
		// quick test for random()
		double a = 0;
		double b = 0;
		for (int i = 0; i < 20000000; i++) {
			if (random()) a++; else b++;
		}
		System.out.println(a/10000000+" -- "+b/10000000);
		
	}
	
}