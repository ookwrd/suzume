package AutoConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class BasicConfigurable implements Configurable {

	protected LinkedHashMap<String, ConfigurationParameter> defaultParameters = new LinkedHashMap<String, ConfigurationParameter>();

	protected HashMap<String, ConfigurationParameter> parameters = new HashMap<String, ConfigurationParameter>();
		
	public BasicConfigurable(){}

	public BasicConfigurable(BasicConfigurable source) {
		this.parameters = source.parameters;
	}
	
	public BasicConfigurable(HashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public ConfigurationParameter getParameter(String key){//TODO put in interface
		return parameters.get(key);
	}
	
	@Override
	public void setParameter(String key, ConfigurationParameter parameter){
		parameters.put(key, parameter);
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getParameters(){
		return parameters;
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
}
