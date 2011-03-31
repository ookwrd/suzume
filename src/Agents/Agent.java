package Agents;

import java.util.ArrayList;

public interface Agent {

	//General Properties
	public AgentConfiguration getConfiguration();
	
	//Language Learning
	public void teach(Agent agent);
	public void learnUtterance(Utterance utterance);
	public boolean canStillLearn();
	public Utterance getRandomUtterance();//TODO is this needed externally?
	
	//Invention Phase
	public void invent();
	
	//Fitness Calculation
	public void setFitness(int fitness);
	public int getFitness();
	public void communicate(Agent partner);
	public void adjustCosts();
	
	//Statistics
	public double geneGrammarMatch();
	public int numberOfNulls();
	public int learningIntensity(); //TODO how do i make this more general??
	
	//Display
	public void printAgent();
	
	public int getId();
	public ArrayList getChromosome();//TODO get rid of this
	
	
}
