package Launcher;

import java.awt.Checkbox;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import Agents.NodeConfiguration.NodeType;
import PopulationModel.GraphConfiguration.GraphType;


public class ConfigurationPanelTools {

	/**
	 * Disallow construction of this class.
	 */
	private ConfigurationPanelTools() {
	}
	
	
	public static void configurePanel(JPanel target){
		target.setLayout(new SpringLayout());
	}
	
	public static void configurePanel(String title, JPanel target) {
		target.setLayout(new SpringLayout());
		target.setBorder(new TitledBorder(title));
	}
	
	public static JComboBox addComboBox(String label, Object[] values, JPanel target) {
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		JComboBox comboBox = new JComboBox(values);
		target.add(comboBox);
		jLabel.setLabelFor(comboBox);
		
		return comboBox;
	}
	
	public static JTextField addField(String label, String initialValue, JPanel target){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		JTextField field = new JTextField(initialValue, 10);
		target.add(field);
		jLabel.setLabelFor(field);
		
		return field;
	}
	
	public static JCheckBox addCheckBox(String label, boolean initialValue, JPanel target){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(initialValue);
		target.add(checkBox);
		jLabel.setLabelFor(checkBox);
		
		return checkBox;
	}
	
	public static NodeTypeConfigurationPanel addNodeSelector(String label, NodeType initialValue, JPanel target){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		NodeTypeConfigurationPanel nodeConfigPanel = new NodeTypeConfigurationPanel();
		//TODO initial value
		target.add(nodeConfigPanel);
		jLabel.setLabelFor(nodeConfigPanel);
		
		return nodeConfigPanel;
		
	}
	
	public static GraphTypeConfigurationPanel addGraphSelector(String label, GraphType initialValue, JPanel target){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		GraphTypeConfigurationPanel graphConfigPanel = new GraphTypeConfigurationPanel();
		
		target.add(graphConfigPanel);
		jLabel.setLabelFor(graphConfigPanel);
		
		return graphConfigPanel;
		
	}
	
	public static void makeGrid(JPanel target){
		SpringUtilities.makeCompactGrid(target,
                target.getComponentCount()/2, 2, 	//rows, cols
                6, 6,        			//initX, initY
                6, 0);      			 //xPad, yPad
	}
}
