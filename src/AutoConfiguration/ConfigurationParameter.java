package AutoConfiguration;

import populationNodes.NodeConfiguration;

/**
 * Would be nicer if Java had union types...
 * 
 * @author lukemccrohon
 *
 */
public class ConfigurationParameter {

	public enum ConfigurationParameterType {STRING, INTEGER, DOUBLE, LONG, BOOLEAN, SINGLE_LIST, MULTI_LIST, NODE}
	
	public ConfigurationParameterType type;
	private Object value;
	private Object selected;
	
	public ConfigurationParameter(String value){
		type = ConfigurationParameterType.STRING;
		this.value = value;
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
		
		System.out.println("ConfigurationParameter" + value);
		type = ConfigurationParameterType.NODE;
		this.value = value;
	}
	
	public ConfigurationParameter(Object[] values){
		this(values, values[0]);
	}
	
	public ConfigurationParameter(Object[] values, Object selected){
		type = ConfigurationParameterType.SINGLE_LIST;
		this.value = values;
		this.selected = selected;
	}
	
	public ConfigurationParameter(Object[] values, Object[] selected){
		type = ConfigurationParameterType.MULTI_LIST;
		this.value = values;
		this.selected = selected;
	}
	
	public String getString(){
		assert(type == ConfigurationParameterType.STRING);
		return (String)value;
	}
	
	public Object[] getList(){
		assert(type == ConfigurationParameterType.SINGLE_LIST || type == ConfigurationParameterType.MULTI_LIST);
		return (Object[])value;
	}
	
	public Object getSelectedValue(){
		assert(type == ConfigurationParameterType.SINGLE_LIST);
		return selected;
	}
	
	public Object[] getSelectedValues(){
		assert(type == ConfigurationParameterType.MULTI_LIST);
		return (Object[])selected;
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
