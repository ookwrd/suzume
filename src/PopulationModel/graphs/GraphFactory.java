package PopulationModel.graphs;

import autoconfiguration.ConfigurationParameter;
import PopulationModel.graphs.Graph.GraphType;

public class GraphFactory {

	public static Graph constructGraph(GraphType type){
		
		Graph retVal;
		
		switch (type) {
		case CYCLIC:
			retVal = new CyclicGraph();
			break;
			
		case COMPLETE:
			retVal = new CompleteGraph();
			break;
			
		case GRID:
			retVal = new Grid();
			break;

		default:
			System.err.println("Unrecognized graph type in GraphFactory.");
			retVal = null;
		}
		
		retVal.setFixedParameter(GraphTypeConfigurationPanel.GRAPH_TYPE, new ConfigurationParameter(Graph.GraphType.values(), new Object[]{type}));
		
		return retVal;
	} 
	
}
