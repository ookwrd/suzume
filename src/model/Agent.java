package model;
import java.util.ArrayList;
import java.util.Random;

import model.World.Allele;



public class Agent {
	
	private static final int LEARNING_RESOURCE = 24;
	private static final int FITNESS = 1;
	private static final int CHROMOSOME_SIZE = 12;
	
	public ArrayList<Allele> chromosome;
	public ArrayList<Allele> grammar;
	
	public int learningResource;
	public int fitness;
	public int id;
	
	public Agent(int id) {
		this.id = id;
		chromosome = new ArrayList<Allele>(CHROMOSOME_SIZE);
		for (int i = 0; i < chromosome.size(); i++) { // all alleles are initially set to # i.e. the null value 
			chromosome.set(i, Allele.NULL);//TODO these should be set to 0 or 1 randomly
		}
		grammar = new ArrayList<World.Allele>(CHROMOSOME_SIZE);
		for (int i = 0; i < grammar.size(); i++){
			grammar.set(i, Allele.NULL);
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
	public Agent(Agent parent1, Agent Parent2, int id){
		
		//TODO
		
		//Crossover
		
		//Mutation
		
	}
	
	
	public Utterance getRandomUtterance() {
		int index = random(chromosome.size());
		Allele value;
		if (random()) value = Allele.ONE; else value = Allele.ZERO;
		Utterance u = new Utterance(index, value);
		return u;
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
	public static int random(int limit) {
		Random r = new Random(); //TODO extract to a new class
		return r.nextInt() % (limit+1);
	}
	
	/**
	 * Return a random boolean
	 * 
	 */
	public static boolean random() {
		return (random(1) == 0); 
	}
	
	public int getFitness(){
		return fitness;
	}
	
	public void setFitness(int fitness){
		this.fitness = fitness;
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