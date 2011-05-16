package Agents;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import Agents.NodeConfiguration.AgentType;
import AutoConfiguration.BasicConfigurationPanel;

@SuppressWarnings("serial")
public class AgentConfigurationPanel extends JPanel {
	
	private BasicConfigurationPanel basicPanel;
	
	private AgentType type;
	
	public AgentConfigurationPanel(AgentType type){
		
		this.type = type;
		this.basicPanel = new BasicConfigurationPanel(AgentFactory.constructUninitializedAgent(type));

		setBorder(new TitledBorder(type.toString() + " configuration"));
		this.add(basicPanel);
	}
	
	public NodeConfiguration getConfiguration(){
		return new NodeConfiguration(type, basicPanel.getConfiguration());
	}

}
