package simulation.selectionModels;

import java.util.ArrayList;

import PopulationModel.Node;

import populationNodes.Agents.Agent;


public class ConstantProbabilitySelectionModel extends SelectionModel {

	@Override
	public ArrayList<Node> selectAgents(ArrayList<Node> agents, int number) {
		
		ArrayList<Node> toReturn = new ArrayList<Node>();
		int inputSize = agents.size();
		while(toReturn.size() < number){
			toReturn.add(agents.get(randomGenerator.randomInt(inputSize)));
		}
		
		return toReturn;
	}

}
