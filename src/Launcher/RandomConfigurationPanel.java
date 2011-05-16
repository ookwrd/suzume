package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simulation.RandomGenerator;


@SuppressWarnings("serial")
public class RandomConfigurationPanel extends JPanel {
	
	private JCheckBox useRandomSeedBox;
	private JTextField randomSeedField;
	
	public RandomConfigurationPanel(){
		ConfigurationPanelTools.configurePanel("Random Number Generator Configuration", this);
		
		useRandomSeedBox = ConfigurationPanelTools.addCheckBox("Use current time as seed:", true, this);
		useRandomSeedBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomSeedField.setEnabled(!useRandomSeedBox.isSelected());
			}
		});
		
		randomSeedField = ConfigurationPanelTools.addField("Seed:", ""+1, this);
		randomSeedField.setEnabled(false);
		
		ConfigurationPanelTools.makeGrid(this);
	}

	public RandomGenerator getGenerator(){
		
		if(useRandomSeedBox.isSelected()){
			return new RandomGenerator();
		}else{
			return new RandomGenerator(Long.parseLong(randomSeedField.getText().trim()));
		}
		
	}
}
