package Agents;

import java.util.HashMap;

import AutoConfiguration.BasicConfiguration;

public class AgentConfiguration extends BasicConfiguration {

	public enum AgentType { YamauchiHashimoto2010, BiasAgent/*, SynonymAgent*/, AlteredAgent, FixedProbabilityAgent }

	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.YamauchiHashimoto2010;
	
	public AgentType type = DEFAULT_AGENT_TYPE;

	public AgentConfiguration(){
		//*#*
		//TODO maybe I need to get rid of this method?
	}
	
	public AgentConfiguration(AgentType type, BasicConfiguration baseConfig){
		super(baseConfig.parameters);
		this.type = type;
	}

	public String toString(){
		return "Agent Type: " + type;
	}

}

