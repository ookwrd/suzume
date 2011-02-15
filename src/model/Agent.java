package model;
import java.util.ArrayList;
import java.util.Random;

import model.World.Allele;



public class Agent {
	
	private static final int DEFAULT_LEARNING_RESOURCE = 24;
	private static final int DEFAULT_FITNESS = 1;
	
	public ArrayList<Allele> chromosome;
	public int learningResource;
	public int fitness;
	public int id;
	//public ILanguage;
	
	public Agent(int id) {
		this.id = id;
		chromosome = new ArrayList<Allele>();
		for (int i = 0; i < chromosome.size(); i++) { // all alleles are initially set to # i.e. the null value 
			chromosome.set(i, Allele.NULL);
		}	
		learningResource = DEFAULT_LEARNING_RESOURCE;
		fitness = DEFAULT_FITNESS;
	}
	
	
	public Utterance utter() {
		int index = random(chromosome.size());
		Allele value;
		if (random()) value = Allele.ONE; else value = Allele.ZERO;
		Utterance u = new Utterance(index, value);
		return u;
	}
	
	/**
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