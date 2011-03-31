package model;

import java.util.ArrayList;

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
public class OriginalPopulationModel implements PopulationModel {

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

	public static void main(String[] args) {

		ArrayList<Agent> agents = new ArrayList<Agent>();

		for (int i = 1; i <= 200; i++) {
			agents.add(new OriginalAgent(i, new RandomGenerator()));
		}
		
		ArrayList<Agent> agents1 = new ArrayList<Agent>();

		for (int i = 1; i <= 200; i++) {
			agents1.add(new OriginalAgent(i, new RandomGenerator()));
		}

		OriginalPopulationModel test = new OriginalPopulationModel(agents, agents1);
		test.switchGenerations(agents);

		ArrayList<Agent> neighbours = test.getAncestors(agents.get(99), 3);

		for (Agent agent : neighbours) {
			System.out.println("Agent " + agent.getId());
		}
	}

}
