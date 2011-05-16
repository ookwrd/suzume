package model;

import java.util.ArrayList;

import Agents.Agent;
import Agents.Visualizable;

/**
 * Interface capturing the common elements of population structure. Makes the assumption of discrete generations.
 * 
 * @author Luke McCrohon
 */
public interface PopulationModel extends Visualizable {

	/**
	 * Replaces the oldGeneration with the current generation, which is in turn replaced by the specified new set 
	 * of agents.
	 * 
	 * @param newGeneration
	 */
	public void switchGenerations(ArrayList<Agent> newGeneration);

	public ArrayList<Agent> getPossibleTeachers(Agent agent);

	public ArrayList<Agent> getPossibleCommunicators(Agent agent);
	
	public ArrayList<Agent> getPossibleParents(Agent agent);
	
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
