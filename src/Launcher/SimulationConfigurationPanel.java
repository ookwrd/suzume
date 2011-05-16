package Launcher;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import simulation.SimulationConfiguration;
import simulation.SimulationConfiguration.PopulationModelType;
import simulation.SelectionModel.SelectionModels;

@SuppressWarnings("serial")
public class SimulationConfigurationPanel extends JPanel {

	private NodeTypeConfigurationPanel agentConfigurationPanel;
	
	private JPanel innerPanel;
	
	private JComboBox populationModelTypeBox;
	private JComboBox selectionModelTypeBox;
	
	private JTextField generationCountField;
	private JTextField populationSizeField;
	
	private JTextField baseFitnessField;
	private JTextField communicationPerNeighbourField;
	
	private JTextField criticalPeriodField;

	private JTextField runCountField;
	
	public SimulationConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Simulation Configuration:"));
		
		agentConfigurationPanel = new NodeTypeConfigurationPanel();
		add(agentConfigurationPanel);
		
		innerPanel = new JPanel();
		add(innerPanel);
		
		ConfigurationPanelTools.configurePanel(innerPanel);
		
		populationModelTypeBox = ConfigurationPanelTools.addComboBox("Population Model:", PopulationModelType.values(), innerPanel);
		selectionModelTypeBox= ConfigurationPanelTools.addComboBox("Selection Model:", SelectionModels.values(), innerPanel);
		generationCountField = ConfigurationPanelTools.addField("Number of Generations:", ""+SimulationConfiguration.DEFAULT_GENERATION_COUNT, innerPanel);
		runCountField = ConfigurationPanelTools.addField("Number of Runs", ""+SimulationConfiguration.DEFAULT_RUN_COUNT,innerPanel);
		populationSizeField = ConfigurationPanelTools.addField("Population Size:", ""+SimulationConfiguration.DEFAULT_POPULATION_SIZE, innerPanel); 
		baseFitnessField= ConfigurationPanelTools.addField("Base fitness value:", ""+SimulationConfiguration.DEFAULT_BASE_FITNESS, innerPanel);
		communicationPerNeighbourField = ConfigurationPanelTools.addField("CommunicationsPerNeighbour:", ""+SimulationConfiguration.DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR, innerPanel);
		criticalPeriodField = ConfigurationPanelTools.addField("Critical Period:", ""+SimulationConfiguration.DEFAULT_CRITICAL_PERIOD, innerPanel);
		
		ConfigurationPanelTools.makeGrid(innerPanel);
	}
	
	public SimulationConfiguration getConfiguration(){
		
		return new SimulationConfiguration(
				agentConfigurationPanel.getConfiguration(),
				(PopulationModelType)populationModelTypeBox.getSelectedItem(),
				(SelectionModels)selectionModelTypeBox.getSelectedItem(),
				Integer.parseInt(generationCountField.getText().trim()),
				Integer.parseInt(populationSizeField.getText().trim()),
				Integer.parseInt(baseFitnessField.getText().trim()),
				Integer.parseInt(communicationPerNeighbourField.getText().trim()),
				Integer.parseInt(criticalPeriodField.getText().trim()),
				Integer.parseInt(runCountField.getText().trim()));
	}
	
}
