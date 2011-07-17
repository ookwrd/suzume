package populationNodes;

import javax.swing.JPanel;

import populationNodes.AbstractNode.NodeType;

import AutoConfiguration.BasicConfigurationPanel;

@SuppressWarnings("serial")
public class NodeConfigurationPanel extends JPanel {
	
	private BasicConfigurationPanel basicPanel;
	
	private NodeType type;
	
	public NodeConfigurationPanel(NodeType type){
		
		this.type = type;
		this.basicPanel = NodeFactory.constructUninitializedNode(type).getConfigurationPanel();

		this.add(basicPanel);
	}
	
	public NodeConfiguration getConfiguration(){
		return new NodeConfiguration(type, basicPanel.getConfiguration());
	}

}
