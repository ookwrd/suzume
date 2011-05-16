package Agents;

import AutoConfiguration.BasicConfiguration;

public class NodeConfiguration extends BasicConfiguration {

	public enum NodeType { YamauchiHashimoto2010, BiasAgent/*, SynonymAgent*/, AlteredAgent, FixedProbabilityAgent, ConfigurablePopulation }

	public static final NodeType DEFAULT_AGENT_TYPE = NodeType.YamauchiHashimoto2010;
	
	public NodeType type = DEFAULT_AGENT_TYPE;

	public NodeConfiguration(){
		//*#*
		//TODO maybe I need to get rid of this method?
	}
	
	public NodeConfiguration(NodeType type, BasicConfiguration baseConfig){
		super(baseConfig.parameters);
		this.type = type;
	}

	public String toString(){
		return "Agent Type: " + type;
	}

}

