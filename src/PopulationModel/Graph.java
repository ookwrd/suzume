package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.Configurable;

public interface Graph extends Configurable {

	public void init(ArrayList<Node> populations, GraphConfiguration config);
	
	public ArrayList<Node> getNodeSet();
	
	public ArrayList<Node> getInNodes(Node node);
	
	public ArrayList<Node> getOutNodes(Node node);
	
}
