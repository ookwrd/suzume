package PopulationModel;

import java.util.ArrayList;

import Agents.AgentConfiguration;
import AutoConfiguration.AbstractConfigurable;

public abstract class AbstractGraph extends AbstractConfigurable implements PopulationGraph {

	protected ArrayList<PopulationNode> populations;
	protected AgentConfiguration config;
	
	@Override
	public void init(ArrayList<PopulationNode> populations, AgentConfiguration config){
		this.config = config;
		this.populations = populations; 
	}
	
	@Override
	public ArrayList<PopulationNode> getNodeSet(){
		return populations;
	}
	
}
