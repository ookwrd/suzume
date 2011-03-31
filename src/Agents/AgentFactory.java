package Agents;

import model.RandomGenerator;
import Agents.AgentConfiguration.AgentType;

public class AgentFactory {

	private static int nextAgentID = 0; // keeps count of all the next agents from this world
	
	public static Agent constructAgent(AgentConfiguration agentConfig, RandomGenerator randomGenerator){
		
		AgentType agentType = agentConfig.type;
		
		switch (agentType) {
		case OriginalAgent:
			return new OriginalAgent(agentConfig, nextAgentID++, randomGenerator);
			
		case AlteredAgent:
			return new AlteredAgent(agentConfig, nextAgentID++, randomGenerator);
			
		case BiasAgent:
			return new BiasAgent(agentConfig, nextAgentID++, randomGenerator);
			
		case SynonymAgent:
			return new SynonymAgent(agentConfig, nextAgentID++, SynonymAgent.DEFAULT_MEMEORY_SIZE);

		default:
			System.out.println("Unrecognized agent type in AgentFactory.");
			return null;
		}
		
	}
	
	public static Agent constructAgent(Agent parentA, Agent parentB, RandomGenerator randomGenerator){
		
		AgentType agentType = parentA.getConfiguration().type; 
		
		if(agentType != parentB.getConfiguration().type){
			System.out.println("Agent type of parents do not match in AgentFactory. Sexual reproduction not possible.");
			return null;
		}	
		
		switch (agentType) {
		case OriginalAgent:
			return new OriginalAgent((OriginalAgent)parentA, (OriginalAgent)parentB, nextAgentID++, randomGenerator);
			
		case AlteredAgent:
			return new AlteredAgent((AlteredAgent)parentA, (AlteredAgent)parentB, nextAgentID++, randomGenerator);
			
		case BiasAgent:
			return new BiasAgent((BiasAgent)parentA, (BiasAgent)parentB, nextAgentID++, randomGenerator);
			
		case SynonymAgent:
			return new SynonymAgent((SynonymAgent)parentA, (SynonymAgent)parentB, nextAgentID++);

		default:
			System.out.println("Unrecognized agent type in AgentFactory.");
			return null;
		}
	}
}
