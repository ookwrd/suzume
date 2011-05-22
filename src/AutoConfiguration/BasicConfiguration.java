package AutoConfiguration;

import java.util.HashMap;

public class BasicConfiguration extends AbstractConfigurable {

	public HashMap<String, ConfigurationParameter> parameters = new HashMap<String, ConfigurationParameter>();//TODO change visibility

	public BasicConfiguration(){}
	
	public BasicConfiguration(HashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	public ConfigurationParameter get(String key){
		return parameters.get(key);
	}

	public void put(String key, ConfigurationParameter parameter){
		parameters.put(key, parameter);
	}
}
