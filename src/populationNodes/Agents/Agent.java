package populationNodes.Agents;

import PopulationModel.Node;

public interface Agent extends Node {
	
	//Fitness Calculation
	public void setFitness(double fitness);
	public double getFitness();
	public void adjustFinalFitnessValue();
	
	//Statistics
	public Double geneGrammarMatch();
	public Double numberOfNulls();
	public Double learningIntensity(); //TODO how do i make this more general??
	public Object getGenotype();//TODO get rid of this
	public Object getPhenotype();//TODO get rid of this as well
	
}
