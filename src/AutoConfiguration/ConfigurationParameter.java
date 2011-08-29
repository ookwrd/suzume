package AutoConfiguration;

import populationNodes.NodeConfiguration;

public class ConfigurationParameter {

	public enum ConfigurationParameterType {STRING, INTEGER, DOUBLE, LONG, BOOLEAN, LIST, NODE}
	
	public ConfigurationParameterType type;
	private Object value;
	
	public ConfigurationParameter(String value){
		type = ConfigurationParameterType.STRING;
		this.value = value;
	}
	
	public ConfigurationParameter(Object[] values){
		type = ConfigurationParameterType.LIST;
		this.value = values;
	}
	
	public ConfigurationParameter(Integer value){
		type = ConfigurationParameterType.INTEGER;
		this.value = value;
	}
	
	public ConfigurationParameter(Double value){
		type = ConfigurationParameterType.DOUBLE;
		this.value = value;
	}
	
	public ConfigurationParameter(Long value){
		type = ConfigurationParameterType.LONG;
		this.value = value;
	}
	
	public ConfigurationParameter(Boolean value){
		type = ConfigurationParameterType.BOOLEAN;
		this.value = value;
	}

	public ConfigurationParameter(NodeConfiguration value){
		type = ConfigurationParameterType.NODE;
		this.value = value;
	}
	
	public String getString(){
		assert(type == ConfigurationParameterType.STRING);
		return (String)value;
	}
	
	public Object[] getList(){
		assert(type == ConfigurationParameterType.LIST);
		return (Object[])value;
	}
	
	public Integer getInteger(){
		assert(type == ConfigurationParameterType.INTEGER);
		return (Integer)value;
	}
	
	public Double getDouble(){
		assert(type == ConfigurationParameterType.DOUBLE);
		return (Double)value;
	}
	
	public Long getLong(){
		assert(type == ConfigurationParameterType.LONG);
		return (Long)value;
	}
	
	public Boolean getBoolean(){
		assert(type == ConfigurationParameterType.BOOLEAN);
		return (Boolean)value;
	}
	
	public NodeConfiguration getNodeConfiguration(){
		assert(type == ConfigurationParameterType.NODE);
		return (NodeConfiguration)value;
	}
	
	@Override
	public String toString(){
		return value.toString();
	}
}
