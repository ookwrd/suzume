package PopulationModel;

import java.util.ArrayList;
import java.util.HashMap;

import AutoConfiguration.ConfigurationParameter;

public class CyclicGraph extends AbstractGraph {

	@SuppressWarnings("serial")
	public static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Include Self Links", new ConfigurationParameter(false));
		put("Max link distance", new ConfigurationParameter(1));//TODO factor out these strings
	}};

	@Override
	public ArrayList<PopulationNode> getInNodes(PopulationNode node) {

		ArrayList<PopulationNode> retValAgents = new ArrayList<PopulationNode>();
		
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
