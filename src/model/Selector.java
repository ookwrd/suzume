package model;

import java.util.ArrayList;

public class Selector {

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
		
		Selector selector = new Selector();
		
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
