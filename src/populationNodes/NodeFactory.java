package populationNodes;

import populationNodes.AbstractNode.NodeType;
import populationNodes.Agents.Agent;
import populationNodes.Agents.AlteredAgent;
import populationNodes.Agents.BiasAgent;
import populationNodes.Agents.FixedProbabilityAgent;
import populationNodes.Agents.ProbabalityAgent;
import populationNodes.Agents.YamauchiHashimoto2010;
import simulation.RandomGenerator;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.CompositePopulationModel;
import PopulationModel.Node;

public class NodeFactory {

	public static int nextNodeID = 0; // keeps count of all the next nodes from this world
	
	public static Node constructUninitializedNode(NodeType type){
		
		Node retVal;
		
		switch (type) {
		case YamauchiHashimoto2010:
			retVal = new YamauchiHashimoto2010();
			break;
			
		case AlteredAgent:
			retVal = new AlteredAgent();
			break;
			
		case BiasAgent:
			retVal = new BiasAgent();
			break;
			
		case FixedProbabilityAgent:
			retVal = new FixedProbabilityAgent();
			break;
			
		case ProbabilityAgent:
			retVal = new ProbabalityAgent();
			break;
		
		case ConfigurablePopulation:
			retVal = new CompositePopulationModel();
			break;

		default:
			System.err.println("Unrecognized agent type in AgentFactory.");
			retVal = null;
		}
		
		retVal.setFixedParameter(NodeTypeConfigurationPanel.NODE_TYPE, new ConfigurationParameter(AbstractNode.NodeType.values(), new Object[]{type}));
		
		return retVal;	
	}
	
	public static Node constructPopulationNode(Agent parentA, Agent parentB, RandomGenerator randomGenerator){
		
		NodeType type = (NodeType) parentA.getParameter(NodeTypeConfigurationPanel.NODE_TYPE).getSelectedValue();
		
		if(type != parentB.getParameter(NodeTypeConfigurationPanel.NODE_TYPE).getSelectedValue()){
			System.out.println("Agent type of parents do not match in AgentFactory. Sexual reproduction not possible.");
			return null;
		}	
		
		Node returnVal = constructUninitializedNode(type);
		
		returnVal.initializeAgent(parentA, parentB, nextNodeID++, randomGenerator);
		return returnVal;
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
