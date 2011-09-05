 package AutoConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class BasicConfigurable implements Configurable {

	private LinkedHashMap<String, ConfigurationParameter> parameters = new LinkedHashMap<String, ConfigurationParameter>();
		
	public BasicConfigurable(){}

	public BasicConfigurable(BasicConfigurable source) {
		this.parameters = source.parameters;
	}
	
	public BasicConfigurable(LinkedHashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	//TODO initialization method suitable for the agents to use
	
	@Override
	public ConfigurationParameter getParameter(String key){
		return parameters.get(key);
	}
	
	@Override
	public void setParameter(String key, ConfigurationParameter parameter){
		parameters.put(key, parameter);
	}
	
	@Override
	public void setDefaultParameter(String key, ConfigurationParameter parameter){
		if(!parameters.containsKey(key)){
			parameters.put(key, parameter);
		}
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getParameters(){
		return parameters;
	}
	
	@Override
	public BasicConfigurationPanel getConfigurationPanel(){
		return new BasicConfigurationPanel(this);
	}
	
}
