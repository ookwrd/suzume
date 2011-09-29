package PopulationModel;

import populationNodes.AbstractNode;
import populationNodes.NodeTypeConfigurationPanel;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Graph.GraphType;

public class GraphFactory {

	public static Graph constructGraph(GraphType type){
		
		Graph retVal;
		
		switch (type) {
		
		case CYCLIC:
			retVal = new CyclicGraph();
			break;
			
		case COMPLETE:
			retVal = new FullyConnectedGraph();
			break;

		default:
			System.err.println("Unrecognized graph type in GraphFactory.");
			retVal = null;
		}
		
		retVal.setFixedParameter(GraphTypeConfigurationPanel.GRAPH_TYPE, new ConfigurationParameter(Graph.GraphType.values(), new Object[]{type}));
		
		return retVal;
	} 
	
}
