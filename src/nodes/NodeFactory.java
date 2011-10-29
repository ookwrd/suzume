package nodes;

import nodes.AbstractNode.NodeType;
import nodes.Agents.Agent;
import nodes.Agents.BiasAgent;
import nodes.Agents.ExtendedYamauchiHashimotoAgent;
import nodes.Agents.ProbabilityAgent;
import nodes.Agents.SynonymAgent;
import nodes.Agents.YamauchiHashimoto2010;
import autoconfiguration.ConfigurationParameter;
import simulation.RandomGenerator;
import PopulationModel.AdvancedConfigurableModel;
import PopulationModel.SimpleConfigurableModel;

public class NodeFactory {

	public static int nextNodeID = 0; // keeps count of all the next nodes from this world
	
	public static Node constructUninitializedNode(NodeType type){
		
		Node retVal;
		
		switch (type) {
		case YamauchiHashimoto2010Agent:
			retVal = new YamauchiHashimoto2010();
			break;
			
		case BiasAgent:
			retVal = new BiasAgent();
			break;
			
		case ExtendedYamauchiHashimoto2010Agent:
			retVal = new ExtendedYamauchiHashimotoAgent();
			break;
			
		case ProbabilityAgent:
			retVal = new ProbabilityAgent();
			break;
		
		case AdvancedConfigurableModel:
			retVal = new AdvancedConfigurableModel();
			break;
			
		case SimpleConfigurableModel:
			retVal = new SimpleConfigurableModel();
			break;
			
		case SynonymAgent:
			retVal = new SynonymAgent();
			break;

		default:
			System.err.println("Unrecognized agent type in AgentFactory.");
			retVal = null;
		}
		
		retVal.setFixedParameter(AbstractNode.NODE_TYPE, new ConfigurationParameter(AbstractNode.NodeType.values(), new Object[]{type}));
		
		//TODO temp until I implement recursive population models hack to only make valid options appear
		switch (type) {
		default:
		case YamauchiHashimoto2010Agent:
		case BiasAgent:
		case ExtendedYamauchiHashimoto2010Agent:
		case ProbabilityAgent:
		case SynonymAgent:
			retVal.getParameter(AbstractNode.NODE_TYPE).removeListOption(new Object[]{
					AbstractNode.NodeType.AdvancedConfigurableModel,
					AbstractNode.NodeType.SimpleConfigurableModel
					});
			break;

		case AdvancedConfigurableModel:
		case SimpleConfigurableModel:
			retVal.getParameter(AbstractNode.NODE_TYPE).removeListOption(new Object[]{
					AbstractNode.NodeType.YamauchiHashimoto2010Agent,
					AbstractNode.NodeType.BiasAgent,
					AbstractNode.NodeType.ExtendedYamauchiHashimoto2010Agent,
					AbstractNode.NodeType.ProbabilityAgent,
					AbstractNode.NodeType.SynonymAgent
			});
			break;
		}
		
		return retVal;	
	}
	
	public static Node constructPopulationNode(Agent parentA, Agent parentB, RandomGenerator randomGenerator){
		
		NodeType type = (NodeType) parentA.getParameter(AbstractNode.NODE_TYPE).getSelectedValue();
		
		if(type != parentB.getParameter(AbstractNode.NODE_TYPE).getSelectedValue()){
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
