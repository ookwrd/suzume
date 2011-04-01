package Agents;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import Agents.AgentConfiguration.AgentType;
import Launcher.ConfigurationPanelTools;

public class AgentConfigurationPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private AgentType type;
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components;

	protected JPanel innerPanel;
	
	public AgentConfigurationPanel(AgentType type){
		
		setBorder(new TitledBorder(type.toString() + " configuration"));
		
		innerPanel = new JPanel();
		
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
		
		innerPanel.add(new JLabel("No configuration required.")); 
		
		add(innerPanel);
		
		this.type = type;
		
		//Get default parameter map for this agent type
		parameters = AgentFactory.constructUninitializedAgent(type).getDefaultParameters();
		components = new HashMap<String, Component>();
		
		JPanel autoPanel = new JPanel();
		ConfigurationPanelTools.configurePanel(autoPanel);
		
		//For each specified parameter add the appropriate configuration field to the configuration panel.
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case Integer:
				JTextField field = ConfigurationPanelTools.addField(key, parameter.value.toString(), autoPanel);
				components.put(key, field);
				break;
				
			case Double:
				JTextField field1 = ConfigurationPanelTools.addField(key, parameter.value.toString(), autoPanel);
				components.put(key, field1);
				break;
				
			case Boolean:
				JCheckBox box = ConfigurationPanelTools.addCheckBox(key, parameter.getBoolean(), autoPanel);
				components.put(key, box);
				break;
				
			case String:
				JTextField field2 = ConfigurationPanelTools.addField(key, parameter.getString(), autoPanel);
				components.put(key, field2);
				break;
				
			default:
				System.out.println("Unsupported Configuration Parameter type.");
				break;
			}
			
		}
		
		ConfigurationPanelTools.makeGrid(autoPanel);
		
		add(autoPanel);
		
	}
	
	public AgentConfiguration getConfiguration(){
	
		HashMap<String, ConfigurationParameter> retParameters = new HashMap<String, ConfigurationParameter>();
		
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			Component comp = components.get(key);
			
			switch (parameter.type) {
			
			case Integer:
				retParameters.put(key, new ConfigurationParameter(Integer.parseInt(((JTextField)comp).getText())) );
				break;
			
			case Double:
				retParameters.put(key, new ConfigurationParameter(Double.parseDouble(((JTextField)comp).getText())) );
				break;
			
			case String:
				retParameters.put(key, new ConfigurationParameter(((JTextField)comp).getText()) );
				break;				
				
			case Boolean:
				retParameters.put(key, new ConfigurationParameter(((JCheckBox)comp).isSelected()));
				break;
				
			default:
				break;
			}
		
		}
		
		return new AgentConfiguration(type, retParameters);
	}

}
