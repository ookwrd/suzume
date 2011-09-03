package populationNodes.Agents;

import populationNodes.NodeConfiguration;
import simulation.RandomGenerator;
import AutoConfiguration.Configurable.Describable;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Node;

public class AlteredAgent extends YamauchiHashimoto2010 implements Agent,
		Describable {

	{
		setDefaultParameter("Leftover Resource Multiplier",
				new ConfigurationParameter(1.0));
	}

	private double resourceMultiplier;

	public AlteredAgent() {
	}

	@Override
	public void initializeAgent(Node parentA, Node parentB, int id,
			RandomGenerator randomGenerator) {
		super.initializeAgent(parentA, parentB, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter) config
				.getParameter("Leftover Resource Multiplier")).getDouble();
	}

	@Override
	public void initializeAgent(NodeConfiguration config, int id,
			RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);
		resourceMultiplier = ((ConfigurationParameter) config
				.getParameter("Leftover Resource Multiplier")).getDouble();
	}

	@Override
	public String getName() {
		return "Altered Agent";
	}

	public void adjustCosts() {
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
