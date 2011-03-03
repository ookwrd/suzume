package model;

import java.util.ArrayList;

public class ModelController {
	
	private static final int GENERATION_COUNT = 2000; 

	private static final int POPULATION_SIZE = 200;
	private static final int SELECTION_SIZE = 100; //TODO check this as a default value
	
	private static final int BASE_FITNESS = 1;
	private static final int COMMUNICATIONS_PER_NEIGHBOUR = 6;
	
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
				
				//TODO break on extinguished learning resource.
			}
	
			//Use leftover learning resource to potentially invent new grammar items.
			learner.invent();
		}
		
	}
	
	/**
	 * Communication Phase  which calculates the fitness of all agents in the population.
	 */
	private void communication(){
		
		for(Agent agent : population.getCurrentGeneration()){
		
			//TODO this is redundant, as the affect of agent n on n+1 are symetrical. 
			//TODO also doesn't match the paper as potential for different utterances used for calculating fitness of agent n and n+1 
			
			ArrayList<Agent> neighbouringAgents = population.getNeighbors(agent, 1);
		
			//Set the agents fitness to the default base level 
			agent.fitness = BASE_FITNESS;
			
			//Communicate with all neighbours
			for(Agent neighbour : neighbouringAgents){
				
				for(int i = 0; i < COMMUNICATIONS_PER_NEIGHBOUR; i++){
					Utterance utterance = neighbour.getRandomUtterance();//TODO fix the getRandomUtterance method

					//If agent and neighbour agree update fitness.
					if(!utterance.isNull() && (agent.grammar.get(utterance.index) == utterance.value)){
						agent.fitness += 1;
					}
				}
			}
			
		}
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
			totalFitness += agent.fitness;
		}
		
		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){
		
			int selectionPoint = (int)(Math.random() * totalFitness);
			int pointer = 0;

			for(Agent agent : agents){
				//move the pointer along to the next agents borderline
				pointer += agent.fitness;
				
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
		
		//Test selection
		ModelController selector = new ModelController();
		
		for( Agent agent :selector.population.getCurrentGeneration()){
			
			for(int i = 0; i < agent.grammar.size(); i++){
				
				agent.grammar.set(i, Allele.ZERO);
				
			}
			
		}
		
		selector.communication();
		
		for(Agent agent : selector.population.getCurrentGeneration()){
			System.out.println("Agent " + agent.id + " has fitness of " + agent.fitness);
		}
		
		/*ArrayList<Agent> results = selector.select(50, agents);

		System.out.println("Size: " + results.size());
		
		for (Agent agent : results) {
			System.out.println("Agent " + agent.id);
		}*/
	}
}
