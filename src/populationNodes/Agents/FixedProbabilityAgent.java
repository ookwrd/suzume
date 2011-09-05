package populationNodes.Agents;

import populationNodes.Utterance;
import AutoConfiguration.ConfigurationParameter;
import AutoConfiguration.Configurable.Describable;

public class FixedProbabilityAgent extends YamauchiHashimoto2010 implements Describable {

	private static final String MATCH_LEARN_PROB = "MatchingLearnProbability";
	private static final String NON_MATCH_LEARN_PROB = "NonMatchingLearnProbability";
	private static final String DEDUCT_COST_ON_ATTEMPT = "Deduct Cost on attempt";
	
	{
		setDefaultParameter(MATCH_LEARN_PROB, new ConfigurationParameter(0.7));
		setDefaultParameter(NON_MATCH_LEARN_PROB, new ConfigurationParameter(0.3));
		setDefaultParameter(DEDUCT_COST_ON_ATTEMPT, new ConfigurationParameter(true));	
	} 
	
	@Override
	public void learnUtterance(Utterance u) {
		
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(u.signal == chromosome.get(u.meaning)){//Matches this agents UG
			if(learningResource < config.getParameter(LEARNING_COST_ON_MATCH).getInteger()){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.random() < config.getParameter(MATCH_LEARN_PROB).getDouble()){
				grammar.set(u.meaning, u.signal);
				learningResource -= config.getParameter(LEARNING_COST_ON_MATCH).getInteger();
			}else if (config.getParameter(DEDUCT_COST_ON_ATTEMPT).getBoolean()){//still subtract
				learningResource -= config.getParameter(LEARNING_COST_ON_MATCH).getInteger();
			}
			
		}else{//Doesn't match this agents UG
			if(learningResource < config.getParameter(LEARNING_COST_ON_MISMATCH).getInteger()){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.random() < config.getParameter(NON_MATCH_LEARN_PROB).getDouble()){
				grammar.set(u.meaning, u.signal);
				learningResource -= config.getParameter(LEARNING_COST_ON_MISMATCH).getInteger();
			}else if (config.getParameter(DEDUCT_COST_ON_ATTEMPT).getBoolean()){
				learningResource -= config.getParameter(LEARNING_COST_ON_MISMATCH).getInteger();
			}
		}
		
	}
	
	@Override
	public String getName(){
		return "Fixed Probability Agent";
	}
	
	@Override
	public String getDescription(){
		return "Extended version of YamauchiHashimoto2010 in which learning is not guranteed " +
				"on encountering a particular token.\n\n" +
				"MatchingLearnProbability = probability of learning when token matches UG\n" +
				"NonMatchingLearnProbability = probability of learning when token doesn't match UG\n\n" +
				"Deduct Cost on attempt = does it cost just to attempt? or only to learn?";
	}
	
}
