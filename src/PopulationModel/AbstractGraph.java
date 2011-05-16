package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.AbstractConfigurable;

public abstract class AbstractGraph extends AbstractConfigurable implements Graph {

	protected ArrayList<PopulationNode> populations;
	protected GraphConfiguration config;
	
	@Override
	public void init(ArrayList<PopulationNode> populations, GraphConfiguration config){
		this.config = config;
		this.populations = populations; 
	}
	
	@Override
	public ArrayList<PopulationNode> getNodeSet(){
		return populations;
	}
	
	/**
	 * Override this implementation to create a directed graph.
	 */
	@Override
	public ArrayList<PopulationNode> getOutNodes(PopulationNode node){
		return getInNodes(node);
	}
	
}
