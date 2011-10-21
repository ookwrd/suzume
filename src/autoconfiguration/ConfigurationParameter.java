package autoconfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import PopulationModel.graphs.Graph;

import nodes.Node;

/**
 * Would be nicer if Java had union types...
 * 
 * @author Luke McCrohon
 *
 */
public class ConfigurationParameter {

	public enum ConfigurationParameterType {STRING, INTEGER, DOUBLE, LONG, BOOLEAN, NODE, GRAPH, LIST}
	
	public ConfigurationParameterType type;
	private Object value;
	private Object[] selected;
	
	public boolean singleSelection = true;
	
	//Used by clone
	private ConfigurationParameter(){
	}
	
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

	public ConfigurationParameter(Configurable value){
		if(value instanceof Node){
			type = ConfigurationParameterType.NODE;
		}else if (value instanceof Graph){
			type = ConfigurationParameterType.GRAPH;
		}else{
			System.err.println("Unknown type in ConfigurationParameter... fix this" + value.getClass());
		}
		this.value = value;
	}
	
	public ConfigurationParameter(Object[] values){
		this(values, true);
	}
	
	public ConfigurationParameter(Object[] values, boolean singleSelection){
		this(values, new Object[0]);
		this.singleSelection = singleSelection;
		if(singleSelection){
			selected = new Object[]{values[0]};
		}
	}
	
	public ConfigurationParameter(Object[] values, Object selected){
		type = ConfigurationParameterType.LIST;
		this.value = values;
		this.selected = new Object[]{selected};
		this.singleSelection = true;
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
	
	public void removeListOption(Object[] toRemove){
		ArrayList<Object> newVal = new ArrayList<Object>(Arrays.asList((Object[])value));//Wow, the class cast here actually changes the semantics of the method...
		newVal.removeAll(Arrays.asList(toRemove));
		value = newVal.toArray();
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
	
	public Configurable getNodeConfiguration(){
		assert(type == ConfigurationParameterType.NODE);
		return (BasicConfigurable)value;
	}
	
	public Configurable getGraphConfiguration(){
		assert(type == ConfigurationParameterType.GRAPH);
		return (BasicConfigurable)value;
	}
	
	public Object[] getList(){
		assert(type == ConfigurationParameterType.LIST);
		return (Object[])value;
	}
	
	public Object getSelectedValue(){
		assert(type == ConfigurationParameterType.LIST && singleSelection);
		if(selected.length > 0){
			return selected[0];
		}else{
			return ((Object[])value)[0];
		}
	}
	
	public Object[] getSelectedValues(){
		assert(type == ConfigurationParameterType.LIST);
		return selected;
	}
	
	@Override
	public String toString(){
		if(type != ConfigurationParameterType.LIST){
			return value.toString();
		}
		else{
			String retVal = "\n";
			for(Object object : (Object[])value){
				if(Arrays.asList(selected).contains(object)){
					retVal += object + "\t[selected]\n";
				}else{
					retVal += object + "\n";
				}
			}
			return retVal;
		}
	}
	
	public ConfigurationParameter cloneParameter(){
		ConfigurationParameter retVal = new ConfigurationParameter();
		retVal.type = this.type;
		retVal.value = this.value;
		retVal.selected = this.selected;
		retVal.singleSelection = this.singleSelection;
		return retVal;
	}
}
