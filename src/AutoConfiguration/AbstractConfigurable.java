package AutoConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;


public abstract class AbstractConfigurable implements Configurable {

	protected LinkedHashMap<String, ConfigurationParameter> defaultParameters = new LinkedHashMap<String, ConfigurationParameter>();
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
}
