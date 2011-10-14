package PopulationModel;

import java.util.ArrayList;

import nodes.AbstractNode;
import nodes.Node;
import nodes.Agents.Agent;

public abstract class AbstractPopulationModel extends AbstractNode implements PopulationModel {

	protected ArrayList<Node> currentGeneration = new ArrayList<Node>();
	
	@Override
	public ArrayList<Agent> getCurrentGeneration() {	
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		for(Node node : currentGeneration){
			retAgents.addAll(node.getBaseAgents());
		}
		return retAgents;
	}

	@Override
	public double getFitness() {
		double count = 0;
		for(Node node : currentGeneration){
			count += node.getFitness();
		}
		return count;
	}

	@Override
	public void finalizeFitnessValue() {
		for(Node node : currentGeneration){
			node.finalizeFitnessValue();
		}
	}

}
