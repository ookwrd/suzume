package AutoConfiguration;

import java.awt.Component;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import AutoConfiguration.Configurable.Describable;

import populationNodes.NodeTypeConfigurationPanel;


@SuppressWarnings("serial")
public class BasicConfigurationPanel extends ConfigurationPanel {
	
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components;
	
	public BasicConfigurationPanel(Configurable toConfigure){
		super();
		
		if(toConfigure instanceof Describable){
			addTextField(((Describable)toConfigure).getDescription());
		}
		
		//Get default parameter map for this agent type
		parameters = toConfigure.getParameters();
		components = new HashMap<String, Component>();
		
		//For each specified parameter add the appropriate configuration field to the configuration panel.
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case INTEGER:
				JTextField field = addField(key, parameter.getInteger().toString());
				components.put(key, field);
				break;
				
			case DOUBLE:
				JTextField field1 = addField(key, parameter.getDouble().toString());
				components.put(key, field1);
				break;
				
			case LONG:
				JTextField field3 = addField(key, parameter.getLong().toString());
				components.put(key, field3);
				break;
				
			case BOOLEAN:
				JCheckBox box = addCheckBox(key, parameter.getBoolean());
				components.put(key, box);
				break;
				
			case STRING:
				JTextField field2 = addField(key, parameter.getString());
				components.put(key, field2);
				break;
				
			case LIST:
				if(parameter.multiple){
					JList list = addList(key, parameter.getList());
					components.put(key, list);
				}else{
					JComboBox listBox = addComboBox(key, parameter.getList());
					components.put(key, listBox);
				}
				break;
				
			case NODE:
				NodeTypeConfigurationPanel panel1 = addNodeSelector(key, parameter.getNodeConfiguration());
				components.put(key, panel1);
				break;
				
			default:
				System.out.println("Unsupported Configuration Parameter type.");
				break;
			}	
		}
		
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
				if(comp instanceof JComboBox){
					retParameters.put(key, new ConfigurationParameter(((JComboBox)comp).getSelectedItem().toString()));
				}else{
					retParameters.put(key, new ConfigurationParameter(((JList)comp).getSelectedValues()));
				}
				break;
				
			case NODE:
				retParameters.put(key, new ConfigurationParameter(((NodeTypeConfigurationPanel)comp).getConfiguration()));
				break;
				
			default:
				break;
			}
		
		}
		
		return new BasicConfigurable(retParameters);
	}
	
}
