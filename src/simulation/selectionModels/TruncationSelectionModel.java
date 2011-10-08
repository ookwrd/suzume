package simulation.selectionModels;

import java.util.ArrayList;
import java.util.Arrays;

import nodes.Node;

public class TruncationSelectionModel extends SelectionModel {

	//TODO make configurable
	private static final double selectionIntensity = 0.5;
	
	/**
	 * Not quite truncation selection, cuts off everything less than the fitness of the agent 
	 * at the selectionIntensity point. May cut off less agents than expected if many agents have
	 * the same fitness value.
	 */
	
	@Override
	public ArrayList<Node> select(ArrayList<Node> nodes, int number) {
		
		double[] fitnesses = new double[nodes.size()];
		for(int i = 0; i < fitnesses.length; i++){
			fitnesses[i] = nodes.get(i).getFitness();
		}
		Arrays.sort(fitnesses);
		
		double cutOff = fitnesses[fitnesses.length - (int)(selectionIntensity*nodes.size())];
		
		ArrayList<Node> retVal = new ArrayList<Node>();
		for(Node node : nodes){
			if(node.getFitness() >= cutOff){
				retVal.add(node);
			}
		}
		return retVal;
	}

}
