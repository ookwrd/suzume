package populationNodes;

import populationNodes.NodeConfiguration.NodeType;
import simulation.RandomGenerator;
import PopulationModel.CompositePopulationModel;
import PopulationModel.Node;

public class NodeFactory {

	public static int nextNodeID = 0; // keeps count of all the next nodes from this world
	
	public static Node constructUninitializedNode(NodeType type){
		
		switch (type) {
		case YamauchiHashimoto2010:
			return new YamauchiHashimoto2010();
			
		case AlteredAgent:
			return new AlteredAgent();
			
		case BiasAgent:
			return new BiasAgent();
			
		case FixedProbabilityAgent:
			return new FixedProbabilityAgent();
			
		case ProbabilityAgent:
			return new ProbabalityAgent();
		
		case ConfigurablePopulation:
			return new CompositePopulationModel();
			
	//	case SynonymAgent:
	//		return new SynonymAgent();

		default:
			System.out.println("Unrecognized agent type in AgentFactory.");
			return null;
		}
		
	}
	
	public static Node constructPopulationNode(NodeConfiguration nodeConfig){
		
		Node node = constructUninitializedNode(nodeConfig.type);
		
		//node.initializeAgent(nodeConfig, nextNodeID++, randomGenerator);
		
		return node;
		
	}
	
	public static Node constructPopulationNode(Agent parentA, Agent parentB, RandomGenerator randomGenerator){
		
		NodeType agentType = parentA.getConfiguration().type; 
		
		if(agentType != parentB.getConfiguration().type){
			System.out.println("Agent type of parents do not match in AgentFactory. Sexual reproduction not possible.");
			return null;
		}	
		
		Node returnVal = constructUninitializedNode(agentType);
		
		returnVal.initializeAgent(parentA, parentB, nextNodeID++, randomGenerator);
		return returnVal;
	}
	
	public static NodeConfigurationPanel getConfigurationPanel(NodeType type){
	
		return new NodeConfigurationPanel(type);

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
