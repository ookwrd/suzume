package PopulationModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import populationNodes.AbstractNode;
import populationNodes.NodeConfiguration;
import populationNodes.NodeFactory;
import populationNodes.Utterance;
import populationNodes.Agents.Agent;
import populationNodes.Agents.YamauchiHashimoto2010;

import simulation.RandomGenerator;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.GraphConfiguration.GraphType;
import PopulationModel.Node.StatisticsAggregator;

public class CompositePopulationModel extends AbstractNode implements PopulationModel {

	public static final String SUB_NODE = "Sub node";
	public static final String REPRODUCTION_GRAPH = "Reproduction Graph";
	public static final String COMMUNICATION_GRAPH = "Communication Graph";
	public static final String LEARNING_GRAPH = "Learning Graph";
	public static final String POPULATION_SIZE = "Population Size";
	public static final String LEARN_TO_DISTANCE = "Learn from agents to distance:";
	public static final String COMMUNICATE_TO_DISTANCE = "Communicate with agents to distance:";
	
	{
		setDefaultParameter(POPULATION_SIZE, new ConfigurationParameter(200));
		//setDefaultParameter(LEARNING_GRAPH, new ConfigurationParameter(GraphType.FullyConnected));
		//setDefaultParameter(COMMUNICATION_GRAPH, new ConfigurationParameter(GraphType.CyclicGraph));
		//setDefaultParameter(REPRODUCTION_GRAPH, new ConfigurationParameter(GraphType.CyclicGraph));
		setDefaultParameter(SUB_NODE, new ConfigurationParameter(new YamauchiHashimoto2010()));
		setDefaultParameter(LEARN_TO_DISTANCE, new ConfigurationParameter(2));
		setDefaultParameter(COMMUNICATE_TO_DISTANCE, new ConfigurationParameter(1));
	}
	
	private Graph learningGraph;
	private Graph communicationGraph;
	private Graph reproductionGraph;
	
	private ArrayList<Node> previousGeneration = new ArrayList<Node>();
	private ArrayList<Node> currentGeneration = new ArrayList<Node>();
	
	public CompositePopulationModel(){}
	
	public CompositePopulationModel(//TODO use initialization pattern
			ArrayList<Node> currentGeneration, 
			ArrayList<Node> previousGeneration 
			//ModelConfiguration config
			//GraphConfiguration learning, 
			//GraphConfiguration communication, 
			//GraphConfiguration reproduction
			) 
	{//TODO kill this
		this.currentGeneration = currentGeneration;
		this.previousGeneration = previousGeneration;
	}
	

	@Override
	public void switchGenerations(ArrayList<Node> newGeneration) {
		previousGeneration = currentGeneration;
		currentGeneration = newGeneration;
	}

	/**
	 * 
	 * size of returnValue = distance*2 
	 * 
	 */
	@Override
	public ArrayList<Node> getPossibleCommunicators(Node agent) {

		int distance = getParameter(COMMUNICATE_TO_DISTANCE).getInteger();
		
		int location = currentGeneration.indexOf(agent);

		ArrayList<Node> retValAgents = new ArrayList<Node>();

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around ends of arrays
			if (neighbour1 < 0) {
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			if (neighbour2 >= currentGeneration.size()) {
				neighbour2 = neighbour2 - currentGeneration.size();
			}

			retValAgents.add(currentGeneration.get(neighbour1));
			retValAgents.add(currentGeneration.get(neighbour2));
		}

		return retValAgents;
	}

	/**
	 * 
	 * size of return = agents*2 + 1
	 */
	@Override
	public ArrayList<Node> getPossibleTeachers(Node agent) {

		int distance = getParameter(LEARN_TO_DISTANCE).getInteger();
		
		int location = currentGeneration.indexOf(agent);

		ArrayList<Node> retValAgents = new ArrayList<Node>();

		//add the ancestor at the same point as the specified agent
		retValAgents.add(previousGeneration.get(location));

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around the end of arrays
			if (neighbour1 < 0) {
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			if (neighbour2 >= currentGeneration.size()) {
				neighbour2 = neighbour2 - currentGeneration.size();
			}

			retValAgents.add(previousGeneration.get(neighbour1));
			retValAgents.add(previousGeneration.get(neighbour2));

		}

		return retValAgents;
	}

	@Override
	public ArrayList<Agent> getAncestorGeneration() {
		
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		for(Node node :previousGeneration){
			retAgents.addAll(node.getBaseAgents());
		}
		return retAgents;
	}

	@Override
	public ArrayList<Agent> getCurrentGeneration() {
		
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		for(Node node :currentGeneration){
			retAgents.addAll(node.getBaseAgents());
		}
		return retAgents;
		
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
			//TODO reset the translate point.
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

	@Override
	public ArrayList<Node> getPossibleParents(Node agent) {
		return currentGeneration;
	}//TODO switch out for a graphbased implemenation

	@Override
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initializeAgent(config, id, randomGenerator);
	
		
		NodeConfiguration sub = config.getParameter(SUB_NODE).getNodeConfiguration();
			
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i = 1; i <= config.getParameter(POPULATION_SIZE).getInteger(); i++) {
			
			Node node = NodeFactory.constructPopulationNode(sub);
			node.initializeAgent(sub, NodeFactory.nextNodeID++, randomGenerator);
			nodes.add(node);
			
		}
		currentGeneration = nodes;
		
		nodes = new ArrayList<Node>();
		for (int i = 1; i <= config.getParameter(POPULATION_SIZE).getInteger(); i++) {
			
			Node node = NodeFactory.constructPopulationNode(sub);
			node.initializeAgent(sub, NodeFactory.nextNodeID++, randomGenerator);
			nodes.add(node);
			
		}
		previousGeneration = nodes;
	
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB,
			int id, RandomGenerator randomGenerator) {
		
		System.out.println("In population mode this should never be called.");
		
	}

	
	//TODO extract these to an interface
	@Override
	public void teach(Node agent) {

		System.out.println("Shouldnt be here");
	}

	@Override
	public void learnUtterance(Utterance utterance) {

		System.out.println("Shouldnt be here");
	}

	@Override
	public boolean canStillLearn() {

		System.out.println("Shouldnt be here");
		return false;
	}

	@Override
	public Utterance getRandomUtterance() {
		System.out.println("Shouldnt be here");
		return null;
	}

	@Override
	public void invent() {
		System.out.println("Shouldnt be here");
		
	}

	@Override
	public ArrayList<Agent> getBaseAgents() {
		System.out.println("Shouldnt be here");
		return getCurrentGeneration();
	}

	@Override
	public void communicate(Node partner) {
		System.out.println("Shouldnt be here");
	}

	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		return currentGeneration.get(0).getStatisticsAggregators();
	}

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return currentGeneration.get(0).getVisualizationKeys();
	}
}
