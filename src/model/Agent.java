package model;

import java.util.ArrayList;

public interface Agent {
	
	public void invent();

	//Grammar Learning
	public void teach(Agent agent);
	public void learnUtterance(Utterance utterance);
	public boolean canStillLearn();
	public Utterance getRandomUtterance();//TODO is this needed externally?
	
	//Fitness 
	public void setFitness(int fitness);
	public int getFitness();
	
	
	//Statistics
	public double geneGrammarMatch();
	public int numberOfNulls();
	public int learningIntensity(); //TODO how do i make this more general??
	
	public void printAgent();
	
	public int getId();
	public ArrayList<Integer> getGrammar();//TODO is this needed?
	public ArrayList<Integer> getChromosome();
	
	//public boolean genotypeEquals(Agent agent);	
	
}
