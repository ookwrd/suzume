package Agents;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import Agents.AgentConfiguration.AgentType;
import Launcher.ConfigurationPanelTools;

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
		
		innerPanel.add(new JLabel("No configuration required.")); 
		
		add(innerPanel);
		
		this.type = type;
		
		HashMap<String, ConfigurationParameter> parameters = AgentFactory.constructUninitializedAgent(type).getDefaultParameters();
		
		JPanel autoPanel = new JPanel();
		ConfigurationPanelTools.configurePanel(autoPanel);
		
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case Integer:
				ConfigurationPanelTools.addField(key, parameter.value.toString(), autoPanel);
				break;
				
			case Double:
				ConfigurationPanelTools.addField(key, parameter.value.toString(), autoPanel);
				break;
				
			case Boolean:
				ConfigurationPanelTools.addCheckBox(key, parameter.getBoolean(), autoPanel);
				break;
				
			case String:
				ConfigurationPanelTools.addField(key, parameter.getString(), autoPanel);

			default:
				break;
			}
			
		}
		
		ConfigurationPanelTools.makeGrid(autoPanel);
		
		add(autoPanel);
		
	}
	
	public AgentConfiguration getConfiguration(){
		return new AgentConfiguration(type);
	}

}
