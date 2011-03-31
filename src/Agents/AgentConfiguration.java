package Agents;

import java.util.StringTokenizer;

public class AgentConfiguration {

	public enum AgentType { OriginalAgent, BiasAgent, SynonymAgent, AlteredAgent }

	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.OriginalAgent;
	
	public static final double DEFAULT_MUTATION_RATE = 0.05;
	
	public AgentType type;
	public double mutationRate;
	
	public AgentConfiguration(){
		this.type = DEFAULT_AGENT_TYPE;
		this.mutationRate = DEFAULT_MUTATION_RATE;
	}
	
	public AgentConfiguration(AgentType agentType, double mutationRate){
		this.type = agentType;
		this.mutationRate = mutationRate;
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

