package Agents;

import java.util.ArrayList;

public class AlteredAgent extends OriginalAgent implements Agent {
	
	public AlteredAgent(OriginalAgent parent1, OriginalAgent parent2, int id) {
		super(parent1, parent2, id);
	}
	
	public AlteredAgent(int id) {
		super(id);
	}

	protected final static int LEFTOVER_RESOURCE_USE = 1;
	
	

	public void adjustCosts() {
		if (learningResource>0) {
			setFitness(getFitness()+learningResource*LEFTOVER_RESOURCE_USE);
			learningResource=0;
		}
	}

}
