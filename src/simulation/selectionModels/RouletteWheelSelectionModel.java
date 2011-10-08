package simulation.selectionModels;

import java.util.ArrayList;

import nodes.Node;

public class RouletteWheelSelectionModel extends SelectionModel {
	
	@Override
	public ArrayList<Node> select(ArrayList<Node> nodes, int toSelect){

		ArrayList<Node> toReturn = new ArrayList<Node>();

		//Calculate total fitness of all agents.
		double totalFitness = 0;
		for(Node agent : nodes){
			totalFitness += agent.getFitness();
		}

		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){
			double selectionPoint = randomGenerator.random()*totalFitness;
			double pointer = 0;

			for(Node node : nodes){//TODO binary search, all of the selection points in an ordered list?
				//move the pointer along to the next agents borderline
				pointer += node.getFitness();
				
				//have we gone past the selectionPoint?
				if(pointer > selectionPoint){
					toReturn.add(node);
					break;
				}
			}
		}
		
		return toReturn;
	}


}
