package populationNodes;

import java.util.ArrayList;

import PopulationModel.Node;


public interface Agent extends Node {
	
	
	
	//Fitness Calculation
	public void setFitness(int fitness);
	public int getFitness();
	public void adjustCosts();
	
	//Statistics
	public double geneGrammarMatch();
	public int numberOfNulls();
	public int learningIntensity(); //TODO how do i make this more general??
	public ArrayList getGenotype();//TODO get rid of this
	public ArrayList getPhenotype();//TODO get rid of this as well
	//Need to refactor how statistics are handled to be more general.
	
}
