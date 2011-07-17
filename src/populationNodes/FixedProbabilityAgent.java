package populationNodes;

import AutoConfiguration.ConfigurationParameter;


public class FixedProbabilityAgent extends YamauchiHashimoto2010 {

	{
		setDefaultParameter("Matching Learn Probability", new ConfigurationParameter(0.7));
		setDefaultParameter("NonMatching Learn Probability", new ConfigurationParameter(0.3));
		setDefaultParameter("Deduct Cost on attempt", new ConfigurationParameter(true));	
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
			
			if(randomGenerator.random() < config.getParameter("Matching Learn Probability").getDouble()){
				grammar.set(u.meaning, u.signal);
				learningResource -= config.getParameter(LEARNING_COST_ON_MATCH).getInteger();
			}else if (config.getParameter("Deduct Cost on attempt").getBoolean()){//still subtract
				learningResource -= config.getParameter(LEARNING_COST_ON_MATCH).getInteger();
			}
		}else{//Doesn't match this agents UG
			if(learningResource < config.getParameter(LEARNING_COST_ON_MISMATCH).getInteger()){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.random() < config.getParameter("NonMatching Learn Probability").getDouble()){
				grammar.set(u.meaning, u.signal);
				learningResource -= config.getParameter(LEARNING_COST_ON_MISMATCH).getInteger();
			}else if (config.getParameter("Deduct Cost on attempt").getBoolean()){
				learningResource -= config.getParameter(LEARNING_COST_ON_MISMATCH).getInteger();
			}
		}
		
	}
	
	@Override
	public String getName(){
		return "Fixed Probability Agent";
	}
	
}
