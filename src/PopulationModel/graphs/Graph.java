package PopulationModel.graphs;

import java.util.ArrayList;

import nodes.Node;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;

import runTimeVisualization.Visualizable;
import simulation.RandomGenerator;


public interface Graph extends Configurable, Visualizable {

	public enum GraphType {COMPLETE, CYCLIC, GRID}
	
	public void init(ArrayList<Node> subNodes, Configurable config, RandomGenerator randomGenerator);
	
	public void resetSubNodes(ArrayList<Node> subNodes);
	
	public ArrayList<Node> getNodeSet();
	
	public BasicConfigurable getConfiguration();
	
	public ArrayList<Node> getInNodes(int index);
	
	public ArrayList<Node> getOutNodes(int index);
	
}
