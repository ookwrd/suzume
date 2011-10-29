package simulation.selectionModels;

import java.util.ArrayList;

import nodes.Node;

public class RandomProbabilitySelectionModel extends
		ConstantProbabilitySelectionModel {
	
	public static final int lowerFitness = 1;
	public static final int upperFitness = 13;
	

	public ArrayList<Node> select(ArrayList<Node> nodes, int number){
		
		ArrayList<Integer> fitnessValues = new ArrayList<Integer>();
		double totalFitness = 0;
		for(int i = 0; i< nodes.size(); i++){
			int newInt = lowerFitness + randomGenerator.nextInt(upperFitness-lowerFitness);
			fitnessValues.add(newInt);
			totalFitness += newInt;
		}
		
		ArrayList<Node> toReturn = new ArrayList<Node>();
		for(int i = 0; i < number; i++){
			double selectionPoint = randomGenerator.nextDouble()*totalFitness;
			double pointer = 0;

			for(int j = 0; j < nodes.size(); j++){
				//move the pointer along to the next agents borderline
				pointer += fitnessValues.get(j);
				
				//have we gone past the selectionPoint?
				if(pointer > selectionPoint){
					toReturn.add(nodes.get(j));
					break;
				}
			}
		}
		
		return super.select(nodes, number);
	}

}
