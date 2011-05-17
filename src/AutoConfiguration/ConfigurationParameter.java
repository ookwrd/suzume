package AutoConfiguration;

import Agents.NodeConfiguration;
import Agents.NodeConfiguration.NodeType;
import PopulationModel.Graph;
import PopulationModel.GraphConfiguration;
import PopulationModel.GraphConfiguration.GraphType;
import PopulationModel.PopulationNode;

public class ConfigurationParameter {

	public enum ConfigurationParameterType {String, Integer, Double, Boolean, List, Graph, Node}
	
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
	
	public ConfigurationParameter(Boolean value){
		type = ConfigurationParameterType.Boolean;
		this.value = value;
	}
	
	public ConfigurationParameter(NodeType value){
		type = ConfigurationParameterType.Node;
		this.value = value;
	}
	
	public ConfigurationParameter(NodeConfiguration value){
		type = ConfigurationParameterType.Node;
		this.value = value;
	}
	
	public ConfigurationParameter(GraphType value){
		type = ConfigurationParameterType.Graph;
		this.value = value;
	}
	
	public ConfigurationParameter(GraphConfiguration value){
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
	
	public Boolean getBoolean(){
		return (Boolean)value;
	}
	
	public NodeType getNodeType(){
		return (NodeType)value;
	}
	
	public NodeConfiguration getNode(){
		return (NodeConfiguration)value;
	}
	
	public GraphType getGraphType(){
		return (GraphType)value;
	}
	
	public GraphConfiguration getGraph(){
		return (GraphConfiguration)value;
	}
}
