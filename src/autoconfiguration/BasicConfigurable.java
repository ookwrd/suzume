 package autoconfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import autoconfiguration.ConfigurationParameter.ConfigurationParameterType;

public class BasicConfigurable implements Configurable {

	private HashMap<String, ConfigurationParameter> parameters = new LinkedHashMap<String, ConfigurationParameter>();;
	private ArrayList<String> fixedParameters = new ArrayList<String>();

	public BasicConfigurable(){}
	
	public BasicConfigurable(Configurable source) {
		initialize(source);
	}
	
	@Override
	public void initialize(HashMap<String, ConfigurationParameter> parameters) {
		this.parameters = parameters;
	}
	
	public void initialize(Configurable source){
		this.parameters = source.getParameters();
		this.fixedParameters = source.getFixedParameters();
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getParameters(){
		return parameters;
	}
	
	@Override
	public ArrayList<String> getFixedParameters(){
		return fixedParameters;
	}
	
	@Override
	public ConfigurationParameter getParameter(String key){
		return parameters.get(key);
	}
	
	public String getStringParameter(String key){
		return getParameter(key).getString();
	}
	
	public Integer getIntegerParameter(String key){
		return getParameter(key).getInteger();
	}
	
	public Boolean getBooleanParameter(String key){
		return getParameter(key).getBoolean();
	}
	
	public Double getDoubleParameter(String key){
		return getParameter(key).getDouble();
	}
	
	public Long getLongParameter(String key){
		return getParameter(key).getLong();
	}
	
	public Object[] getListParameter(String key){
		return getParameter(key).getSelectedValues();
	}
	
	public Configurable getNodeParameter(String key){
		return getParameter(key).getNodeConfiguration();
	}
	
	public Configurable getGraphParameter(String key){
		return getParameter(key).getGraphConfiguration();
	}
	
	@Override
	public void setFixedParameter(String key, ConfigurationParameter parameter){
		overrideParameter(key, parameter);
		fixParameter(key);
	}
	
	public void fixParameter(String key){
		fixedParameters.add(key);
	}
	
	protected void removeListOptions(String key, Object[] options){
		getParameter(key).removeListOption(options);
	}
	
	protected void setDefaultParameter(String key, ConfigurationParameter parameter){
		if(!parameters.containsKey(key)){
			parameters.put(key, parameter);
		}else if (
				parameter.type == parameters.get(key).type 
				&& (parameter.type == ConfigurationParameterType.LIST)){
			
			parameters.get(key).addListOptions(parameter);
		}else{
			System.err.println("Duplicate parameter key in BasicConfigurable:setDefaultParameter ("+ key + ") for non-list parameter");
		}
	}
	
	protected void setDefaultParameter(String key, String value){
		setDefaultParameter(key, new ConfigurationParameter(value));
	}
	
	protected void setDefaultParameter(String key, Integer value){
		setDefaultParameter(key, new ConfigurationParameter(value));
	}
	
	protected void setDefaultParameter(String key, Boolean value){
		setDefaultParameter(key, new ConfigurationParameter(value));
	}
	
	protected void setDefaultParameter(String key, Double value){
		setDefaultParameter(key, new ConfigurationParameter(value));
	}
	
	protected void setDefaultParameter(String key, Long value){
		setDefaultParameter(key, new ConfigurationParameter(value));
	}
	
	protected void setDefaultParameter(String key, Object[] values, Object[] selected){
		setDefaultParameter(key, new ConfigurationParameter(values,selected));
	}

	protected void setDefaultParameter(String key, Object[] values, Object selected){
		setDefaultParameter(key, new ConfigurationParameter(values,selected));
	}
	
	@Override
	public void overrideParameter(String key, ConfigurationParameter parameter){//TODO can this and setDefault parameter be merged?
		parameters.put(key, parameter);
	}
	
	@Override
	public String toString(){
		String retVal = "\n";
		int i = 1;
		for(Map.Entry<String, ConfigurationParameter> param : parameters.entrySet()){
			retVal += i + " " + param.getKey() + ": " + param.getValue() + "\n";
			i++;
		}
		return retVal;
	}
}
