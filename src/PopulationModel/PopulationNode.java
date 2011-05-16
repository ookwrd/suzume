package PopulationModel;

import java.util.ArrayList;

import runTimeVisualization.Visualizable;
import simulation.RandomGenerator;
import Agents.Agent;
import Agents.NodeConfiguration;
import Agents.Utterance;
import AutoConfiguration.Configurable;

public interface PopulationNode extends Visualizable, Configurable {

	//Initialization
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator);
	public void initializeAgent(PopulationNode parentA, PopulationNode parentB, int id, RandomGenerator randomGenerator);
	
	//General Properties
	public String getName();
	public String getDescription();
	public NodeConfiguration getConfiguration();
	public int getId();
	
	//Language Learning
	public void teach(PopulationNode agent);
	public void learnUtterance(Utterance utterance);
	public boolean canStillLearn();
	public Utterance getRandomUtterance();//TODO is this needed externally?
	
	//Invention Phase
	public void invent();
	

	public void communicate(PopulationNode partner);
	
	public ArrayList<Agent> getBaseAgents();
	
	
}

