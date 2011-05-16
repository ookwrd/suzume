package AutoConfiguration;

import java.util.HashMap;


public abstract class AbstractConfigurable implements Configurable {

	protected static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>();
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
}
