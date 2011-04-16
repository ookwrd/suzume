package model;

import java.util.ArrayList;

import Agents.Agent;

public class ConstantProbabilitySelectionModel extends SelectionModel {

	@Override
	public ArrayList<Agent> selectAgents(ArrayList<Agent> agents, int number) {
		
		ArrayList<Agent> toReturn = new ArrayList<Agent>();
		int inputSize = agents.size();
		while(toReturn.size() < number){
			toReturn.add(agents.get(randomGenerator.randomInt(inputSize)));
		}
		
		return toReturn;
	}

}
