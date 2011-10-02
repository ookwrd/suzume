package PopulationModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import populationNodes.AbstractNode;
import populationNodes.NodeConfiguration;
import populationNodes.NodeFactory;
import populationNodes.NodeTypeConfigurationPanel;
import populationNodes.Utterance;
import populationNodes.Agents.Agent;

import simulation.RandomGenerator;

import AutoConfiguration.ConfigurationParameter;

public class CompositePopulationModel extends AbstractNode implements PopulationModel {

	public static final String SUB_NODE = "Sub Model:";
	public static final String REPRODUCTION_GRAPH = "Reproduction Graph";
	public static final String COMMUNICATION_GRAPH = "Communication Graph";
	public static final String LEARNING_GRAPH = "Learning Graph";
	public static final String POPULATION_SIZE = "Population Size";
	public static final String LEARN_TO_DISTANCE = "Learn from agents to distance:";
	public static final String COMMUNICATE_TO_DISTANCE = "Communicate with agents to distance:";
	public static final String REPRODUCE_TO_DISTANCE = "Parents selected out to distance:";
	
	private Graph learningGraph;
	private Graph communicationGraph;
	private Graph reproductionGraph;
	
	private ArrayList<Node> previousGeneration = new ArrayList<Node>();
	private ArrayList<Node> currentGeneration = new ArrayList<Node>();
	
	public CompositePopulationModel(){
		setDefaultParameter(POPULATION_SIZE, new ConfigurationParameter(200));
		setDefaultParameter(LEARNING_GRAPH, new ConfigurationParameter(GraphFactory.constructGraph(Graph.GraphType.CYCLIC).getConfiguration()));
		setDefaultParameter(COMMUNICATION_GRAPH, new ConfigurationParameter(GraphFactory.constructGraph(Graph.GraphType.CYCLIC).getConfiguration()));
		setDefaultParameter(REPRODUCTION_GRAPH, new ConfigurationParameter(GraphFactory.constructGraph(Graph.GraphType.COMPLETE).getConfiguration()));
		setDefaultParameter(SUB_NODE, new ConfigurationParameter(NodeFactory.constructUninitializedNode(AbstractNode.NodeType.YamauchiHashimoto2010).getConfiguration()));
	}
	
	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initialize(config, id, randomGenerator);
			
		//Initialize SubNodes
		NodeConfiguration sub = getParameter(SUB_NODE).getNodeConfiguration();
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 1; i <= getParameter(POPULATION_SIZE).getInteger(); i++) {
			Node node = NodeFactory.constructUninitializedNode((NodeType) sub.getParameter(NodeTypeConfigurationPanel.NODE_TYPE).getSelectedValue());
			node.initialize(sub, NodeFactory.nextNodeID++, randomGenerator);
			nodes.add(node);
		}
		currentGeneration = nodes;
		
		nodes = new ArrayList<Node>();
		for (int i = 1; i <= getParameter(POPULATION_SIZE).getInteger(); i++) {
			Node node = NodeFactory.constructUninitializedNode((NodeType) sub.getParameter(NodeTypeConfigurationPanel.NODE_TYPE).getSelectedValue());
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
	public ArrayList<Agent> getCurrentGeneration() {	
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		for(Node node : currentGeneration){
			retAgents.addAll(node.getBaseAgents());
		}
		return retAgents;
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
	public boolean canStillLearn() {
		System.err.println("Shouldnt be here");
		return false;
	}

	@Override
	public void invent() {
		System.err.println("Shouldnt be here");
	}

	@Override
	public ArrayList<Agent> getBaseAgents() {
		System.err.println("Shouldnt be here");
		return getCurrentGeneration();
	}

	@Override
	public void communicate(Node partner) {
		System.err.println("Shouldnt be here");
	}

	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		return currentGeneration.get(0).getStatisticsAggregators();
	}

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return currentGeneration.get(0).getVisualizationKeys();
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension, VisualizationStyle type){
		
		if(currentGeneration.size() == 0){
			System.out.print("Trying to get Dimension of empty population model");
			return null;
		}
	
		switch(type){
		case layout:
			return getDimensionLayout(baseDimension, type);
			
		case vertical:
			return getDimensionVertical(baseDimension, type);
			
		default:
			System.out.println("Unsupported Visualization type");
			return null;
		}
	
	}
	
	private Dimension getDimensionLayout(Dimension baseDimension, VisualizationStyle type){
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);
		
		int agentsPerEdge = (size/4)+(size%4!=0?1:0) + 1;
		
		return new Dimension(agentsPerEdge*agentDimension.width, agentsPerEdge*agentDimension.height);
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
		int size = currentGeneration.size();
		Dimension thisDimension = getDimension(baseDimension, type);
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);

		int agentsPerSection = (size/4)+(size%4!=0?1:0);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, thisDimension.width, thisDimension.height);
		
		//Top edge
		int i;
		for(i = 0; i < agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,key,g);
			g.translate(agentDimension.width, 0);
		}
		
		//right edge
		for(; i < 2*agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,key,g);
			g.translate(0, agentDimension.height);
		}
		
		//Bottom edge
		for(; i < 3*agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,key,g);
			g.translate(-agentDimension.width, 0);
		}
		
		//Left edge
		for(; i < size; i++){
			previousGeneration.get(i).draw(baseDimension,type,key,g);
			g.translate(0, -agentDimension.height);
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
