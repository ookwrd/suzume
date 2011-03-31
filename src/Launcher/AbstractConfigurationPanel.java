package Launcher;

import java.lang.annotation.Target;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class AbstractConfigurationPanel extends JPanel {
	
	//TODO this should be a static utitilities class
	
	public AbstractConfigurationPanel(String title) {
		super();
		setLayout(new SpringLayout());
		setBorder(new TitledBorder(title));
	}
	
	public AbstractConfigurationPanel(String title, JPanel target) {
		super();
		target.setLayout(new SpringLayout());
		target.setBorder(new TitledBorder(title));
	}
	
	protected JComboBox addComboBox(String label, Object[] values, JPanel target) {
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		JComboBox comboBox = new JComboBox(values);
		target.add(comboBox);
		jLabel.setLabelFor(comboBox);
		
		return comboBox;
	}
	
	protected JTextField addField(String label, String initialValue, JPanel target){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		JTextField field = new JTextField(initialValue, 10);
		target.add(field);
		jLabel.setLabelFor(field);
		
		return field;
	}
	
	protected JCheckBox addCheckBox(String label, boolean initialValue, JPanel target){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		target.add(jLabel);
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(initialValue);
		target.add(checkBox);
		jLabel.setLabelFor(checkBox);
		
		return checkBox;
	}
	
	protected void makeGrid(JPanel target){
		SpringUtilities.makeCompactGrid(target,
                target.getComponentCount()/2, 2, 	//rows, cols
                6, 6,        			//initX, initY
                6, 0);      			 //xPad, yPad
	}
}
