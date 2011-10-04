package nodes.Agents;

import nodes.Node;

public interface Agent extends Node {
	
	//Fitness Calculation
	public void setFitness(double fitness);
	public double getFitness();
	public void finalizeFitnessValue();
	
	//Invention Phase
	public void invent();
	
	//Death Phase
	public void killPhase();
	
	//Reproduction Phase
	public boolean isAlive();
}
