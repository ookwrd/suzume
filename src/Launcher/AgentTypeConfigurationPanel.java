package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import Agents.AgentConfiguration;
import Agents.AgentConfiguration.AgentType;
import Agents.AgentConfigurationPanel;
import Agents.AgentFactory;


@SuppressWarnings("serial")
public class AgentTypeConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	private JTextField mutationRateField;//TODO remove this
	
	private AgentConfigurationPanel subPanel;
	
	public AgentTypeConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel();
		ConfigurationPanelTools.configurePanel(topPanel);
		agentTypesBox = ConfigurationPanelTools.addComboBox("Agent type:", AgentConfiguration.AgentType.values(),topPanel);
		agentTypesBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		ConfigurationPanelTools.makeGrid(topPanel);
		
		add(topPanel);
		
		
		reconfigureSubPanel();
		
	}
	
	private JPanel reconfigureSubPanel(){
		
		if(subPanel != null ) {
			remove(subPanel);
		}
		
		subPanel = AgentFactory.getConfigurationPanel((AgentType)agentTypesBox.getSelectedItem());
			
	/*		new JPanel();
		
		ConfigurationPanelTools.configurePanel(""+agentTypesBox.getSelectedItem(), subPanel);
		
		mutationRateField = ConfigurationPanelTools.addField("Mutation Rate", "0.05", subPanel);
		
		ConfigurationPanelTools.makeGrid(subPanel);
		*/
			
		add(subPanel);
		
		validate();
		
		return subPanel;
	}
	
	public AgentConfiguration getConfiguration(){
		return subPanel.getConfiguration();
	}
	
}
