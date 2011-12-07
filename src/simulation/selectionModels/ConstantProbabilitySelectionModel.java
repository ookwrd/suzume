package simulation.selectionModels;

import java.util.ArrayList;

import nodes.Node;

public class ConstantProbabilitySelectionModel extends SelectionModel {

	@Override
	public ArrayList<Node> select(ArrayList<Node> nodes, int number) {
		
		ArrayList<Node> toReturn = new ArrayList<Node>();
		int inputSize = nodes.size();
		while(toReturn.size() < number){
			toReturn.add(nodes.get(randomGenerator.nextInt(inputSize)));
		}
		return toReturn;
	}

}
