package simulation;

import java.util.ArrayList;

import runTimeVisualization.Visualizable;

import Agents.Agent;
import PopulationModel.PopulationNode;

/**
 * Interface capturing the common elements of population structure. Makes the assumption of discrete generations.
 * 
 * @author Luke McCrohon
 */
public interface PopulationModel extends Visualizable, PopulationNode {

	/**
	 * Replaces the oldGeneration with the current generation, which is in turn replaced by the specified new set 
	 * of agents.
	 * 
	 * @param newGeneration
	 */
	public void switchGenerations(ArrayList<PopulationNode> newGeneration);

	public ArrayList<PopulationNode> getPossibleTeachers(PopulationNode agent);

	public ArrayList<PopulationNode> getPossibleCommunicators(PopulationNode agent);
	
	public ArrayList<PopulationNode> getPossibleParents(PopulationNode agent);
	
	/**
	 * Returns the set of agents representing the previous generation.
	 * 
	 * @return
	 */
	public ArrayList<Agent> getCurrentGeneration();

	/**
	 * Returns the set of agents comprising the previous generation.
	 * 
	 * @return
	 */
	public ArrayList<Agent> getAncestorGeneration();
	
}
