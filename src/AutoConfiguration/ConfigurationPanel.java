package AutoConfiguration;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import populationNodes.NodeConfiguration;
import populationNodes.NodeTypeConfigurationPanel;

@SuppressWarnings("serial")
public class ConfigurationPanel extends JPanel {
	
	private GridBagConstraints constraints;
	private int row;
	
	public ConfigurationPanel(){
		setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		row = 0;
	}

	public ConfigurationPanel(String title){
		this();
		setBorder(new TitledBorder(title));
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
