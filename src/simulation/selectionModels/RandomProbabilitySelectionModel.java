package simulation.selectionModels;

import java.util.ArrayList;

import nodes.Agents.Agent;

import PopulationModel.Node;


public class RandomProbabilitySelectionModel extends
		ConstantProbabilitySelectionModel {
	
	public static final int lowerFitness = 1;
	public static final int upperFitness = 13;
	

	public ArrayList<Node> selectAgents(ArrayList<Node> agents, int number){
		
		for(Node agent : agents){
			((Agent)agent).setFitness(lowerFitness + randomGenerator.randomInt(upperFitness-lowerFitness));
		}
		
		return super.selectAgents(agents, number);
	}

}
