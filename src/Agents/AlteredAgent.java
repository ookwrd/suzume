package Agents;

import java.util.ArrayList;

public class AlteredAgent extends OriginalAgent implements Agent {
	
	protected final static int LEFTOVER_RESOURCE_USE = 1;
	
	public AlteredAgent(int id) {
		super(id);
	}

	public void adjustCosts() {
		if (learningResource>0) {
			setFitness(getFitness()+learningResource*LEFTOVER_RESOURCE_USE);
			learningResource=0;
		}
	}

}
