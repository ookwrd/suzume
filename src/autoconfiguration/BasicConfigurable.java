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
	public ConfigurationParameter getParameter(String key){
		return parameters.get(key);
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getParameters(){
		return parameters;
	}
	
	@Override
	public HashMap<String, ConfigurationParameter> getEditableParameters(){
		LinkedHashMap<String, ConfigurationParameter> retVal = new LinkedHashMap<String, ConfigurationParameter>();
		
		for(String key : parameters.keySet()){
			if(!fixedParameters.contains(key)){
				retVal.put(key, parameters.get(key));
			}
		}
		
		return retVal;
	}
	
	@Override
	public ArrayList<String> getFixedParameters(){
		return fixedParameters;
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
	public void setParameter(String key, ConfigurationParameter parameter){//TODO can this and setDefault parameter be merged?
		parameters.put(key, parameter);
	}
	
	@Override
	public void setFixedParameter(String key, ConfigurationParameter parameter){
		setParameter(key, parameter);
		fixParameter(key);
	}
	
	@Override
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
			
			//TODO check how many are being constructed
		}else{
			System.err.println("Duplicate parameter key in BasicConfigurable:setDefaultParameter ("+ key + ") for non-list parameter");
		}
	}
	
	@Override
	public ConfigurationPanel getConfigurationPanel(){
		return new ConfigurationPanel(this);
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
