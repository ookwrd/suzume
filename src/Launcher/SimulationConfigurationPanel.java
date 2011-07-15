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
	
	private JPanel innerPanel;
	
	private BasicConfigurationPanel panel;
	
	private JComboBox selectionModelTypeBox;
	
	public SimulationConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Simulation Configuration:"));
		
		agentConfigurationPanel = new NodeTypeConfigurationPanel();
		add(agentConfigurationPanel);
		
		innerPanel = new JPanel();
		add(innerPanel);
		
		ConfigurationDisplayTools.configurePanel(innerPanel);
		
		selectionModelTypeBox= ConfigurationDisplayTools.addComboBox("Selection Model:", SelectionModels.values(), innerPanel);
		
		ConfigurationDisplayTools.makeGrid(innerPanel);
		
		panel = new BasicConfigurationPanel(new SimulationConfiguration());
		add(panel);
	}
	
	public SimulationConfiguration getConfiguration(){
		
		return new SimulationConfiguration(
				agentConfigurationPanel.getConfiguration(),
				(SelectionModels)selectionModelTypeBox.getSelectedItem(),
				panel.getConfiguration());
	}
	
}
