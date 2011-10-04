package PopulationModel;

import nodes.NodeConfiguration;
import PopulationModel.graphs.Graph;
import PopulationModel.graphs.GraphConfiguration;
import PopulationModel.graphs.GraphFactory;
import PopulationModel.graphs.Grid;
import autoconfiguration.ConfigurationParameter;
import simulation.RandomGenerator;

public class SimpleConfigurableModel extends ConfigurableModel {

	public static final String GRAPH = "Graph:";
	
	public SimpleConfigurableModel(){
		fixParameter(REPRODUCTION_GRAPH);
		fixParameter(LEARNING_GRAPH);
		fixParameter(COMMUNICATION_GRAPH);
		fixParameter(VISUALIZATION_STRUCTURE);
		
		GraphConfiguration graph = GraphFactory.constructGraph(Graph.GraphType.GRID).getConfiguration();
		graph.setParameter(Grid.SELF_LINKS, new ConfigurationParameter(true));
		graph.setParameter(Grid.AUTO_LAYOUT, new ConfigurationParameter(false));
		graph.setParameter(Grid.ROW_NUMBERS, new ConfigurationParameter(20));
		setDefaultParameter(GRAPH, new ConfigurationParameter(graph));
	}
	
	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		config.setParameter(REPRODUCTION_GRAPH, new ConfigurationParameter(config.getGraphParameter(GRAPH)));
		config.setParameter(COMMUNICATION_GRAPH, new ConfigurationParameter(config.getGraphParameter(GRAPH)));
		config.setParameter(LEARNING_GRAPH, new ConfigurationParameter(config.getGraphParameter(GRAPH)));
		
		super.initialize(config, id, randomGenerator);
	}
	
}
