package simulation.selectionModels;

import java.util.ArrayList;

import populationNodes.Agents.Agent;

public class RandomProbabilitySelectionModel extends
		ConstantProbabilitySelectionModel {
	
	public static final int lowerFitness = 1;
	public static final int upperFitness = 13;
	

	public ArrayList<Agent> selectAgents(ArrayList<Agent> agents, int number){
		
		for(Agent agent : agents){
			agent.setFitness(lowerFitness + randomGenerator.randomInt(upperFitness-lowerFitness));
		}
		
		return super.selectAgents(agents, number);
	}

}
