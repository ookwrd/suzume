package PopulationModel;

import java.util.ArrayList;

import populationNodes.NodeConfiguration;
import populationNodes.Utterance;
import populationNodes.Agents.Agent;

import runTimeVisualization.Visualizable;
import simulation.RandomGenerator;
import AutoConfiguration.Configurable;

public interface Node extends Visualizable, Configurable {

	//Initialization
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator);
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator);
	
	//General Properties
	public String getName();
	public NodeConfiguration getConfiguration();
	public int getId();
	
	//Language Learning
	public void teach(Node agent);
	public void learnUtterance(Utterance utterance);
	public boolean canStillLearn();
	public Utterance getRandomUtterance();//TODO is this needed externally?
	
	//Invention Phase
	public void invent();
	
	public void communicate(Node partner);
	
	public ArrayList<Agent> getBaseAgents();
}

