package Agents;

import simulation.RandomGenerator;
import Agents.NodeConfiguration.AgentType;
import PopulationModel.OriginalPopulationModel;

public class AgentFactory {

	private static int nextAgentID = 0; // keeps count of all the next agents from this world
	
	public static Agent constructUninitializedNode(AgentType type){
		
		switch (type) {
		case YamauchiHashimoto2010:
			return new YamauchiHashimoto2010();
			
		case AlteredAgent:
			return new AlteredAgent();
			
		case BiasAgent:
			return new BiasAgent();
			
		case FixedProbabilityAgent:
			return new FixedProbabilityAgent();
		
		case ConfigurablePopulation:
			return new OriginalPopulationModel();
			
	//	case SynonymAgent:
	//		return new SynonymAgent();

		default:
			System.out.println("Unrecognized agent type in AgentFactory.");
			return null;
		}
		
	}
	
	public static Agent constructAgent(NodeConfiguration agentConfig, RandomGenerator randomGenerator){
		
		Agent agent = constructUninitializedNode(agentConfig.type);
		
		agent.initializeAgent(agentConfig, nextAgentID++, randomGenerator);
		
		return agent;
		
	}
	
	public static Agent constructAgent(Agent parentA, Agent parentB, RandomGenerator randomGenerator){
		
		AgentType agentType = parentA.getConfiguration().type; 
		
		if(agentType != parentB.getConfiguration().type){
			System.out.println("Agent type of parents do not match in AgentFactory. Sexual reproduction not possible.");
			return null;
		}	
		
		Agent returnVal = constructUninitializedNode(agentType);
		
		returnVal.initializeAgent(parentA, parentB, nextAgentID++, randomGenerator);
		return returnVal;
	}
	
	public static AgentConfigurationPanel getConfigurationPanel(AgentType type){
	
		switch (type) {//TODO

		default:
			return new AgentConfigurationPanel(type);
		}
		
	}
	
	/*
	
	public static void main(String[] args){
		
		String className = new AgentFactory().getClass().getName();
		
		System.out.println(className);
		
		Class testClass = null;
		
		try {
			testClass= Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Constructor[] constructors = testClass.getConstructors();
		
		Object[] nl = null;
		
		try {
			Object output = (Object)constructors[0].newInstance(nl);
			
			output.toString();
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
