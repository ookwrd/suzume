package AutoConfiguration;

import populationNodes.NodeConfiguration;

/**
 * Would be nicer if Java had union types...
 * 
 * @author lukemccrohon
 *
 */
public class ConfigurationParameter {

	public enum ConfigurationParameterType {STRING, INTEGER, DOUBLE, LONG, BOOLEAN, LIST, NODE}
	
	public ConfigurationParameterType type;
	private Object value;
	private Object selected;
	
	public boolean singleSelection = true;
	
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
		
		System.out.println("ConfigurationParameter constructor: " + value.getClass());
		type = ConfigurationParameterType.NODE;
		this.value = value;
	}
	
	public ConfigurationParameter(Object[] values){
		this(values, true);
	}
	
	public ConfigurationParameter(Object[] values, boolean singleSelection){
		this(values, new Object[]{values[0]});
		this.singleSelection = singleSelection;
	}
	
	public ConfigurationParameter(Object[] values, Object[] selected){
		type = ConfigurationParameterType.LIST;
		this.value = values;
		this.selected = selected;
		this.singleSelection = false;
	}
	
	public void addListOptions(ConfigurationParameter other){
		assert(type == ConfigurationParameterType.LIST);
		
		//Values
		Object[] extraOptions = other.getList();
		Object[] newArray = new Object[extraOptions.length
				+ ((Object[])value).length];
		System.arraycopy(extraOptions, 0, newArray, 0, extraOptions.length);
		System.arraycopy(((Object[])value), 0, newArray, extraOptions.length,
				((Object[])value).length);
		this.value = newArray;
		
		//Selected
		Object[] extraSelected = other.getSelectedValues();
		Object[] newSelected = new Object[extraSelected.length
				+ ((Object[])selected).length];
		System.arraycopy(extraSelected, 0, newSelected, 0,
				extraSelected.length);
		System.arraycopy(((Object[])selected), 0, newSelected, extraSelected.length,
				((Object[])selected).length);
		this.selected = newSelected;
		
		//Multivalued
		this.singleSelection = singleSelection && other.singleSelection;
	}
	
	public String getString(){
		assert(type == ConfigurationParameterType.STRING);
		return (String)value;
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
	
	public Object[] getList(){
		assert(type == ConfigurationParameterType.LIST);
		return (Object[])value;
	}
	
	public Object getSelectedValue(){
		assert(type == ConfigurationParameterType.LIST && singleSelection);
		return ((Object[])selected)[0];
	}
	
	public Object[] getSelectedValues(){
		assert(type == ConfigurationParameterType.LIST);
		return (Object[])selected;
	}
	
	@Override
	public String toString(){
		return value.toString();
	}
}
