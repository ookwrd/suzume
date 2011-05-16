package PopulationModel;

import java.util.ArrayList;

import Agents.NodeConfiguration;

public abstract class AbstractGraph implements PopulationGraph {

	protected ArrayList<PopulationNode> populations;
	protected NodeConfiguration config;
	
	@Override
	public void init(ArrayList<PopulationNode> populations, NodeConfiguration config){
		this.config = config;
		this.populations = populations; 
	}
	
	@Override
	public ArrayList<PopulationNode> getNodeSet(){
		return populations;
	}
	
}
