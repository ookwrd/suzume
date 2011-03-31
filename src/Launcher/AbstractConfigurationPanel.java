package Launcher;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class AbstractConfigurationPanel extends JPanel {
	
	public AbstractConfigurationPanel(String title) {
		super();
		setLayout(new SpringLayout());
		setBorder(new TitledBorder(title));
		
	
	}
	
	protected JComboBox addComboBox(String label, Object[] values) {
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		add(jLabel);
		
		JComboBox comboBox = new JComboBox(values);
		add(comboBox);
		jLabel.setLabelFor(comboBox);
		
		return comboBox;
	}
	
	protected JTextField addField(String label, String initialValue){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		add(jLabel);
		
		JTextField field = new JTextField(initialValue, 10);
		add(field);
		jLabel.setLabelFor(field);
		
		return field;
	}
	
	protected JCheckBox addCheckBox(String label, boolean initialValue){
		
		JLabel jLabel = new JLabel(label);
		jLabel.setHorizontalAlignment(JLabel.TRAILING);
		add(jLabel);
		
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(initialValue);
		add(checkBox);
		jLabel.setLabelFor(checkBox);
		
		return checkBox;
	}
	
	protected void makeGrid(){
		SpringUtilities.makeCompactGrid(this,
                this.getComponentCount()/2, 2, 	//rows, cols
                6, 6,        			//initX, initY
                6, 0);      			 //xPad, yPad
	}
}
