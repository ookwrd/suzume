package model;

import java.util.ArrayList;

public abstract class AbstractAgent implements Agent {

	protected static final int CHROMOSOME_SIZE = 12;
	
	private int fitness;
	private int id;
	
	protected ArrayList<Integer> grammar;
	
	
	public AbstractAgent(int id){
		this.id = id;
		fitness = 0;
		
		grammar = new ArrayList<Integer>(CHROMOSOME_SIZE);
		for (int j = 0; j < CHROMOSOME_SIZE; j++){
			grammar.add(Utterance.NULL_VALUE);
		}
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public ArrayList<Integer> getGrammar() {
		return grammar;
	}
	
	@Override
	public int numberOfNulls() {
		
		int count = 0;
		
		for(int i = 0; i < CHROMOSOME_SIZE; i++){
			if(grammar.get(i).equals(Utterance.NULL_VALUE)){
				count++;
			}
		}
		
		return count;
	}
	
	@Override
	public int getFitness() {
		return fitness;
	}
	
	@Override
	public void setFitness(int fitness){
		this.fitness = fitness;
	}
	
}
