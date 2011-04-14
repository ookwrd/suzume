package Agents;

import model.RandomGenerator;
import Agents.AgentConfiguration.AgentType;

public class AgentFactory {

	private static int nextAgentID = 0; // keeps count of all the next agents from this world
	
	public static Agent constructUninitializedAgent(AgentType type){
		
		switch (type) {
		case OriginalAgent:
			return new OriginalAgent();
			
		case AlteredAgent:
			return new AlteredAgent();
			
		case BiasAgent:
			return new BiasAgent();
			
		case FixedProbabilityAgent:
			return new FixedProbabilityAgent();
			
	//	case SynonymAgent:
	//		return new SynonymAgent();

		default:
			System.out.println("Unrecognized agent type in AgentFactory.");
			return null;
		}
		
	}
	
	public static Agent constructAgent(AgentConfiguration agentConfig, RandomGenerator randomGenerator){
		
		Agent returnVal = constructUninitializedAgent(agentConfig.type);
		
		returnVal.initializeAgent(agentConfig, nextAgentID++, randomGenerator);
		return returnVal;
		
	}
	
	public static Agent constructAgent(Agent parentA, Agent parentB, RandomGenerator randomGenerator){
		
		AgentType agentType = parentA.getConfiguration().type; 
		
		if(agentType != parentB.getConfiguration().type){
			System.out.println("Agent type of parents do not match in AgentFactory. Sexual reproduction not possible.");
			return null;
		}	
		
		Agent returnVal = constructUninitializedAgent(agentType);
		
		returnVal.initializeAgent(parentA, parentB, nextAgentID++, randomGenerator);
		return returnVal;
	}
	
	public static AgentConfigurationPanel getConfigurationPanel(AgentType type){
	
		switch (type) {

		default:
			return new AgentConfigurationPanel(type);
		}
		
	}
	
	/*public String toString(){
		System.out.println("Sucess!");
		return "Success";
	}
	
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
