package PopulationModel;

import java.util.ArrayList;

import simulation.RandomGenerator;

import AutoConfiguration.Configurable;

public interface Graph extends Configurable {

	public enum GraphType {COMPLETE, CYCLIC/*, GRID*/}
	
	public void init(ArrayList<Node> subNodes /*TODO is this needed?*/, GraphConfiguration config, RandomGenerator randomGenerator);
	
	public void resetSubNodes(ArrayList<Node> subNodes);
	
	public ArrayList<Node> getNodeSet();
	
	public GraphConfiguration getConfiguration();
	
	public ArrayList<Node> getInNodes(int index);
	
	public ArrayList<Node> getOutNodes(int index);
	
}
