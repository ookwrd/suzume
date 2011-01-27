import java.util.ArrayList;


public class OriginalPopulationModel implements PopulationModel {
	
	private ArrayList<Agent> previousGeneration = new ArrayList<Agent>();
	private ArrayList<Agent> currentGeneration = new ArrayList<Agent>();
	
	public OriginalPopulationModel(int size){
		
	}

	@Override
	public void switchGenerations() {
		
		previousGeneration = currentGeneration;
		currentGeneration = new ArrayList<Agent>();
		
	}

	@Override
	public ArrayList<Agent> getNeighbours(Agent agent) {
	
		return getNeighbours(agent, 1);
	}

	@Override
	public ArrayList<Agent> getNeighbours(Agent agent, int distance) {
		
		int location = currentGeneration.indexOf(agent);
		
		ArrayList<Agent> retValAgents = new ArrayList<Agent>();
		
		for(int i = 1; i < distance; i++){
			int neighbour1 = location - i;
			int neighbour2 = location + i;
			
			if(neighbour1 < 0){
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			
			if(neighbour2 < currentGeneration.size()){
				neighbour2 = neighbour2 - currentGeneration.size();
			}
			
			retValAgents.add(currentGeneration.get(neighbour1));
			retValAgents.add(currentGeneration.get(neighbour2));
		}
		
		return retValAgents;
	}

	@Override
	public ArrayList<Agent> getAncestors(Agent agent, int distance) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
