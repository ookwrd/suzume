package Agents;

import java.util.HashMap;
import java.util.StringTokenizer;

public class AgentConfiguration {

	public enum AgentType { YamauchiHashimoto2010, BiasAgent/*, SynonymAgent*/, AlteredAgent, FixedProbabilityAgent }

	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.YamauchiHashimoto2010;//See *** below if you change this.
	
	public AgentType type;
	
	public HashMap<String, ConfigurationParameter> parameters = new HashMap<String, ConfigurationParameter>();
	
	public AgentConfiguration(){
		this.type = DEFAULT_AGENT_TYPE;
		//***
		//TODO maybe I need to get rid of this method?
	}
	
	public AgentConfiguration(AgentType agentType, HashMap<String, ConfigurationParameter> parameters){
		this.type = agentType;
		this.parameters = parameters;
	}

	public AgentConfiguration(StringTokenizer tokenizer) {
		// TODO Auto-generated constructor stub
	}

	public String toString(){
		return "Agent Type: " + type;
	}
	
	public String saveString() {
		// TODO Auto-generated method stub
		return null;
	}
}

