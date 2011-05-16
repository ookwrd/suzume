package PopulationModel;

import java.util.ArrayList;

import Agents.NodeConfiguration;

public interface PopulationGraph {

	public void init(ArrayList<PopulationNode> populations, NodeConfiguration config);
	
	public ArrayList<PopulationNode> getNodeSet();
	
	public ArrayList<PopulationNode> getOutNodes(PopulationNode node);
	
	public ArrayList<PopulationNode> getInNodes(PopulationNode node);
	
}
