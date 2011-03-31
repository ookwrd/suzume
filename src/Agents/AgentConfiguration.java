package Agents;

import java.util.StringTokenizer;

public class AgentConfiguration {

	public enum AgentType { OriginalAgent, BiasAgent, SynonymAgent, AlteredAgent }

	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.OriginalAgent;
	

	public AgentType type;
	
	public AgentConfiguration(){
		this.type = DEFAULT_AGENT_TYPE;
	}
	
	public AgentConfiguration(AgentType agentType){
		this.type = agentType;
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

