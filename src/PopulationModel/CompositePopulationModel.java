package PopulationModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import simulation.PopulationModel;
import simulation.RandomGenerator;

import Agents.AbstractNode;
import Agents.Agent;
import Agents.NodeConfiguration.NodeType;
import Agents.Utterance;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.GraphConfiguration.GraphType;

/**
 * Population model corresponding to a cyclical distribution of agents as seen in:
 * Relaxation of selection, Niche Construction, and the Baldwin Eﬀect in Language 
 * Evolution - Hajime Yamauchi & Takashi Hashimoto
 * 
 * @author Luke McCrohon
 *
 */
public class CompositePopulationModel extends AbstractNode implements PopulationModel {

	public static final String SUB_NODE = "Sub node";
	public static final String REPRODUCTION_GRAPH = "Reproduction Graph";
	public static final String COMMUNICATION_GRAPH = "Communication Graph";
	public static final String LEARNING_GRAPH = "Learning Graph";
	public static final String POPULATION_SIZE = "Population Size";
	public static final String LEARN_TO_DISTANCE = "Learn from agents to distance:";
	public static final String COMMUNICATE_TO_DISTANCE = "Communicate with agents to distance:";
	
	{
		defaultParameters.put(POPULATION_SIZE, new ConfigurationParameter(200));
		defaultParameters.put(LEARNING_GRAPH, new ConfigurationParameter(GraphType.FullyConnected));
		defaultParameters.put(COMMUNICATION_GRAPH, new ConfigurationParameter(GraphType.CyclicGraph));
		defaultParameters.put(REPRODUCTION_GRAPH, new ConfigurationParameter(GraphType.CyclicGraph));
		defaultParameters.put(SUB_NODE, new ConfigurationParameter(NodeType.YamauchiHashimoto2010));
		defaultParameters.put(LEARN_TO_DISTANCE, new ConfigurationParameter(2));
		defaultParameters.put(COMMUNICATE_TO_DISTANCE, new ConfigurationParameter(1));
	}
	
	private Graph learningGraph;
	private Graph communicationGraph;
	private Graph reproductionGraph;
	
	private ArrayList<Node> previousGeneration = new ArrayList<Node>();
	private ArrayList<Node> currentGeneration = new ArrayList<Node>();
	
	public CompositePopulationModel(){}//TODO kill this
	
	public CompositePopulationModel(//TODO use initialization pattern
			ArrayList<Node> currentGeneration, 
			ArrayList<Node> previousGeneration 
			//ModelConfiguration config
			//GraphConfiguration learning, 
			//GraphConfiguration communication, 
			//GraphConfiguration reproduction
			) 
	{
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
	public Dimension getDimension(Dimension baseDimension, VisualizationType type){
		
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
	
	private Dimension getDimensionLayout(Dimension baseDimension, VisualizationType type){
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);
		
		int agentsPerEdge = (size/4)+(size%4!=0?1:0) + 1;
		
		return new Dimension(agentsPerEdge*agentDimension.width, agentsPerEdge*agentDimension.height);
	}
	
	private Dimension getDimensionVertical(Dimension baseDimension, VisualizationType type){
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);
		
		return new Dimension(agentDimension.width, agentDimension.height*size);
	}
		
	
	@Override
	public void draw(Dimension baseDimension, VisualizationType type, Graphics g){
		switch(type){
		case layout:
			drawLayout(baseDimension, type, g);
			break;
			
		case vertical:
			drawVertical(baseDimension, type, g);
			break;
			
		default:
			System.out.println("Unrecognized Visualization type");
			return;
		}
	}
	
	private void drawLayout(Dimension baseDimension, VisualizationType type, Graphics g){
		int size = currentGeneration.size();
		Dimension thisDimension = getDimension(baseDimension, type);
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);

		int agentsPerSection = (size/4)+(size%4!=0?1:0);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, thisDimension.width, thisDimension.height);
		
		//Top edge
		int i;
		for(i = 0; i < agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(agentDimension.width, 0);
		}
		
		//right edge
		for(; i < 2*agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(0, agentDimension.height);
		}
		
		//Bottom edge
		for(; i < 3*agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(-agentDimension.width, 0);
		}
		
		//Left edge
		for(; i < size; i++){
			//TODO reset the translate point.
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(0, -agentDimension.height);
		}
	}
	

	private void drawVertical(Dimension baseDimension, VisualizationType type, Graphics g){
		int size = previousGeneration.size();
		Dimension agentDimension = previousGeneration.get(0).getDimension(baseDimension, type);

		for(int i = 0; i < size; i++){
			previousGeneration.get(i).draw(baseDimension, type, g);
			g.translate(0, agentDimension.height);
		}
	}
	
	/**
	 * Print a generations worth of agents. 
	 */
	@Override
	public void print(){
		System.out.println("Printing Previous Generation");	
		for(Node agent : getAncestorGeneration()){  
			agent.print();
			System.out.println();
		}
	}

	@Override
	public ArrayList<Node> getPossibleParents(Node agent) {
		return previousGeneration;
	}//TODO w�switch out for a graphbased implemenation

	@Override
	public void initializeAgent(Node parentA, Node parentB,
			int id, RandomGenerator randomGenerator) {
		
		System.out.println("In population mode this should never be called.");
		
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
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

}