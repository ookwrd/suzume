package AutoConfiguration;

import populationNodes.NodeConfiguration;
import PopulationModel.Graph;
import PopulationModel.GraphConfiguration.GraphType;

public class ConfigurationParameter {

	public enum ConfigurationParameterType {String, Integer, Double, Long, Boolean, List, Node, Graph}
	
	public ConfigurationParameterType type;
	
	public Object value;
	
	public ConfigurationParameter(String value){
		type = ConfigurationParameterType.String;
		this.value = value;
	}
	
	public ConfigurationParameter(String[] values){
		type = ConfigurationParameterType.List;
		this.value = values;
	}
	
	public ConfigurationParameter(Integer value){
		type = ConfigurationParameterType.Integer;
		this.value = value;
	}
	
	public ConfigurationParameter(Double value){
		type = ConfigurationParameterType.Double;
		this.value = value;
	}
	
	public ConfigurationParameter(Long value){
		type = ConfigurationParameterType.Long;
		this.value = value;
	}
	
	public ConfigurationParameter(Boolean value){
		type = ConfigurationParameterType.Boolean;
		this.value = value;
	}
	
	/*public ConfigurationParameter(NodeType value){//TODO remove
		type = ConfigurationParameterType.Node;
		this.value = value;
	}*/
	
	public ConfigurationParameter(NodeConfiguration value){
		type = ConfigurationParameterType.Node;
		this.value = value;
	}
	
	public ConfigurationParameter(GraphType value){
		type = ConfigurationParameterType.Graph;
		this.value = value;
	}
	
	public ConfigurationParameter(Graph value){
		type = ConfigurationParameterType.Graph;
		this.value = value;
	}
	
	public String getString(){
		return (String)value;
	}
	
	public String[] getList(){
		return (String[])value;
	}
	
	public Integer getInteger(){
		return (Integer)value;
	}
	
	public Double getDouble(){
		return (Double)value;
	}
	
	public Long getLong(){
		return (Long)value;
	}
	
	public Boolean getBoolean(){
		return (Boolean)value;
	}
	
	/*public NodeType getNodeType(){//TODO remove
		return (NodeType)value;
	}*/
	
	public NodeConfiguration getNodeConfiguration(){
		return (NodeConfiguration)value;
	}
	
	public GraphType getGraphType(){//TODO remove
		return (GraphType)value;
	}
	
	public Graph getGraph(){
		return (Graph)value;
	}
	
	@Override
	public String toString(){
		return value.toString();
	}
}
