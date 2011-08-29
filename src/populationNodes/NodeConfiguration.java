package populationNodes;

import populationNodes.AbstractNode.NodeType;
import AutoConfiguration.BasicConfigurable;

public class NodeConfiguration extends BasicConfigurable {

	public static final NodeType DEFAULT_AGENT_TYPE = NodeType.YamauchiHashimoto2010;
	
	public NodeType type = DEFAULT_AGENT_TYPE;
	
	public NodeConfiguration(NodeType type, BasicConfigurable baseConfig){
		super(baseConfig);
		this.type = type;
	}

	public String toString(){
		return "Agent Type: " + type;
	}

}

