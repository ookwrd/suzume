package model;

import java.util.ArrayList;

public class ModelController {
	
	private static final int GENERATION_COUNT = 2000; 

	private static final int POPULATION_SIZE = 200;
	private static final int SELECTION_SIZE = 100; //TODO check this as a default value
	
	private static final int BASE_FITNESS = 1;
	
	private static final int CRITICAL_PERIOD = 200; //Number of utterances available to learners
	
	public enum Allele {
		ZERO, ONE, NULL;
	}
	
	private int nextAgentID = 0; // keeps count of all the next agents from this world
	
	private PopulationModel population;
	
	public ModelController(){
		

		
		population = new OriginalPopulationModel(getIntialAgents());
		
	}
	
	private ArrayList<Agent> getIntialAgents(){
		
		ArrayList<Agent> agents = new ArrayList<Agent>();

		for (int i = 1; i <= POPULATION_SIZE; i++) {
			agents.add(getRandomAgent());
		}
		
		return agents;
	}
	
	private Agent getRandomAgent(){
		return new Agent(nextAgentID++);
	}
	
	public void runSimulation(){
		
		for(int i = 0; i < GENERATION_COUNT; i++){
			
			iterateGeneration();
			
		}
	}
	
	/**
	 * Runs a single round of the simulation.
	 * 
	 */
	private void iterateGeneration(){
		
		training();
		communication();
		
		population.switchGenerations(selection());
		
	}
	
	/**
	 * Training and invention phase of a single round of the simulation.
	 */
	private void training(){
		
		for(Agent learner : population.getCurrentGeneration()){
			ArrayList<Agent> teachers = population.getAncestors(learner, 2);
			
			for(int i = 0; i < CRITICAL_PERIOD; i++){
				
				//Get random teacher
				Agent teacher = teachers.get((int)(Math.random()*teachers.size()));
				
				teacher.teach(learner);
			}
			
			learner.invent();
		}
		
	}
	
	/**
	 * Communication Phase  which calculates the fitness of all agents in the population.
	 */
	private void communication(){
		
		for(Agent agent : population.getCurrentGeneration()){
			ArrayList<Agent> neighboursAgents = population.getNeighbors(agent, 2);
		
			//Set the agents fitness to the default base level 
			agent.fitness = BASE_FITNESS;
			
			
		}
	}
	
	public void communicate(Agent agent1, Agent agent2) {
		//TODO
		
		//benefits both speaker and reciever... 
	}
	
	/**
	 * Selection and construction of the new generation.
	 * 
	 * @return
	 */
	private ArrayList<Agent> selection(){
		
		ArrayList<Agent> selected = select(SELECTION_SIZE, population.getCurrentGeneration());
		
		ArrayList<Agent> newGenerationAgents = new ArrayList<Agent>();
		
		
		
		return null;
	}
	
	/**
	 * TODO optimize this class
	 * TODO allow ability to disable the selection of multiples
	 * 
	 * @param toSelect
	 * @param agents
	 * @return
	 */
	public ArrayList<Agent> select(int toSelect, ArrayList<Agent> agents){
		
		ArrayList<Agent> toReturn = new ArrayList<Agent>();
		
		//Calculate total fitness of all agents.
		int totalFitness = 0;
		for(Agent agent : agents){
			totalFitness += agent.getFitness();
		}
		
		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){
		
			int selectionPoint = (int)(Math.random() * totalFitness);
			int pointer = 0;

			for(Agent agent : agents){
				//move the pointer along to the next agents borderline
				pointer += agent.getFitness();
				
				//have we gone past the selectionPoint?
				if(pointer > selectionPoint){
					toReturn.add(agent);
					break;
				}
			}
		}
		
		return toReturn;
	}
	
	public static void main(String[] args){
		
		ModelController selector = new ModelController();
		
		ArrayList<Agent> agents = new ArrayList<Agent>();

		for (int i = 1; i <= 200; i++) {
			Agent toAdd = new Agent(i);
			toAdd.setFitness(10);
			agents.add(toAdd);
		}
		
		ArrayList<Agent> results = selector.select(50, agents);

		System.out.println("Size: " + results.size());
		
		for (Agent agent : results) {
			System.out.println("Agent " + agent.id);
		}
	}
}
