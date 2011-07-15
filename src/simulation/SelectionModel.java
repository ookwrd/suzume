package simulation;

import java.util.ArrayList;

import populationNodes.Agent;

public abstract class SelectionModel {

	public enum SelectionModels {RouletteWheelSelection, ConstantProbabilitySelection, RandomProbabilitySelection}
	
	protected RandomGenerator randomGenerator;
	
	public void intialize(RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
	
	public abstract ArrayList<Agent> selectAgents(ArrayList<Agent> agents, int number);
	
	public static SelectionModel constructSelectionModel(SelectionModels type, RandomGenerator randomGenerator){
		SelectionModel retVal;
		
		switch (type) {
		case RouletteWheelSelection:

			retVal = new RouletteWheelSelectionModel();
			break;
			
		case RandomProbabilitySelection:
			
			retVal = new RandomProbabilitySelectionModel();
			break;
			
		case ConstantProbabilitySelection:
		default:
			
			retVal = new ConstantProbabilitySelectionModel();
			break;
		}
		
		retVal.intialize(randomGenerator);
		
		return retVal;
	}
}
