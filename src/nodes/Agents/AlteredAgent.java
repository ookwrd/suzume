package nodes.Agents;

import autoconfiguration.ConfigurationParameter;
import autoconfiguration.Configurable.Describable;

public class AlteredAgent extends YamauchiHashimoto2010 implements Agent,
		Describable {

	protected static final String LEFTOVER_RESOURCE_MULTIPLIER = "Leftover Resource Multiplier";

	public AlteredAgent() {
		setDefaultParameter(LEFTOVER_RESOURCE_MULTIPLIER,
				new ConfigurationParameter(1.0));
	}

	@Override
	public String getName() {
		return "Altered Agent";
	}

	public void finalizeFitnessValue() {
		super.finalizeFitnessValue();
		
		if (learningResource > 0) {
			setFitness(getFitness()
					+ (int) (learningResource * getDoubleParameter(LEFTOVER_RESOURCE_MULTIPLIER)));
		}
	}

	@Override
	public String getDescription() {
		return "This agent is a modified version of YamauchiHashimoto2010," +
				" which additionally receives fitness points for any left " +
				"over learning resource it possesses in proportion to " +
				"leftoverResources * leftoverResourceMultiplier";
	}

}
