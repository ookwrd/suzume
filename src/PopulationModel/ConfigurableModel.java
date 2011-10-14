package PopulationModel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import nodes.AbstractNode;
import nodes.Node;
import nodes.NodeConfiguration;
import nodes.NodeConfigurationPanel;
import nodes.NodeFactory;
import nodes.Utterance;

import PopulationModel.graphs.CyclicGraph;
import PopulationModel.graphs.Graph;
import PopulationModel.graphs.GraphConfiguration;
import PopulationModel.graphs.GraphFactory;
import autoconfiguration.ConfigurationParameter;

import simulation.RandomGenerator;

public class ConfigurableModel extends AbstractPopulationModel implements PopulationModel {

	private enum VisualizationStructure {LEARNING_GRAPH, COMMUNICATION_GRAPH, REPRODUCTION_GRAPH}
	
	public static final String SUB_NODE = "Sub Model:";
	public static final String REPRODUCTION_GRAPH = "Reproduction Graph";
	public static final String COMMUNICATION_GRAPH = "Communication Graph";
	public static final String LEARNING_GRAPH = "Learning Graph";
	public static final String POPULATION_SIZE = "Population Size";
	public static final String VISUALIZATION_STRUCTURE = "Visualize based on:";
	
	private Graph learningGraph;
	private Graph communicationGraph;
	private Graph reproductionGraph;
	
	private ArrayList<Node> previousGeneration = new ArrayList<Node>();
	
	public ConfigurableModel(){
		setDefaultParameter(POPULATION_SIZE, new ConfigurationParameter(200));
		setDefaultParameter(VISUALIZATION_STRUCTURE, new ConfigurationParameter(VisualizationStructure.values()));
		
		GraphConfiguration learning = GraphFactory.constructGraph(Graph.GraphType.CYCLIC).getConfiguration();
		learning.setParameter(CyclicGraph.LINK_DISTANCE, new ConfigurationParameter(2));
		setDefaultParameter(LEARNING_GRAPH, new ConfigurationParameter(learning));
		
		GraphConfiguration communication = GraphFactory.constructGraph(Graph.GraphType.CYCLIC).getConfiguration();
		communication.setParameter(CyclicGraph.SELF_LINKS, new ConfigurationParameter(false));
		setDefaultParameter(COMMUNICATION_GRAPH, new ConfigurationParameter(communication));
		
		setDefaultParameter(REPRODUCTION_GRAPH, new ConfigurationParameter(GraphFactory.constructGraph(Graph.GraphType.COMPLETE).getConfiguration()));
		setDefaultParameter(SUB_NODE, new ConfigurationParameter(NodeFactory.constructUninitializedNode(AbstractNode.NodeType.YamauchiHashimoto2010Agent).getConfiguration()));
	}
	
	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initialize(config, id, randomGenerator);
			
