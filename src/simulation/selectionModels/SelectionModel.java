package simulation.selectionModels;

import java.util.ArrayList;

import nodes.Node;


import simulation.RandomGenerator;

public abstract class SelectionModel {

	public enum SelectionModels {RouletteWheelSelection, TruncationSelection, ConstantProbability, RandomProbability}
	
	protected RandomGenerator randomGenerator;
	
	public void intialize(RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
	
	public abstract ArrayList<Node> select(ArrayList<Node> nodes, int number);
	
	public static SelectionModel constructSelectionModel(SelectionModels type, RandomGenerator randomGenerator){
		SelectionModel retVal;
		
		switch (type) {
		case RouletteWheelSelection:
			retVal = new RouletteWheelSelectionModel();
			break;
			
		case TruncationSelection:
			retVal = new TruncationSelectionModel();
			break;
			
		case RandomProbability:
			retVal = new RandomProbabilitySelectionModel();
			break;

		default:
			System.err.println("Unknown Selection Model, defaulting to constant probability.");
		case ConstantProbability:
			retVal = new ConstantProbabilitySelectionModel();
			break;
		}
		
		retVal.intialize(randomGenerator);
		
		return retVal;
	}
}
