package PopulationModel;

import nodes.AbstractNode;
import PopulationModel.graphs.Graph;
import PopulationModel.graphs.GraphFactory;
import PopulationModel.graphs.Grid;
import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;
import simulation.RandomGenerator;

public class SimpleConfigurableModel extends AdvancedConfigurableModel {

	public static final String GRAPH = "Agent Interaction Graph:";
	
	public SimpleConfigurableModel(){
		fixParameter(REPRODUCTION_GRAPH);
		fixParameter(LEARNING_GRAPH);
		fixParameter(COMMUNICATION_GRAPH);
		fixParameter(VISUALIZATION_STRUCTURE);
		
		BasicConfigurable graph = GraphFactory.constructGraph(Graph.GraphType.GRID).getConfiguration();
		graph.overrideParameter(Grid.SELF_LINKS, new ConfigurationParameter(true));
		graph.overrideParameter(Grid.ROW_NUMBERS, new ConfigurationParameter(20));
		setDefaultParameter(GRAPH, new ConfigurationParameter(graph));
	}
	
	public SimpleConfigurableModel(Configurable config, RandomGenerator generator){
		super(fixConfiguration(config), generator);
	}
	
	private static Configurable fixConfiguration(Configurable config){
		config.overrideParameter(REPRODUCTION_GRAPH, new ConfigurationParameter(config.getParameter(GRAPH).getGraphConfiguration()));
		config.overrideParameter(COMMUNICATION_GRAPH, new ConfigurationParameter(config.getParameter(GRAPH).getGraphConfiguration()));
		config.overrideParameter(LEARNING_GRAPH, new ConfigurationParameter(config.getParameter(GRAPH).getGraphConfiguration()));
		return config;
	}
	
	@Override
	public String getDescription() {
		return AbstractNode.NodeType.SimpleConfigurableModel + " is a population model in which learning, communication and reproduction all take place on the same interaction graph. " +
				"To configure these interactions seperately make use of an " + AbstractNode.NodeType.AdvancedConfigurableModel + " instead.";
	}
}
