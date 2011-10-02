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
	public ArrayList<Node> getInNodes(int location) {
		ArrayList<Node> retValAgents = new ArrayList<Node>();
		
		int distance = getIntegerParameter(LINK_DISTANCE);

		if(getBooleanParameter(SELF_LINKS)){
			retValAgents.add(subNodes.get(location));
		}

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around ends of arrays
			if (neighbour1 < 0) {
				neighbour1 = subNodes.size() + neighbour1;
			}
			if (neighbour2 >= subNodes.size()) {
				neighbour2 = neighbour2 - subNodes.size();
			}

			retValAgents.add(subNodes.get(neighbour1));
			retValAgents.add(subNodes.get(neighbour2));
		}

		return retValAgents;	
	}
	
}
