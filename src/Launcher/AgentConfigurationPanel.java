package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import Agents.AgentConfiguration;
import Agents.AgentConfiguration.AgentType;


@SuppressWarnings("serial")
public class AgentConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	private JTextField mutationRateField;
	
	private JPanel subPanel;
	
	public AgentConfigurationPanel(){
		
		//setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		agentTypesBox = ConfigurationPanelTools.addComboBox("Agent type:", AgentConfiguration.AgentType.values(),this);
		agentTypesBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
		
		reconfigureSubPanel();
		
	}
	
	private JPanel reconfigureSubPanel(){
		
		if(subPanel != null ) {
			remove(subPanel);
		}
		
		subPanel = new JPanel();
		
		ConfigurationPanelTools.configurePanel(""+agentTypesBox.getSelectedItem(), subPanel);
		
		mutationRateField = ConfigurationPanelTools.addField("Mutation Rate", "0.05", subPanel);
		
		ConfigurationPanelTools.makeGrid(subPanel);
		
		add(subPanel);
		
		validate();
		
		return subPanel;
	}
	
	public AgentConfiguration getConfiguration(){
		return new AgentConfiguration((AgentType)agentTypesBox.getSelectedItem(), Double.parseDouble(mutationRateField.getText()));
	}
	
}
