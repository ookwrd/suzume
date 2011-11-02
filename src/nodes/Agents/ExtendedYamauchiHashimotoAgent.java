package nodes.Agents;

import nodes.AbstractNode;
import nodes.Utterance;
import autoconfiguration.Configurable.Describable;

public class ExtendedYamauchiHashimotoAgent extends YamauchiHashimoto2010 implements Describable {

	protected static final String MATCH_LEARN_PROB = "MatchingLearnProbability";
	protected static final String NON_MATCH_LEARN_PROB = "NonMatchingLearnProbability";
	protected static final String DEDUCT_COST_ON_ATTEMPT = "Deduct Cost on attempt";
	protected static final String LEFTOVER_RESOURCE_MULTIPLIER = "Leftover Resource Multiplier";
	
	public ExtendedYamauchiHashimotoAgent(){
		setDefaultParameter(MATCH_LEARN_PROB, 1.0);
		setDefaultParameter(NON_MATCH_LEARN_PROB, 1.0);
		setDefaultParameter(DEDUCT_COST_ON_ATTEMPT, true);
		setDefaultParameter(LEFTOVER_RESOURCE_MULTIPLIER, 0.0);
	}
	
	@Override
	public void learnUtterance(Utterance u) {
		
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(u.signal == chromosome.get(u.meaning)){//Matches this agents UG
			if(learningResource < getIntegerParameter(LEARNING_COST_ON_MATCH)){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.nextDouble() < getDoubleParameter(MATCH_LEARN_PROB)){
				grammar.set(u.meaning, u.signal);
				learningResource -= getIntegerParameter(LEARNING_COST_ON_MATCH);
			}else if (getBooleanParameter(DEDUCT_COST_ON_ATTEMPT)){//still subtract
				learningResource -= getIntegerParameter(LEARNING_COST_ON_MATCH);
			}
			
		}else{//Doesn't match this agents UG
			if(learningResource < getIntegerParameter(LEARNING_COST_ON_MISMATCH)){
				learningResource = 0;
				return;
			}
			
			if(randomGenerator.nextDouble() < getDoubleParameter(NON_MATCH_LEARN_PROB)){
				grammar.set(u.meaning, u.signal);
				learningResource -= getIntegerParameter(LEARNING_COST_ON_MISMATCH);
			}else if (getBooleanParameter(DEDUCT_COST_ON_ATTEMPT)){
				learningResource -= getIntegerParameter(LEARNING_COST_ON_MISMATCH);
			}
		}
	}
	
	@Override
	public void finalizeFitnessValue() {
		super.finalizeFitnessValue();
		
		if (learningResource > 0) {
			setFitness(getFitness()
					+ learningResource * getDoubleParameter(LEFTOVER_RESOURCE_MULTIPLIER));
		}
	}
	
	@Override
	public String getName(){
		return "Fixed Probability Agent";
	}
	
	@Override
	public String getDescription(){
		return "Extended version of "+ AbstractNode.NodeType.YamauchiHashimoto2010Agent + " which adds several extra configuration options.\n\n" +
				MATCH_LEARN_PROB + ":\n" +
				"Probability of learning when the encountered token matches the agent's UG value. Setting this option to 1.0 results in behaviour " +
				"identical to that of the original agent.\n\n" +
				NON_MATCH_LEARN_PROB +":\n" +
				"Probability of learning when the encountered token doesn't match the agent's UG value. Setting this option to 1.0 results in behaviour" +
				"identical to that of the original agent.\n\n" +
				DEDUCT_COST_ON_ATTEMPT + ":\n" +
				"Determines if the cost of learning is deducted even when learning fails.\n\n" +
				LEFTOVER_RESOURCE_MULTIPLIER + ":\n" +
				"At the completion of the simulation any leftover learning resources are multiplied by this value and added to the agent's fitness score. " +
				"This is intended to reward agents that don't utilize their full learning potential. Setting this option to 0.0 results in behaviour" +
				"identical to that of the original agent.";
	}
	
}
