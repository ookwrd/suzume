package AutoConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class AbstractConfigurable implements Configurable {

	protected LinkedHashMap<String, ConfigurationParameter> defaultParameters = new LinkedHashMap<String, ConfigurationParameter>();

	protected HashMap<String, ConfigurationParameter> parameters = new HashMap<String, ConfigurationParameter>();
		
	public AbstractConfigurable(){}

	public AbstractConfigurable(AbstractConfigurable source) {
		this.parameters = source.parameters;
	}
	
	public AbstractConfigurable(HashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	public ConfigurationParameter get(String key){//TODO put in interface
		return parameters.get(key);
	}

	public void put(String key, ConfigurationParameter parameter){
		parameters.put(key, parameter);
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
}
