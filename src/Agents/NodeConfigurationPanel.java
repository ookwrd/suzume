package Agents;

import javax.swing.JPanel;

import Agents.NodeConfiguration.NodeType;
import AutoConfiguration.BasicConfigurationPanel;

@SuppressWarnings("serial")
public class NodeConfigurationPanel extends JPanel {
	
	private BasicConfigurationPanel basicPanel;
	
	private NodeType type;
	
	public NodeConfigurationPanel(NodeType type){
		
		this.type = type;
		this.basicPanel = new BasicConfigurationPanel(NodeFactory.constructUninitializedNode(type));

		this.add(basicPanel);
	}
	
	public NodeConfiguration getConfiguration(){
		return new NodeConfiguration(type, basicPanel.getConfiguration());
	}

}
