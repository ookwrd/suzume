package Agents;

import model.RandomGenerator;

public class AlteredAgent extends OriginalAgent implements Agent {
	
	public AlteredAgent(OriginalAgent parent1, OriginalAgent parent2, int id, RandomGenerator randomGenerator) {
		super(parent1, parent2, id, randomGenerator);
	}
	
	public AlteredAgent(int id, RandomGenerator randomGenerator) {
		super(id, randomGenerator);
	}

	protected final static int LEFTOVER_RESOURCE_USE = 1;
	
	public void adjustCosts() {
		if (learningResource>0) {
			setFitness(getFitness()+learningResource*LEFTOVER_RESOURCE_USE);
			//learningResource=0;
		}
	}

}
