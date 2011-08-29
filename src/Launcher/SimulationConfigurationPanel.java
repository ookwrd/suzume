package Launcher;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import populationNodes.NodeTypeConfigurationPanel;
import AutoConfiguration.BasicConfigurationPanel;

import simulation.SimulationConfiguration;
import simulation.SelectionModel.SelectionModels;

@SuppressWarnings("serial")
public class SimulationConfigurationPanel extends JPanel {

	private NodeTypeConfigurationPanel agentConfigurationPanel;
	
	private ConfigurationPanel innerPanel;
	
	private BasicConfigurationPanel panel;
	
	private JComboBox selectionModelTypeBox;
	
	public SimulationConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Simulation Configuration:"));
		
		agentConfigurationPanel = new NodeTypeConfigurationPanel();
		add(agentConfigurationPanel);
		
		innerPanel = new ConfigurationPanel();
		add(innerPanel);
		
		innerPanel.configurePanel();
		
		selectionModelTypeBox= innerPanel.addComboBox("Selection Model:", SelectionModels.values());
		
		panel = new SimulationConfiguration().getConfigurationPanel();
		add(panel);
	}
	
	public SimulationConfiguration getConfiguration(){	
		return new SimulationConfiguration(
				agentConfigurationPanel.getConfiguration(),
				(SelectionModels)selectionModelTypeBox.getSelectedItem(),
				panel.getConfiguration());
	}
	
}
