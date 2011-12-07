package PopulationModel;

import java.util.ArrayList;

import nodes.Node;

import runTimeVisualization.Visualizable;

/**
 * Interface capturing the common elements of population structure. Makes the assumption of discrete generations.
 * 
 * @author Luke McCrohon
 */
public interface PopulationModel extends Visualizable, Node {

	/**
	 * Replaces the oldGeneration with the current generation, which is in turn replaced by the specified new set 
	 * of agents.
	 * 
	 * @param newGeneration
	 */
	public void setNewSubNodes(ArrayList<Node> newGeneration);

	public ArrayList<Node> getPossibleTeachers(Node agent);

	public ArrayList<Node> getPossibleCommunicators(Node agent);
	
	public ArrayList<Node> getPossibleParents(Node agent);
	
}
