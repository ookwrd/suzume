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

import Launcher.ConfigurationPanel;

@SuppressWarnings("serial")
public class BasicConfigurationPanel extends JPanel {
	
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components;
	
	public BasicConfigurationPanel(Configurable toConfigure){
		
		//Get default parameter map for this agent type
		parameters = toConfigure.getParameters();
		components = new HashMap<String, Component>();
		
		ConfigurationPanel autoPanel = new ConfigurationPanel();
		autoPanel.configurePanel();
		
		//For each specified parameter add the appropriate configuration field to the configuration panel.
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case INTEGER:
				JTextField field = autoPanel.addField(key, parameter.getInteger().toString());
				components.put(key, field);
				break;
				
			case DOUBLE:
				JTextField field1 = autoPanel.addField(key, parameter.getDouble().toString());
				components.put(key, field1);
				break;
				
			case LONG:
				JTextField field3 = autoPanel.addField(key, parameter.getLong().toString());
				components.put(key, field3);
				break;
				
			case BOOLEAN:
				JCheckBox box = autoPanel.addCheckBox(key, parameter.getBoolean());
				components.put(key, box);
				break;
				
			case STRING:
				JTextField field2 = autoPanel.addField(key, parameter.getString());
				components.put(key, field2);
				break;
				
			case LIST:
				JComboBox listBox = autoPanel.addComboBox(key, parameter.getList());
				components.put(key, listBox);
				break;
				
			case NODE:
				NodeTypeConfigurationPanel panel1 = autoPanel.addNodeSelector(key, parameter.getNodeConfiguration());
				components.put(key, panel1);
				break;
				
			default:
				System.out.println("Unsupported Configuration Parameter type.");
				break;
			}	
		}
		
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
			
			case INTEGER:
				retParameters.put(key, new ConfigurationParameter(Integer.parseInt(((JTextField)comp).getText().trim())));
				break;
			
			case DOUBLE:
				retParameters.put(key, new ConfigurationParameter(Double.parseDouble(((JTextField)comp).getText().trim())));
				break;
				
			case LONG:
				retParameters.put(key, new ConfigurationParameter(Long.parseLong(((JTextField)comp).getText().trim())));
				break;
			
			case STRING:
				retParameters.put(key, new ConfigurationParameter(((JTextField)comp).getText()) );
				break;				
				
			case BOOLEAN:
				retParameters.put(key, new ConfigurationParameter(((JCheckBox)comp).isSelected()));
				break;
				
			case LIST:
				retParameters.put(key, new ConfigurationParameter(((JComboBox)comp).getSelectedItem().toString()));
				break;
				
			case NODE:
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
