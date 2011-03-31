package Launcher;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;


public class ConfigurationPanelTools {

	/**
	 * Disallow construction of this class.
	 */
	private ConfigurationPanelTools() {
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
	
	public static void makeGrid(JPanel target){
		SpringUtilities.makeCompactGrid(target,
                target.getComponentCount()/2, 2, 	//rows, cols
                6, 6,        			//initX, initY
                6, 0);      			 //xPad, yPad
	}
}
