package AutoConfiguration;

import java.util.HashMap;

public class BasicConfiguration {

	public HashMap<String, ConfigurationParameter> parameters = new HashMap<String, ConfigurationParameter>();

	public BasicConfiguration(){}
	
	public BasicConfiguration(HashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	public ConfigurationParameter get(String key){
		return parameters.get(key);
	}

}
