package nodes.Agents;

import nodes.Node;

public interface Agent extends Node {
	
	//Reset Phase
	public void reset();
	
	//Learning Phase
	public boolean canStillLearn(int utterancesSeen);
	
	//Invention Phase
	public void invent();
	
	//Death Phase
	public void killPhase();
	
	//Reproduction Phase
	public boolean isAlive();
	
	public void finalizeFitnessValue(int generation);
}
