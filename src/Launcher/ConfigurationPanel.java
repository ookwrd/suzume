package Launcher;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import populationNodes.NodeConfiguration;
import populationNodes.NodeTypeConfigurationPanel;

@SuppressWarnings("serial")
public class ConfigurationPanel extends JPanel {
	
	private GridBagConstraints constraints;
	private int row;
	
	/**
	 * Setup a panel without a title
	 * 
	 * @param target
	 */
	public void configurePanel(){
		setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		row = 0;
	}
	
	/**
	 * Setup a panel with a title
	 * 
	 * @param title
	 * @param target
	 */
	public void configurePanel(String title) {
		configurePanel();
		setBorder(new TitledBorder(title));
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

		addLabel(label);
		
		constraints.gridx=1;
		constraints.gridy=row++;
		constraints.weightx = 1;
		
		NodeTypeConfigurationPanel nodeConfigPanel = new NodeTypeConfigurationPanel();
		//TODO initial value
		add(nodeConfigPanel, constraints);
		
		return nodeConfigPanel;
		
	}
	
	/*public GraphTypeConfigurationPanel addGraphSelector(String label, GraphType initialValue){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		add(jLabel);
		
		GraphTypeConfigurationPanel graphConfigPanel = new GraphTypeConfigurationPanel();
		
		add(graphConfigPanel);
		jLabel.setLabelFor(graphConfigPanel);
		
		return graphConfigPanel;
		
	}*/
}
