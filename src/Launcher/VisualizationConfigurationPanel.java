package Launcher;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import AutoConfiguration.BasicConfigurationPanel;

import simulation.VisualizationConfiguration;

@SuppressWarnings("serial")
public class VisualizationConfigurationPanel extends JPanel {
	
	BasicConfigurationPanel panel;
	
	public VisualizationConfigurationPanel(){
		super();
		
		setBorder(new TitledBorder("Visualization"));
		
		panel = new BasicConfigurationPanel(new VisualizationConfiguration());
		add(panel);
	}
	
	public VisualizationConfiguration getConfiguration(){
		return new VisualizationConfiguration(panel.getConfiguration());		
	}

}
