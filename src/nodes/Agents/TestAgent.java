package nodes.Agents;


public class TestAgent extends AlteredAgent {

	public void killPhase(){
		if(randomGenerator.randomInt(8) == 0){
			killAgent();
		}
	}
	
}
