package model;

import java.util.ArrayList;

public class World {
	
	private int nextAgentID = 0; // keeps count of all the next agents from this world
	public PopulationModel populationModel;
	
	public enum Allele {
		ZERO, ONE, NULL;
	}
	
	// methods
	
	
	public void populate(int size) {
		ArrayList<Agent> initialPopulation = new ArrayList<Agent>(size);
		for (int i = 0; i < size ; i++) {
			initialPopulation.add(createAgent());
		}
		populationModel = new OriginalPopulationModel(initialPopulation);
	}
	
	public void runLearning() {
		
		ArrayList<Agent> currentPopulation = populationModel.getCurrentGeneration();
		/*for (Agent agent : currentPopulation) {
			for (int i = 0; i < 5; i++) {
				Agent ancestor = populationModel.getRandomAgentFrom(populationModel.getAncestors(agent));
			}
		}*/
		
		
	}
	
	public void learn(Agent teacher, Agent learner) {
		//TODO
	}
	
	public void communicate(Agent agent1, Agent agent2) {
		//TODO
	}
	
	public void reproduce() {
		//TODO
	}
	
	/**
	 * Return the next agent ID 
	 * 
	 * @return
	 */
	private int getNextAgentID() {
		return nextAgentID;
	}
	
	/**
	 * Return a new instance of an agent in this world, and increment nextAgentID
	 * 
	 * @return
	 */
	private Agent createAgent() {
		return new Agent(nextAgentID++);
	}
	
	/**
	 * Return n agents at random from a list of agents
	 * 
	 * @param agents
	 * @return
	 */
	public static ArrayList<Agent> getRandomAgentFrom(ArrayList<Agent> agents) {
		ArrayList<Agent> retAgents = new ArrayList();
		
		//for
		//	agents.get
		return agents;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Hello Evolution!");
	}

}
