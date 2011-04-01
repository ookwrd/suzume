package Agents;

import java.util.HashMap;

import model.RandomGenerator;

public class AlteredAgent extends OriginalAgent implements Agent {
	
	private static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Leftover Resource Multiplier", new ConfigurationParameter(1.0));
	}}; 
	
	private double resourceMultiplier;
	
	public AlteredAgent(){}
	
	public void initializeAgent(OriginalAgent parent1, OriginalAgent parent2, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(parent1, parent2, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter)config.parameters.get("Leftover Resource Multiplier")).getDouble();
	}
	
	public void initializeAgent(AgentConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter)config.parameters.get("Leftover Resource Multiplier")).getDouble();
	}
	
	@Override
	public String getName(){
		return "Altered Agent";
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		defaultParameters.putAll(super.getDefaultParameters());
		return defaultParameters;
	}

	public void adjustCosts() {
		if (learningResource>0) {
			setFitness(getFitness()+(int)(learningResource*resourceMultiplier));
		}
	}

}