		//Initialize SubNodes
		NodeConfiguration sub = getParameter(SUB_NODE).getNodeConfiguration();
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 1; i <= getParameter(POPULATION_SIZE).getInteger(); i++) {
			Node node = NodeFactory.constructUninitializedNode((NodeType) sub.getParameter(NodeConfigurationPanel.NODE_TYPE).getSelectedValue());
			node.initialize(sub, NodeFactory.nextNodeID++, randomGenerator);
			nodes.add(node);
		}
		currentGeneration = nodes;
		
		nodes = new ArrayList<Node>();
		for (int i = 1; i <= getParameter(POPULATION_SIZE).getInteger(); i++) {
			Node node = NodeFactory.constructUninitializedNode((NodeType) sub.getParameter(NodeConfigurationPanel.NODE_TYPE).getSelectedValue());
			node.initialize(sub, NodeFactory.nextNodeID++, randomGenerator);
			nodes.add(node);
		}
		previousGeneration = nodes;
	
		//Initialize Graphs
		learningGraph = GraphFactory.constructGraph(getGraphParameter(LEARNING_GRAPH).getType());
		learningGraph.init(previousGeneration, getGraphParameter(LEARNING_GRAPH), randomGenerator);
		
		communicationGraph = GraphFactory.constructGraph(getGraphParameter(COMMUNICATION_GRAPH).getType());
		communicationGraph.init(currentGeneration, getGraphParameter(COMMUNICATION_GRAPH), randomGenerator);
		
		reproductionGraph = GraphFactory.constructGraph(getGraphParameter(REPRODUCTION_GRAPH).getType());
		reproductionGraph.init(currentGeneration, getGraphParameter(REPRODUCTION_GRAPH), randomGenerator);
	}

	@Override
	public void setNewSubNodes(ArrayList<Node> newGeneration) {	
		previousGeneration = currentGeneration;
		currentGeneration = newGeneration;
		
		learningGraph.resetSubNodes(previousGeneration);
		communicationGraph.resetSubNodes(currentGeneration);
		reproductionGraph.resetSubNodes(currentGeneration);
	}

	@Override
	public ArrayList<Node> getPossibleCommunicators(Node target) {
		return communicationGraph.getInNodes(currentGeneration.indexOf(target));
	}

	@Override
	public ArrayList<Node> getPossibleTeachers(Node target) {
		return learningGraph.getInNodes(currentGeneration.indexOf(target));
	}

	@Override
	public ArrayList<Node> getPossibleParents(Node target) {	
		return reproductionGraph.getInNodes(currentGeneration.indexOf(target));
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB,
			int id, RandomGenerator randomGenerator) {
		System.err.println("In population mode this should never be called.");
	}

	@Override
	public void teach(Node agent) {
		System.err.println("Shouldnt be here");
	}

	@Override
	public void learnUtterance(Utterance utterance) {
		System.err.println("Shouldnt be here");
	}

	@Override
	public void communicate(Node partner) {
		System.err.println("Shouldnt be here");
	}

	@Override
	public StatisticsAggregator getStatisticsAggregator(Object key){
		return currentGeneration.get(0).getStatisticsAggregator(key);
	}

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return currentGeneration.get(0).getVisualizationKeys();
	}
	
	@Override
	public ArrayList<Object> getStatisticsKeys(){
		return currentGeneration.get(0).getStatisticsKeys();
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension, VisualizationStyle type){
		assert(currentGeneration.size() != 0);
	
		switch(type){
		case layout:
			return getDimensionLayout(baseDimension, type);
			
		case vertical:
			return getDimensionVertical(baseDimension, type);
			
		default:
			System.err.println("Unsupported Visualization type");
			return null;
		}
	
	}
	
	private Dimension getDimensionLayout(Dimension baseDimension, VisualizationStyle type){

		switch((VisualizationStructure)getListParameter(VISUALIZATION_STRUCTURE)[0]){
		case LEARNING_GRAPH:
			return learningGraph.getDimension(baseDimension, type);
			
		case COMMUNICATION_GRAPH:
			return communicationGraph.getDimension(baseDimension, type);
			
		case REPRODUCTION_GRAPH:
			return reproductionGraph.getDimension(baseDimension, type);
			
		default:
			System.err.println("Unknown Visualization Structure selected in CompositePopulationModel.getDimension");
			return null;
		}
	}
	
	private Dimension getDimensionVertical(Dimension baseDimension, VisualizationStyle type){
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);
		
		return new Dimension(agentDimension.width, agentDimension.height*size);
	}	
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		switch(type){
		case layout:
			drawLayout(baseDimension, type, visualizationKey, g);
			break;
			
		case vertical:
			drawVertical(baseDimension, type, visualizationKey, g);
			break;
			
		default:
			System.out.println("Unrecognized Visualization type");
			return;
		}
	}
	
	private void drawLayout(Dimension baseDimension, VisualizationStyle type, Object key, Graphics g){
		
		switch((VisualizationStructure)getListParameter(VISUALIZATION_STRUCTURE)[0]){
		case LEARNING_GRAPH:
			learningGraph.draw(baseDimension, type, key, g);
			return;
			
		case COMMUNICATION_GRAPH:
			communicationGraph.draw(baseDimension, type, key, g);
			return;
			
		case REPRODUCTION_GRAPH:
			reproductionGraph.draw(baseDimension, type, key, g);
			return;
			
		default:
			System.err.println("Unknown Visualization Structure selected in CompositePopulationModel.getDimension");
		}
	}

	private void drawVertical(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		int size = previousGeneration.size();
		Dimension agentDimension = previousGeneration.get(0).getDimension(baseDimension, type);

		for(int i = 0; i < size; i++){
			previousGeneration.get(i).draw(baseDimension, type, visualizationKey, g);
			g.translate(0, agentDimension.height);
		}
	}
}
