package Agents;

public class ConfigurationParameter {

	public enum ConfigurationParameterType {String, Integer, Double, Boolean}
	
	public ConfigurationParameterType type;
	
	public Object value;
	
	public ConfigurationParameter(String value){
		type = ConfigurationParameterType.String;
		this.value = value;
	}
	
	public ConfigurationParameter(Integer value){
		type = ConfigurationParameterType.Integer;
		this.value = value;
	}
	
	public ConfigurationParameter(Double value){
		type = ConfigurationParameterType.Double;
		this.value = value;
	}
	
	public ConfigurationParameter(Boolean value){
		type = ConfigurationParameterType.Boolean;
		this.value = value;
	}
	
	public String getString(){
		return (String)value;
	}
	
	public Integer getInteger(){
		return (Integer)value;
	}
	
	public Double getDouble(){
		return (Double)value;
	}
	
	public Boolean getBoolean(){
		return (Boolean)value;
	}
}
