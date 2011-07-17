package populationNodes;

import java.util.HashMap;

import simulation.RandomGenerator;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Node;


public class AlteredAgent extends YamauchiHashimoto2010 implements Agent {
	
	@SuppressWarnings("serial")
	private static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Leftover Resource Multiplier", new ConfigurationParameter(1.0));
	}}; 
	
	private double resourceMultiplier;
	
	public AlteredAgent(){}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator){
		super.initializeAgent(parentA, parentB, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter)config.getParameter("Leftover Resource Multiplier")).getDouble();
	}
	
	@Override
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter)config.getParameter("Leftover Resource Multiplier")).getDouble();
	}
	
	@Override
	public String getName(){
		return "Altered Agent";
	}

	public void adjustCosts() {
		if (learningResource>0) {
			setFitness(getFitness()+(int)(learningResource*resourceMultiplier+10));
		}
	}

}
