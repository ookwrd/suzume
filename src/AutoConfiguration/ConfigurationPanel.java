package AutoConfiguration;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import AutoConfiguration.Configurable.Describable;

import populationNodes.NodeConfiguration;
import populationNodes.NodeTypeConfigurationPanel;

@SuppressWarnings("serial")
public class ConfigurationPanel extends JPanel {
	
	private GridBagConstraints constraints;
	private int row;
	
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components;
	
	public ConfigurationPanel(Configurable toConfigure, HashMap<String, ConfigurationParameter> parameters){

		setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		row = 0;
		
		if(toConfigure instanceof Describable){
			addTextField(((Describable)toConfigure).getDescription());
		}
		
		this.parameters = parameters;
		components = new HashMap<String, Component>();
		
		//For each specified parameter add the appropriate configuration field to the configuration panel.
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case INTEGER:
				JTextField field = addField(key, parameter.toString());
				components.put(key, field);
				break;
				
			case DOUBLE:
				JTextField field1 = addField(key, parameter.toString());
				components.put(key, field1);
				break;
				
			case LONG:
				JTextField field3 = addField(key, parameter.toString());
				components.put(key, field3);
				break;
				
			case BOOLEAN:
				JCheckBox box = addCheckBox(key, parameter.getBoolean());
				components.put(key, box);
				break;
				
			case STRING:
				JTextField field2 = addField(key, parameter.toString());
				components.put(key, field2);
				break;
				
			case LIST:
				if(parameter.singleSelection){
					JComboBox listBox = addComboBox(key, parameter.getList());
					components.put(key, listBox);
					break;
				}else{
					JList list = addList(key, parameter.getList());
					components.put(key, list);
					break;
				}
				
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
				if(parameter.singleSelection){
				retParameters.put(key, new ConfigurationParameter(
						parameters.get(key).getList(), new Object[]{((JComboBox)comp).getSelectedItem()}));
					break;
				}else{
					retParameters.put(key, new ConfigurationParameter(parameters.get(key).getList(),((JList)comp).getSelectedValues()));
					break;
				}
				
			case NODE:
				retParameters.put(key, new ConfigurationParameter(((NodeTypeConfigurationPanel)comp).getConfiguration()));
				break;
				
			default:
				break;
			}
		
		}
		
		return new BasicConfigurable(retParameters);
	}
	
	protected JTextArea addTextField(String message){
		
		GridBagConstraints constraints = (GridBagConstraints)this.constraints.clone();
		constraints.gridx=0;
		constraints.gridwidth = 2;
		constraints.gridy=row++;
		constraints.weightx = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 5, 5, 5);
		
		JTextArea field = new JTextArea();
		field.setWrapStyleWord(true);
		field.setLineWrap(true);
		field.setBackground(getBackground());
		field.setColumns(20);
		field.setText(message);
		
		add(field, constraints);
		
		return field;
	} 
	
	private void addLabel(String label){
		
		constraints.gridx=0;
		constraints.gridy=row;
		constraints.weightx = 0;
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		add(jLabel, constraints);
	}
	
	public JComboBox addComboBox(String label, Object[] values) {
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.gridy=row++;
		constraints.weightx = 1;
		
		JComboBox comboBox = new JComboBox(values);
		add(comboBox, constraints);
		
		return comboBox;
	}
	
	public JList addList(String label, Object[] values){
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.gridy=row++;
		constraints.weightx = 1;
		
		JList list = new JList(values);
		add(list, constraints);
		
		return list;
		
	} 
	
	public JTextField addField(String label, String initialValue){
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.gridy=row++;
		constraints.weightx = 1;
		
		JTextField field = new JTextField(initialValue, 10);
		add(field, constraints);
		
		return field;
	}
	
	public JCheckBox addCheckBox(String label, boolean initialValue){
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.gridy=row++;
		constraints.weightx = 1;
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(initialValue);
		add(checkBox, constraints);
		
		return checkBox;
	}
	
	public NodeTypeConfigurationPanel addNodeSelector(String label, NodeConfiguration initialValue){
		constraints.gridx=0;
		constraints.gridwidth = 2;
		constraints.gridy=row++;
		constraints.weightx = 1;
		
		NodeTypeConfigurationPanel nodeConfigPanel = new NodeTypeConfigurationPanel(initialValue);
		nodeConfigPanel.setBorder(new TitledBorder(label));
		add(nodeConfigPanel, constraints);
		
		constraints.gridwidth =1;
		
		return nodeConfigPanel;
	}
	
}
