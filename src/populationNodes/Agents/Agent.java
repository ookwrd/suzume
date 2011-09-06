package populationNodes.Agents;

import PopulationModel.Node;

public interface Agent extends Node {
	
	//Fitness Calculation
	public void setFitness(double fitness);
	public double getFitness();
	public void adjustFinalFitnessValue();
	
}
