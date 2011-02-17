package model;

import java.util.ArrayList;

public class OriginalPopulationModel implements PopulationModel {

	private ArrayList<Agent> previousGeneration = new ArrayList<Agent>();
	private ArrayList<Agent> currentGeneration = new ArrayList<Agent>();

	public OriginalPopulationModel(ArrayList<Agent> agents) {
		currentGeneration = agents;
	}

	@Override
	public void switchGenerations(ArrayList<Agent> newAgents) {

		previousGeneration = currentGeneration;
		currentGeneration = newAgents;

	}

	@Override
	public ArrayList<Agent> getNeighbours(Agent agent) {

		return getNeighbours(agent, 1);
	}

	@Override
	public ArrayList<Agent> getNeighbours(Agent agent, int distance) {

		int location = currentGeneration.indexOf(agent);

		ArrayList<Agent> retValAgents = new ArrayList<Agent>();

		for (int i = 1; i <= distance; i++) {
			int neighbour1 = location - i;
			int neighbour2 = location + i;

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
		return getNeighbours(agent, 2);
	}

	@Override
	public ArrayList<Agent> getAncestors(Agent agent, int distance) {

		int location = currentGeneration.indexOf(agent);

		ArrayList<Agent> retValAgents = new ArrayList<Agent>();

		retValAgents.add(previousGeneration.get(location));

		for (int i = 1; i <= distance; i++) {

			int neighbour1 = location - i;
			int neighbour2 = location + i;

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
			agents.add(new Agent(i));
		}

		OriginalPopulationModel test = new OriginalPopulationModel(agents);
		test.switchGenerations(agents);

		ArrayList<Agent> neighbours = test.getAncestors(agents.get(99), 3);

		for (Agent agent : neighbours) {
			System.out.println("Agent " + agent.id);
		}
	}

}
