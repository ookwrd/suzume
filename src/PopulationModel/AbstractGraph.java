package PopulationModel;

import java.util.ArrayList;

import simulation.RandomGenerator;

public abstract class AbstractGraph extends GraphConfiguration implements Graph {

	protected ArrayList<Node> subNodes;
	
	@Override
	public void init(ArrayList<Node> subNodes, GraphConfiguration config, RandomGenerator randomGenerator){
		super.initialize(config);
		this.subNodes = subNodes; 
	}
	
	@Override
	public void resetSubNodes(ArrayList<Node> subNodes){
		this.subNodes = subNodes;
	}
	
	@Override
	public ArrayList<Node> getNodeSet(){
		return subNodes;
	}

	@Override
	public GraphConfiguration getConfiguration() {
		return this;
	}
	
	/**
	 * Override this implementation to create a directed graph.
	 */
	@Override
	public ArrayList<Node> getOutNodes(int index){
		return getInNodes(index);
	}
	
}
