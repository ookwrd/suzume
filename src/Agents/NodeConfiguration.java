package Agents;

import AutoConfiguration.AbstractConfigurable;

public class NodeConfiguration extends AbstractConfigurable {

	public enum NodeType { YamauchiHashimoto2010, BiasAgent/*, SynonymAgent*/, AlteredAgent, FixedProbabilityAgent, ProbabilityAgent, ConfigurablePopulation }

	public static final NodeType DEFAULT_AGENT_TYPE = NodeType.YamauchiHashimoto2010;
	
	public NodeType type = DEFAULT_AGENT_TYPE;

	public NodeConfiguration(){
		//*#*
		//TODO maybe I need to get rid of this method?
	}
	
	public NodeConfiguration(NodeType type, AbstractConfigurable baseConfig){
		super(baseConfig);
		this.type = type;
	}

	public String toString(){
		return "Agent Type: " + type;
	}

}

