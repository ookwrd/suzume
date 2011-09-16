 package AutoConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;

import AutoConfiguration.ConfigurationParameter.ConfigurationParameterType;

public class BasicConfigurable implements Configurable {

	private LinkedHashMap<String, ConfigurationParameter> parameters = new LinkedHashMap<String, ConfigurationParameter>();
		
	public BasicConfigurable(){}

	public BasicConfigurable(BasicConfigurable source) {
		this.parameters = source.parameters;
	}
	
	public BasicConfigurable(LinkedHashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void initialize(BasicConfigurable source){
		this.parameters = source.parameters;
	}
	
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
		}else if (
				parameter.type == parameters.get(key).type 
				&& (parameter.type == ConfigurationParameterType.SINGLE_LIST 
				|| parameter.type == ConfigurationParameterType.MULTI_LIST)){
			//System.out.println("SHould be joining lists with name " + key);
			
			//TODO check how many are being constructed
		}else{
			System.err.println("Duplicate parameter key ("+ key + ") for non-list parameter");
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
