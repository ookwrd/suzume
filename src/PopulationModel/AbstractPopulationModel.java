package PopulationModel;

import java.util.ArrayList;

import simulation.RandomGenerator;

import autoconfiguration.Configurable;

import nodes.AbstractNode;
import nodes.Node;
import nodes.Agents.Agent;

public abstract class AbstractPopulationModel extends AbstractNode implements PopulationModel {

	protected ArrayList<Node> currentGeneration = new ArrayList<Node>();
	
	public AbstractPopulationModel(){}
	
	public AbstractPopulationModel(Configurable config, RandomGenerator random){
		super(config, random);
	}
	
	@Override
	public ArrayList<Agent> getBaseAgents() {	
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
