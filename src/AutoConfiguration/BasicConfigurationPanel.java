package AutoConfiguration;

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import populationNodes.NodeTypeConfigurationPanel;

import Launcher.ConfigurationDisplayTools;
import Launcher.GraphTypeConfigurationPanel;

@SuppressWarnings("serial")
public class BasicConfigurationPanel extends JPanel {
	
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components;
	
	public BasicConfigurationPanel(Configurable toConfigure){
		
		//Get default parameter map for this agent type
		parameters = toConfigure.getParameters();
		components = new HashMap<String, Component>();
		
		JPanel autoPanel = new JPanel();
		ConfigurationDisplayTools.configurePanel(autoPanel);
		
		//For each specified parameter add the appropriate configuration field to the configuration panel.
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case Integer:
				JTextField field = ConfigurationDisplayTools.addField(key, parameter.value.toString(), autoPanel);
				components.put(key, field);
				break;
				
			case Double:
				JTextField field1 = ConfigurationDisplayTools.addField(key, parameter.value.toString(), autoPanel);
				components.put(key, field1);
				break;
				
			case Long:
				JTextField field3 = ConfigurationDisplayTools.addField(key, parameter.value.toString(), autoPanel);
				components.put(key, field3);
				break;
				
			case Boolean:
				JCheckBox box = ConfigurationDisplayTools.addCheckBox(key, parameter.getBoolean(), autoPanel);
				components.put(key, box);
				break;
				
			case String:
				JTextField field2 = ConfigurationDisplayTools.addField(key, parameter.getString(), autoPanel);
				components.put(key, field2);
				break;
				
			case List:
				JComboBox listBox = ConfigurationDisplayTools.addComboBox(key, parameter.getList(), autoPanel);
				components.put(key, listBox);
				break;
				
			case Graph:
				GraphTypeConfigurationPanel panel = ConfigurationDisplayTools.addGraphSelector(key, parameter.getGraphType(), autoPanel);
				components.put(key, panel);
				break;
				
			case Node:
				NodeTypeConfigurationPanel panel1 = ConfigurationDisplayTools.addNodeSelector(key, parameter.getNodeConfiguration(), autoPanel);
				components.put(key, panel1);
				break;
				
			default:
				System.out.println("Unsupported Configuration Parameter type.");
				break;
			}
			
		}
		
		ConfigurationDisplayTools.makeGrid(autoPanel);
		
		add(autoPanel);
		
		if(parameters.size()==0){
			JPanel innerPanel = new JPanel();
			innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
			innerPanel.add(new JLabel("No configuration required.")); 
			add(innerPanel);
		}
	}
	

	public BasicConfigurable getConfiguration(){
		
		LinkedHashMap<String, ConfigurationParameter> retParameters = new LinkedHashMap<String, ConfigurationParameter>();
		
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			Component comp = components.get(key);
			
			switch (parameter.type) {
			
			case Integer:
				retParameters.put(key, new ConfigurationParameter(Integer.parseInt(((JTextField)comp).getText().trim())));
				break;
			
			case Double:
				retParameters.put(key, new ConfigurationParameter(Double.parseDouble(((JTextField)comp).getText().trim())));
				break;
				
			case Long:
				retParameters.put(key, new ConfigurationParameter(Long.parseLong(((JTextField)comp).getText().trim())));
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
				
			case Graph:
				//retParameters.put(key, new ConfigurationParameter(((GraphTypeConfigurationPanel)comp).getConfiguration()));
				//TODO
				break;
				
			case Node:
				retParameters.put(key, new ConfigurationParameter(
						((NodeTypeConfigurationPanel)comp).getConfiguration())
						);
				break;
				
			default:
				break;
			}
		
		}
		
		return new BasicConfigurable(retParameters);
	}
	
}
