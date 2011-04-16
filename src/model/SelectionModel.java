package model;

import java.util.ArrayList;

import org.apache.commons.collections15.map.CaseInsensitiveMap;

import Agents.Agent;

public abstract class SelectionModel {

	public enum SelectionModels {RouletteWheelSelectionModel, ConstantProbabilitySelectionModel}
	
	protected RandomGenerator randomGenerator;
	
	public void intialize(RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
	
	public abstract ArrayList<Agent> selectAgents(ArrayList<Agent> agents, int number);
	
	public static SelectionModel constructSelectionModel(SelectionModels type, RandomGenerator randomGenerator){
		SelectionModel retVal;
		
		switch (type) {
		case RouletteWheelSelectionModel:

			retVal = new RouletteWheelSelectionModel();
			break;
			
		case ConstantProbabilitySelectionModel:
		default:
			retVal = new ConstantProbabilitySelectionModel();
			break;
		}
		
		retVal.intialize(randomGenerator);
		
		return retVal;
	}
}
