package simulation.selectionModels;

import java.util.ArrayList;

import nodes.Agents.Agent;

import PopulationModel.Node;


public class RouletteWheelSelectionModel extends SelectionModel {

	
	@Override
	public ArrayList<Node> selectAgents(ArrayList<Node> agents, int toSelect){

		ArrayList<Node> toReturn = new ArrayList<Node>();

		//Calculate total fitness of all agents.
		int totalFitness = 0;
		for(Node agent : agents){
			totalFitness += ((Agent)agent).getFitness();
		}

		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){

			int selectionPoint = randomGenerator.randomInt(totalFitness);
			int pointer = 0;

			for(Node agent : agents){//TODO binary search, all of the selection points in an ordered list?
				//move the pointer along to the next agents borderline
				pointer += ((Agent)agent).getFitness();

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
