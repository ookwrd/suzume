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
	
	/*@Override
	public void initializeAgent(Node parentA, Node parentB, int id,
			RandomGenerator randomGenerator) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Agent> getBaseAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void teach(Node agent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void learnUtterance(Utterance utterance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void communicate(Node partner) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void finalizeFitnessValue() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Object> getStatisticsKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
