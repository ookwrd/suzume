package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import Agents.Agent;

/**
 * Interface capturing the common elements of population structure. Makes the assumption of discrete generations.
 * 
 * @author Luke McCrohon
 */
public interface PopulationModel {

	/**
	 * Replaces the oldGeneration with the current generation, which is in turn replaced by the specified new set 
	 * of agents.
	 * 
	 * @param newGeneration
	 */
	public void switchGenerations(ArrayList<Agent> newGeneration);
	
	/**
	 * Gets the immediate neighbors of the specified agent. Equivalent to getNeighbours(agent, 1).
	 * 
	 * @param agent
	 * @return
	 */
	public ArrayList<Agent> getNeighbors(Agent agent);

	/**
	 * Gets the neighbors (agents of the current generation) of the specified agent out to the specified 
	 * distance. The concept of distance may vary according to population model, and so consequently may 
	 * the number of agents returned for a given distance value.
	 * 
	 * @param agent
	 * @param distance
	 * @return
	 */
	public ArrayList<Agent> getNeighbors(Agent agent, int distance);

	/**
	 * Gets the immediate ancestor of the specified agent. Equivalent to getAncestor(agent, 0).
	 * 
	 * @param agent
	 * @return
	 */
	public ArrayList<Agent> getAncestors(Agent agent);

	/**
	 * Gets the ancestors (agents of the previous generation) of the specified agent out to the specified
	 * distance. The concept of distance may vary according to population model, and so consequently may 
	 * the number of agents returned for a given distance value.
	 * 
	 * @param agent
	 * @param distance
	 * @return
	 */
	public ArrayList<Agent> getAncestors(Agent agent, int distance);

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
	
	
	public Dimension getDimension();
	
	public void draw(Graphics g);
	
	public void print();

}
