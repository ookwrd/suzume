package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import org.jfree.ui.Size2D;

import Agents.Agent;
import Agents.OriginalAgent;

/**
 * Population model corresponding to a cyclical distribution of agents as seen in:
 * Relaxation of selection, Niche Construction, and the Baldwin Eﬀect in Language 
 * Evolution - Hajime Yamauchi & Takashi Hashimoto
 * 
 * @author Luke McCrohon
 *
 */
public class OriginalPopulationModel extends AbstractPopulationModel implements PopulationModel {

	private ArrayList<Agent> previousGeneration = new ArrayList<Agent>();
	private ArrayList<Agent> currentGeneration = new ArrayList<Agent>();
	
	/**
	 * Create new population with the specified agents as the currentGeneration and previousGeneration.
	 * 
	 * @param agents
	 */
	public OriginalPopulationModel(ArrayList<Agent> currentGeneration, ArrayList<Agent> previousGeneration) {
		this.currentGeneration = currentGeneration;
		this.previousGeneration = previousGeneration;
	}

	@Override
	public void switchGenerations(ArrayList<Agent> newGeneration) {

		previousGeneration = currentGeneration;
		currentGeneration = newGeneration;

	}

	@Override
	public ArrayList<Agent> getNeighbors(Agent agent) {

		return getNeighbors(agent, 1);
	}

	/**
	 * 
	 * size of returnValue = distance*2 
	 * 
	 */
	@Override
	public ArrayList<Agent> getNeighbors(Agent agent, int distance) {

		int location = currentGeneration.indexOf(agent);

		ArrayList<Agent> retValAgents = new ArrayList<Agent>();

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around ends of arrays
			if (neighbour1 < 0) {
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			if (neighbour2 >= currentGeneration.size()) {
				neighbour2 = neighbour2 - currentGeneration.size();
			}

			retValAgents.add(currentGeneration.get(neighbour1));
			retValAgents.add(currentGeneration.get(neighbour2));
		}

		return retValAgents;
	}

	@Override
	public ArrayList<Agent> getAncestors(Agent agent) {
		return getAncestors(agent, 0);
	}

	/**
	 * 
	 * size of return = agents*2 + 1
	 */
	@Override
	public ArrayList<Agent> getAncestors(Agent agent, int distance) {

		int location = currentGeneration.indexOf(agent);

		ArrayList<Agent> retValAgents = new ArrayList<Agent>();

		//add the ancestor at the same point as the specified agent
		retValAgents.add(previousGeneration.get(location));

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around the end of arrays
			if (neighbour1 < 0) {
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			if (neighbour2 >= currentGeneration.size()) {
				neighbour2 = neighbour2 - currentGeneration.size();
			}

			retValAgents.add(previousGeneration.get(neighbour1));
			retValAgents.add(previousGeneration.get(neighbour2));

		}

		return retValAgents;
	}

	@Override
	public ArrayList<Agent> getAncestorGeneration() {
		return previousGeneration;
	}

	@Override
	public ArrayList<Agent> getCurrentGeneration() {
		return currentGeneration;
	}
	
	@Override
	public Dimension getDimension(){
		
		if(currentGeneration.size() == 0){
			System.out.print("Trying to get Dimension of empty population model");
			return null;
		}
		
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension();
		
		int agentsPerEdge = (size/4)+(size%4!=0?1:0) + 1;
		
		return new Dimension(agentsPerEdge*agentDimension.width, agentsPerEdge*agentDimension.height);
		
	}
	
	@Override
	public void draw(Graphics g){
		
		int size = currentGeneration.size();
		Dimension thisDimension = getDimension();
		Dimension agentDimension = currentGeneration.get(0).getDimension();

		int agentsPerSection = (size/4)+(size%4!=0?1:0);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, thisDimension.width, thisDimension.height);
		
		//Top edge
		int i;
		for(i = 0; i < agentsPerSection; i++){
			previousGeneration.get(i).draw(g);
			g.translate(agentDimension.width, 0);
		}
		
		//right edge
		for(; i < 2*agentsPerSection; i++){
			previousGeneration.get(i).draw(g);
			g.translate(0, agentDimension.height);
		}
		
		//Bottom edge
		for(; i < 3*agentsPerSection; i++){
			previousGeneration.get(i).draw(g);
			g.translate(-agentDimension.width, 0);
		}
		
		//Left edge
		for(; i < size; i++){
			//TODO reset the translate point.
			previousGeneration.get(i).draw(g);
			g.translate(0, -agentDimension.height);
		}
		
	}
	
	/**
	 * Print a generations worth of agents. 
	 */
	@Override
	public void print(){
		System.out.println("Printing Previous Generation");	
		for(Agent agent : getAncestorGeneration()){  
			agent.printAgent();
			System.out.println();
		}
	}

}
