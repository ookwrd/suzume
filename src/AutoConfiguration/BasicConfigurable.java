 package AutoConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;

import populationNodes.NodeConfiguration;

import AutoConfiguration.ConfigurationParameter.ConfigurationParameterType;

public class BasicConfigurable implements Configurable {

	private LinkedHashMap<String, ConfigurationParameter> parameters;
	
	public BasicConfigurable(){
		parameters = new LinkedHashMap<String, ConfigurationParameter>();
	}

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
	public HashMap<String, ConfigurationParameter> getParameters(){
		return parameters;
	}
	
	protected String getStringParameter(String key){
		return getParameter(key).getString();
	}
	
	protected Integer getIntegerParameter(String key){
		return getParameter(key).getInteger();
	}
	
	protected Boolean getBooleanParameter(String key){
		return getParameter(key).getBoolean();
	}
	
	protected Double getDoubleParameter(String key){
		return getParameter(key).getDouble();
	}
	
	protected Long getLongParameter(String key){
		return getParameter(key).getLong();
	}
	
	protected Object[] getListParameter(String key){
		return getParameter(key).getSelectedValues();
	}
	
	protected NodeConfiguration getNodeParameter(String key){
		return getParameter(key).getNodeConfiguration();
	}
	
	@Override
	public void setParameter(String key, ConfigurationParameter parameter){
		parameters.put(key, parameter);
	}
	
	protected void setDefaultParameter(String key, ConfigurationParameter parameter){
		if(!parameters.containsKey(key)){
			parameters.put(key, parameter);
		}else if (
				parameter.type == parameters.get(key).type 
				&& (parameter.type == ConfigurationParameterType.LIST)){
			parameters.get(key).addListOptions(parameter);
			
			//TODO check how many are being constructed
		}else{
			System.err.println("Duplicate parameter key ("+ key + ") for non-list parameter");
		}
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(){
		return new ConfigurationPanel(this);
	}
	
}
