package Agents;

import java.util.HashMap;

import simulation.RandomGenerator;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.PopulationNode;


public class AlteredAgent extends YamauchiHashimoto2010 implements Agent {
	
	@SuppressWarnings("serial")
	private static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Leftover Resource Multiplier", new ConfigurationParameter(1.0));
	}}; 
	
	private double resourceMultiplier;
	
	public AlteredAgent(){}
	
	@Override
	public void initializeAgent(PopulationNode parentA, PopulationNode parentB, int id, RandomGenerator randomGenerator){
		super.initializeAgent(parentA, parentB, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter)config.parameters.get("Leftover Resource Multiplier")).getDouble();
	}
	
	@Override
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
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
			setFitness(getFitness()+(int)(learningResource*resourceMultiplier+10));
		}
	}

}
