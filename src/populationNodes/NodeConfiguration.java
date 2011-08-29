package populationNodes;

import populationNodes.AbstractNode.NodeType;
import AutoConfiguration.BasicConfigurable;
import AutoConfiguration.ConfigurationParameter;

public class NodeConfiguration extends BasicConfigurable {

	public static final String NODE_TYPE = "Node type";
	
	{
		setDefaultParameter(NODE_TYPE, new ConfigurationParameter(NodeType.values()));
	}
	
	public NodeConfiguration(){	
	}
	
	public NodeConfiguration(BasicConfigurable baseConfig){
		super(baseConfig);
	}

	public String toString(){
		return "Agent Type: " + getParameter(NODE_TYPE).getString();
	}

}

