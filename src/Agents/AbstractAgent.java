package Agents;

import java.util.ArrayList;
import java.util.HashMap;

import model.RandomGenerator;

public abstract class AbstractAgent implements Agent {
	
	protected static final int NUMBER_OF_MEANINGS = 12;
	
	protected static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>();
	
	private int fitness;
	private int id;
	protected AgentConfiguration config;
	
	protected ArrayList<Integer> grammar;
	
	public AbstractAgent(){}
	
	public void initializeAgent(AgentConfiguration config, int id, RandomGenerator randomGenerator){
		this.id = id;
		this.config = config;
		this.randomGenerator = randomGenerator;
		fitness = 0;
		
		grammar = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		for (int j = 0; j < NUMBER_OF_MEANINGS; j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getName(){
		return config.type.toString();
	}
	
	@Override
	public String getDescription(){
		return "Undescribed";
	}
	
	@Override
	public AgentConfiguration getConfiguration(){
		return config;
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
	
	@Override
	public void adjustCosts(){
		//Do nothing.
	}
	
	@Override
	public ArrayList getPhenotype(){
		return grammar;
	}

}
