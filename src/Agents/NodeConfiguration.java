package Agents;

import AutoConfiguration.BasicConfiguration;

public class NodeConfiguration extends BasicConfiguration {

	public enum AgentType { YamauchiHashimoto2010, BiasAgent/*, SynonymAgent*/, AlteredAgent, FixedProbabilityAgent, ConfigurablePopulation }

	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.YamauchiHashimoto2010;
	
	public AgentType type = DEFAULT_AGENT_TYPE;

	public NodeConfiguration(){
		//*#*
		//TODO maybe I need to get rid of this method?
	}
	
	public NodeConfiguration(AgentType type, BasicConfiguration baseConfig){
		super(baseConfig.parameters);
		this.type = type;
	}

	public String toString(){
		return "Agent Type: " + type;
	}

}

