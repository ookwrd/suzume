package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.BasicConfigurable;

public abstract class AbstractGraph extends GraphConfiguration implements Graph {

	protected ArrayList<Node> populations;
	protected GraphConfiguration config;
	
	@Override
	public void init(ArrayList<Node> populations, GraphConfiguration config){
		this.config = config;
		this.populations = populations; 
	}
	
	@Override
	public ArrayList<Node> getNodeSet(){
		return populations;
	}

	@Override
	public GraphConfiguration getConfiguration() {
		return this;
	}
	
	/**
	 * Override this implementation to create a directed graph.
	 */
	@Override
	public ArrayList<Node> getOutNodes(Node node){
		return getInNodes(node);
	}
	
}
