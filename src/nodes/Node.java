package nodes;

import java.util.ArrayList;

import nodes.Agents.Agent;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;


import runTimeVisualization.Visualizable;
import simulation.RandomGenerator;
import tools.Pair;

public interface Node extends Visualizable, Configurable {

	public static final String STATISTICS_TYPE = "Statistics Types:";

	//Initialization
	public void initialize(Configurable config, RandomGenerator randomGenerator);
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator);
	
	//General Properties
	public String getName();//TODO check relevance
	public int getId();
	public BasicConfigurable getConfiguration();

	public ArrayList<Agent> getBaseAgents();
	
	//Language Learning
	public void teach(Node agent);
	public void learnUtterance(Utterance utterance);
	
	//Communications Phase
	public void communicate(Node partner);
	
	//Fitness Calculation
	public double getFitness();
	public void finalizeFitnessValue();
	
	//Statistics
	public enum StatisticsCollectionPoint {PostIntialization, PostTraining, PostInvention, PostCommunication, PostFinalizeFitness, PostKilling, PostReproduction}

	public StatisticsAggregator getStatisticsAggregator(Object aggregatorKey);
	public ArrayList<Object> getStatisticsKeys();
	
	public interface StatisticsAggregator{
		public void collectStatistics(StatisticsCollectionPoint point, Node agent);
		public void endGeneration(Integer generation, ArrayList<Agent> agents);
		public void endRun(Integer run, ArrayList<Agent> agents);
		
		public ArrayList<Pair<Double,Double>> getStatistics();
		public String getTitle();
	}
	
}

