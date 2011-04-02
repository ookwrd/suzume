package Agents;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import Agents.AgentConfiguration.AgentType;

public class AgentConfigurationPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AgentType type;
	protected JPanel innerPanel;
	
	public AgentConfigurationPanel(AgentType type){
		
		setBorder(new TitledBorder(type.toString() + " configuration"));
		
		innerPanel = new JPanel();
		
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		
		this.type = type;
		
		innerPanel.add(new JLabel("No configuration required.")); 
		
		add(innerPanel);
	}
	
	public AgentConfiguration getConfiguration(){
		return new AgentConfiguration(type);
	}

}
