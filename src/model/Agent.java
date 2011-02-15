package model;
import java.util.ArrayList;

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
		for (int i = 0; i < chromosome.size(); i++) {
			chromosome.set(i, Allele.NULL);
		}	
		learningResource = DEFAULT_LEARNING_RESOURCE;
		fitness = DEFAULT_FITNESS;
	}
	
	public Utterance utter() {
		Utterance u = new Utterance();
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
	
	
}