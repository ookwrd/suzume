package model;
import java.util.ArrayList;


public interface PopulationModel {

	public void switchGenerations(ArrayList<Agent> agents);
	
	public ArrayList<Agent> getNeighbours(Agent agent);
	public ArrayList<Agent> getNeighbours(Agent agent, int distance);
	
	public ArrayList<Agent> getAncestors(Agent agent);
	public ArrayList<Agent> getAncestors(Agent agent, int distance);
	
}
