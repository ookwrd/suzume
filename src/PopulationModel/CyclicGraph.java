package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.ConfigurationParameter;

public class CyclicGraph extends AbstractGraph {

	protected static final String SELF_LINKS = "Include Self Links";
	protected static final String LINK_DISTANCE = "Link Distance";
	
	public CyclicGraph(){
		setDefaultParameter(SELF_LINKS, new ConfigurationParameter(false));
		setDefaultParameter(LINK_DISTANCE, new ConfigurationParameter(1));
	}

	@Override
	public ArrayList<Node> getInNodes(Node node) {
		//TODO better with index?
		ArrayList<Node> retValAgents = new ArrayList<Node>();
		
		int distance = config.getParameter("Max link distance").getInteger();
		int location = populations.indexOf(node);

		if(config.getParameter("Include Self Links").getBoolean()){
			retValAgents.add(populations.get(location));
		}

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around ends of arrays
			if (neighbour1 < 0) {
				neighbour1 = populations.size() + neighbour1;
			}
			if (neighbour2 >= populations.size()) {
				neighbour2 = neighbour2 - populations.size();
			}

			retValAgents.add(populations.get(neighbour1));
			retValAgents.add(populations.get(neighbour2));
		}

		return retValAgents;
		
	}
	
}
