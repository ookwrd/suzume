package AutoConfiguration;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Launcher.ConfigurationPanelTools;

@SuppressWarnings("serial")
public class BasicConfigurationPanel extends JPanel {
	
	protected JPanel innerPanel; //TODO is this needed?
	
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components;
	
	public BasicConfigurationPanel(Configurable toConfigure){
		
		//Get default parameter map for this agent type
		parameters = toConfigure.getDefaultParameters();
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
				
			case List:
				JComboBox listBox = ConfigurationPanelTools.addComboBox(key, parameter.getList(), autoPanel);
				components.put(key, listBox);
				break;
				
			default:
				System.out.println("Unsupported Configuration Parameter type.");
				break;
			}
			
		}
		
		ConfigurationPanelTools.makeGrid(autoPanel);
		
		add(autoPanel);
		
		if(parameters.size()==0){
			innerPanel = new JPanel();
			innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
			innerPanel.add(new JLabel("No configuration required.")); 
			add(innerPanel);
		}
	}
	

	public BasicConfiguration getConfiguration(){
		
		HashMap<String, ConfigurationParameter> retParameters = new HashMap<String, ConfigurationParameter>();
		
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			Component comp = components.get(key);
			
			switch (parameter.type) {
			
			case Integer:
				retParameters.put(key, new ConfigurationParameter(Integer.parseInt(((JTextField)comp).getText().trim())) );
				break;
			
			case Double:
				retParameters.put(key, new ConfigurationParameter(Double.parseDouble(((JTextField)comp).getText().trim())) );
				break;
			
			case String:
				retParameters.put(key, new ConfigurationParameter(((JTextField)comp).getText()) );
				break;				
				
			case Boolean:
				retParameters.put(key, new ConfigurationParameter(((JCheckBox)comp).isSelected()));
				break;
				
			case List:
				retParameters.put(key, new ConfigurationParameter((String)((JComboBox)comp).getSelectedItem()));
				break;
				
			default:
				break;
			}
		
		}
		
		return new BasicConfiguration(retParameters);
	}
	
}
