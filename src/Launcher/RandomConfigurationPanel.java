package Launcher;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import AutoConfiguration.BasicConfigurationPanel;

import simulation.RandomGenerator;

@SuppressWarnings("serial")
public class RandomConfigurationPanel extends JPanel {
	
	private BasicConfigurationPanel panel;
	
	public RandomConfigurationPanel(){
		super();
		setBorder(new TitledBorder("Random Number Generator"));
		panel = new RandomGenerator().getConfigurationPanel();
		add(panel);
	}

	public RandomGenerator getGenerator(){
		return new RandomGenerator(panel.getConfiguration());
	}
}
