package Agents;

import java.util.HashMap;

import model.RandomGenerator;

public class FixedProbabilityAgent extends YamauchiHashimoto2010 {

	@SuppressWarnings("serial")
	private static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Matching Learn Probability", new ConfigurationParameter(0.7));
		put("NonMatching Learn Probability", new ConfigurationParameter(0.3));
		put("Deduct Cost on attempt", new ConfigurationParameter(true));	
	}}; 
	
	private double matchingLearnProbability;
	private double nonMatchingLearnProbability;
	private boolean deductOnAttempt;
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		defaultParameters.putAll(super.getDefaultParameters());
		return defaultParameters;
	}
	
	@Override
	public void initializeAgent(AgentConfiguration config, int id, RandomGenerator randomGenerator){
		super.initializeAgent(config, id, randomGenerator);
		initializeParameters(config);
	}
	
	@Override
	public void initializeAgent(Agent parentA, Agent parentB, int id, RandomGenerator randomGenerator){
		super.initializeAgent(parentA, parentB, id, randomGenerator);
		initializeParameters(parentA.getConfiguration());	
		}
	
	private void initializeParameters(AgentConfiguration config){
		matchingLearnProbability = config.parameters.get("Matching Learn Probability").getDouble();
		nonMatchingLearnProbability = config.parameters.get("NonMatching Learn Probability").getDouble();
		deductOnAttempt = config.parameters.get("Deduct Cost on attempt").getBoolean();
	}
	
	@Override
	public void learnUtterance(Utterance u) {
		
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(u.signal == chromosome.get(u.meaning)){//Matches this agents UG
			if(learningResource < matchingLearningCost){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.random() < matchingLearnProbability){
				grammar.set(u.meaning, u.signal);
				learningResource -= matchingLearningCost;
			}else if (deductOnAttempt){//still subtract
				learningResource -= matchingLearningCost;
			}
		}else{//Doesn't match this agents UG
			if(learningResource < nonMatchingLearningCost){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.random() < nonMatchingLearnProbability){
				grammar.set(u.meaning, u.signal);
				learningResource -= nonMatchingLearningCost;
			}else if (deductOnAttempt){
				learningResource -= nonMatchingLearningCost;
			}
		}
		
	}
	
	@Override
	public String getName(){
		return "Fixed Probability Agent";
	}
	
}
