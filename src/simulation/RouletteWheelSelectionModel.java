package simulation;

import java.util.ArrayList;

import Agents.Agent;

public class RouletteWheelSelectionModel extends SelectionModel {

	
	@Override
	public ArrayList<Agent> selectAgents(ArrayList<Agent> agents, int toSelect){

		ArrayList<Agent> toReturn = new ArrayList<Agent>();

		//Calculate total fitness of all agents.
		int totalFitness = 0;
		for(Agent agent : agents){
			totalFitness += agent.getFitness();
		}

		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){

			int selectionPoint = randomGenerator.randomInt(totalFitness);
			int pointer = 0;

			for(Agent agent : agents){//TODO binary search, all of the selection points in an ordered list?
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


}
