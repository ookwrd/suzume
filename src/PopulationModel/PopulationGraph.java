package PopulationModel;

import java.util.ArrayList;

import Agents.AgentConfiguration;
import AutoConfiguration.Configurable;

public interface PopulationGraph extends Configurable {

	public void init(ArrayList<PopulationNode> populations, AgentConfiguration config);
	
	public ArrayList<PopulationNode> getNodeSet();
	
	public ArrayList<PopulationNode> getOutNodes(PopulationNode node);
	
	public ArrayList<PopulationNode> getInNodes(PopulationNode node);
	
}
