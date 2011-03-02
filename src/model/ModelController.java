package model;

import java.util.ArrayList;

public class ModelController {

	private static int POPULATION_SIZE = 200;
	private static int SELECTION_SIZE = 100; //TODO check this as a default value
	
	private PopulationModel population;
	
	public ModelController(){
		

		
		population = new OriginalPopulationModel(getIntialAgents());
		
	}
	
	private ArrayList<Agent> getIntialAgents(){
		
		ArrayList<Agent> agents = new ArrayList<Agent>();

		for (int i = 1; i <= POPULATION_SIZE; i++) {
			agents.add(new Agent(i));
		}
		
		return agents;
	}
	
	public void iterateGeneration(){
		
		training();
		
	}
	
	/**
	 * Training and invention phase.
	 */
	private void training(){
		
		for(Agent agent : population.getCurrentGeneration()){
			ArrayList<Agent> teachers = population.getAncestors(agent, 2);
			
			
		}
		
	}
	
	/**
	 * Communication Phase (calculates fitness)
	 */
	private void communication(){
		
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
