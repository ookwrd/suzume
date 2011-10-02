package PopulationModel;

import java.util.ArrayList;

import simulation.RandomGenerator;

import AutoConfiguration.Configurable;

public interface Graph extends Configurable {

	public enum GraphType {COMPLETE, CYCLIC/*, GRID*/}
	
	public void init(ArrayList<Node> populations, GraphConfiguration config, RandomGenerator randomGenerator);
	
	public ArrayList<Node> getNodeSet();
	
	public GraphConfiguration getConfiguration();
	
	public ArrayList<Node> getInNodes(Node node);
	
	public ArrayList<Node> getOutNodes(Node node);
	
}
