package nodes.Agents;

import nodes.Node;

public interface Agent extends Node {
	
	//Learning Phase
	public boolean canStillLearn();
	
	//Invention Phase
	public void invent();
	
	//Death Phase
	public void killPhase();
	
	//Reproduction Phase
	public boolean isAlive();
}
