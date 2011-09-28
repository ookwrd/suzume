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
	
	private HashMap<String, ConfigurationParameter> parameters;
	private HashMap<String, Component> components = new HashMap<String, Component>();
	
	public ConfigurationPanel(){
		setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
	}
	
	public ConfigurationPanel(Configurable toConfigure){
		this();
		initializeParameters(toConfigure);
	}
	
	public void initializeParameters(Configurable toConfigure){
		
		if(toConfigure instanceof Describable){
			addTextField(((Describable)toConfigure).getDescription());
		}
		
		this.parameters = toConfigure.getParameters();
		
		//For each specified parameter add the appropriate configuration field to the configuration panel.
		for(Map.Entry<String, ConfigurationParameter> entry : parameters.entrySet()){
			
			String key = entry.getKey();
			ConfigurationParameter parameter = entry.getValue();
			
			switch (parameter.type) {
			case INTEGER:
				addField(key, parameter.toString());
				break;
				
			case DOUBLE:
				addField(key, parameter.toString());
				break;
				
			case LONG:
				addField(key, parameter.toString());
				break;
				
			case BOOLEAN:
				addCheckBox(key, parameter.getBoolean());
				break;
				
			case STRING:
				addField(key, parameter.toString());
				break;
				
			case LIST:
				if(parameter.singleSelection){
					addComboBox(key, parameter.getList(), parameter.getSelectedValue());
				}else{
					addList(key, parameter.getList(), parameter.getSelectedValues());
				}
				break;
				
			case NODE:
				addNodeSelector(key, parameter.getNodeConfiguration());
				break;
				
			default:
				System.err.println("Unsupported Configuration Parameter type in ConfigurationPanel:initializeParameters.");
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
				}else{
					retParameters.put(key, new ConfigurationParameter(parameters.get(key).getList(),((JList)comp).getSelectedValues()));
				}
				break;
				
			case NODE:
				System.out.println("Key" + key);
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
	
	protected JComboBox addComboBox(String label, Object[] values, Object selected) {
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.weightx = 1;
		
		JComboBox comboBox = new JComboBox(values);
		comboBox.setSelectedItem(selected);
		add(comboBox, constraints);
		
		components.put(label, comboBox);
		
		return comboBox;
	}
	
	protected JList addList(String label, Object[] values, Object[] selected){
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.weightx = 1;
		
		JList list = new JList(values);
		for(Object item : selected){
			for(int i = 0; i < values.length; i++){
				if(values[i] == item){
					list.addSelectionInterval(i, i);
					break;
				}
			}
		}
		add(list, constraints);
		
		components.put(label, list);
		
		return list;
		
	} 
	
	protected JTextField addField(String label, String initialValue){
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.weightx = 1;
		
		JTextField field = new JTextField(initialValue, 10);
		add(field, constraints);
		
		components.put(label, field);
		
		return field;
	}
	
	protected JCheckBox addCheckBox(String label, boolean initialValue){
		
		addLabel(label);
		
		constraints.gridx=1;
		constraints.weightx = 1;
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(initialValue);
		add(checkBox, constraints);
		
		components.put(label, checkBox);
		
		return checkBox;
	}
	
	protected NodeTypeConfigurationPanel addNodeSelector(String label, NodeConfiguration initialValue){
		constraints.gridx=0;
		constraints.gridwidth = 2;
		constraints.weightx = 1;
		
		NodeTypeConfigurationPanel nodeConfigPanel = new NodeTypeConfigurationPanel(initialValue);
		
		nodeConfigPanel.setBorder(new TitledBorder(label));
		add(nodeConfigPanel, constraints);
		
		constraints.gridwidth =1;
		
		components.put(label, nodeConfigPanel);
		
		return nodeConfigPanel;
	}
	
	protected void addLabel(String label){
		
		constraints.gridx=0;
		constraints.weightx = 0;
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		add(jLabel, constraints);
	}
	
}
