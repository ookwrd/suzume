package PopulationModel;

import java.util.HashMap;

import AutoConfiguration.ConfigurationParameter;

public class CyclicGraph {

	@SuppressWarnings("serial")
	public static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Include Self Links", new ConfigurationParameter(false));
		put("Max link distance", new ConfigurationParameter(1));
	}};
	
}
