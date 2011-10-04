package nodes;

import java.util.ArrayList;

import nodes.Agents.Agent;

import autoconfiguration.Configurable;


import runTimeVisualization.Visualizable;
import simulation.RandomGenerator;
import tools.Pair;

public interface Node extends Visualizable, Configurable {

	//Initialization
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator);
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator);
	
	//General Properties
	public String getName();
	public int getId();
	public NodeConfiguration getConfiguration();

	public ArrayList<Agent> getBaseAgents();
	
	//Language Learning
	public void teach(Node agent);
	public void learnUtterance(Utterance utterance);
	public boolean canStillLearn();
	
	//Communications Phase
	public void communicate(Node partner);
	
	//Statistics
	public enum StatisticsCollectionPoint {PostIntialization, PostTraining, PostInvention, PostCommunication, PostKilling, PostReproduction}
	
	public ArrayList<StatisticsAggregator> getStatisticsAggregators();
	
	public interface StatisticsAggregator{
		public void collectStatistics(StatisticsCollectionPoint point, Node agent);
		public void endGeneration(Integer generation);
		
		public ArrayList<Pair<Double,Double>> getStatistics();
		public String getTitle();
	}
	
}
