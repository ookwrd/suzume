package Agents;

import model.RandomGenerator;

public class AlteredAgent extends OriginalAgent implements Agent {
	
	public AlteredAgent(){}
	
	public void initializeAgent(OriginalAgent parent1, OriginalAgent parent2, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(parent1, parent2, id, randomGenerator);
	}
	
	public void initializeAgent(AgentConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);
	}

	protected final static int LEFTOVER_RESOURCE_USE = 1;
	
	@Override
	public String getName(){
		return "Altered Agent";
	}

	public void adjustCosts() {
		if (learningResource>0) {
			setFitness(getFitness()+learningResource*LEFTOVER_RESOURCE_USE);
		}
	}

}
