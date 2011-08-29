package Launcher;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import populationNodes.NodeTypeConfigurationPanel;
import AutoConfiguration.BasicConfigurationPanel;

import simulation.SimulationConfiguration;

@SuppressWarnings("serial")
public class SimulationConfigurationPanel extends JPanel {

	private NodeTypeConfigurationPanel agentConfigurationPanel;
	
	private BasicConfigurationPanel panel;
	
	public SimulationConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Simulation Configuration:"));
		
		agentConfigurationPanel = new NodeTypeConfigurationPanel();
		add(agentConfigurationPanel);
		
		panel = new SimulationConfiguration().getConfigurationPanel();
		add(panel);
	}
	
	public SimulationConfiguration getConfiguration(){	
		return new SimulationConfiguration(
				agentConfigurationPanel.getConfiguration(),
				panel.getConfiguration());
	}
	
}
