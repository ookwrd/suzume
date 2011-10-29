package nodes.Agents;

import nodes.Node;
import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;
import autoconfiguration.Configurable.Describable;
import simulation.RandomGenerator;

public class AlteredAgent extends YamauchiHashimoto2010 implements Agent,
		Describable {

	private double resourceMultiplier;

	public AlteredAgent() {
		setDefaultParameter("Leftover Resource Multiplier",
				new ConfigurationParameter(1.0));
	}

	@Override
	public void initializeAgent(Node parentA, Node parentB, int id,
			RandomGenerator randomGenerator) {
		super.initializeAgent(parentA, parentB, id, randomGenerator);
		resourceMultiplier = getDoubleParameter("Leftover Resource Multiplier");
	}

	@Override
	public void initialize(Configurable config, int id,
			RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter) config
				.getParameter("Leftover Resource Multiplier")).getDouble();
	}

	@Override
	public String getName() {
		return "Altered Agent";
	}

	public void finalizeFitnessValue() {
		if (learningResource > 0) {
			setFitness(getFitness()
					+ (int) (learningResource * resourceMultiplier));
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
