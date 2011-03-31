package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import model.RandomGenerator;

@SuppressWarnings("serial")
public class RandomConfigurationPanel extends AbstractConfigurationPanel {
	
	private JCheckBox useRandomSeedBox;
	private JTextField randomSeedField;
	
	public RandomConfigurationPanel(){
		super("Random Number Generator Configuration");
		
		useRandomSeedBox = addCheckBox("Use current time as seed:", true, this);
		useRandomSeedBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomSeedField.setEnabled(!useRandomSeedBox.isSelected());
			}
		});
		
		randomSeedField = addField("Seed:", ""+1, this);
		randomSeedField.setEnabled(false);
		
		makeGrid(this);
	}

	public RandomGenerator getGenerator(){
		
		if(useRandomSeedBox.isSelected()){
			return new RandomGenerator();
		}else{
			return new RandomGenerator(Long.parseLong(randomSeedField.getText()));
		}
		
	}
}
