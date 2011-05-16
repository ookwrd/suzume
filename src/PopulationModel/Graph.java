package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.Configurable;

public interface Graph extends Configurable {

	public void init(ArrayList<PopulationNode> populations, GraphConfiguration config);
	
	public ArrayList<PopulationNode> getNodeSet();
	
	public ArrayList<PopulationNode> getInNodes(PopulationNode node);
	
	public ArrayList<PopulationNode> getOutNodes(PopulationNode node);
	
}
