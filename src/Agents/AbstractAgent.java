package Agents;

import java.util.ArrayList;



public abstract class AbstractAgent implements Agent {

	protected static final int NUMBER_OF_MEANINGS = 12;
	
	private int fitness;
	private int id;
	
	protected ArrayList<Integer> grammar;
	
	
	public AbstractAgent(int id){
		this.id = id;
		fitness = 0;
		
		grammar = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		for (int j = 0; j < NUMBER_OF_MEANINGS; j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}
	
	@Override
	public int getId() {
		return id;
	}

	public ArrayList<Integer> getGrammar() {
		return grammar;
	}
	
	@Override
	public int numberOfNulls() {
		
		int count = 0;
		
		for(int i = 0; i < NUMBER_OF_MEANINGS; i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
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
	
	@Override
	public boolean canStillLearn(){
		return true;
	}
	
	@Override
	public void invent(){
	}
	
	@Override
	public void teach(Agent learner) {
		learner.learnUtterance(getRandomUtterance());
	}
	
	@Override
	public void communicate(Agent partner){
		
		Utterance utterance = partner.getRandomUtterance();

		//If agent and neighbour agree update fitnes.
		if(!utterance.isNull() && (getGrammar().get(utterance.meaning) == utterance.signal)){
			setFitness(getFitness()+1);
			//partner.setFitness(partner.getFitness()+1); TODO need to implement symetry
		}
	}
}