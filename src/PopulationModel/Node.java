package PopulationModel;

import java.util.ArrayList;

import populationNodes.NodeConfiguration;
import populationNodes.Utterance;
import populationNodes.Agents.Agent;

import runTimeVisualization.Visualizable;
import simulation.RandomGenerator;
import tools.Pair;
import AutoConfiguration.Configurable;

public interface Node extends Visualizable, Configurable {

	//Initialization
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator);
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator);
	
	//General Properties
	public String getName();
	public int getId();
	public NodeConfiguration getConfiguration();
	
	//Language Learning
	public void teach(Node agent);
	public void learnUtterance(Utterance utterance);
	public boolean canStillLearn();
	
	//Invention Phase
	public void invent();
	
	public void communicate(Node partner);
	
	public ArrayList<Agent> getBaseAgents();
	
	//Statistics
	public ArrayList<StatisticsAggregator> getStatisticsAggregators();
	
	public interface StatisticsAggregator{
		//TODO multiple collection points
		public void collectStatistics(Node agent);
		public void endGeneration(Integer generation);
		
		public ArrayList<Pair<Double,Double>> getStatistics();
		public String getTitle();
	}
	
}

