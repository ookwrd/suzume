package Launcher;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import model.ModelConfiguration;
import model.ModelConfiguration.PopulationModelType;

@SuppressWarnings("serial")
public class ModelConfigurationPanel extends JPanel {

	private AgentTypeConfigurationPanel agentConfigurationPanel;
	
	private JPanel innerPanel;
	
	private JComboBox populationModelTypeBox;
	
	private JTextField generationCountField;
	private JTextField populationSizeField;
	
	private JTextField baseFitnessField;
	private JTextField communicationPerNeighbourField;
	
	private JTextField criticalPeriodField;

	private JTextField numberRunsField;
	
	public ModelConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Model Configuration:"));
		
		agentConfigurationPanel = new AgentTypeConfigurationPanel();
		add(agentConfigurationPanel);
		
		innerPanel = new JPanel();
		add(innerPanel);
		
		ConfigurationPanelTools.configurePanel(innerPanel);
		
		populationModelTypeBox = ConfigurationPanelTools.addComboBox("Population Model:", ModelConfiguration.PopulationModelType.values(), innerPanel);
		generationCountField = ConfigurationPanelTools.addField("Number of Generations:", ""+ModelConfiguration.DEFAULT_GENERATION_COUNT, innerPanel);
		populationSizeField = ConfigurationPanelTools.addField("Population Size:", ""+ModelConfiguration.DEFAULT_POPULATION_SIZE, innerPanel); 
		baseFitnessField= ConfigurationPanelTools.addField("Base fitness value:", ""+ModelConfiguration.DEFAULT_BASE_FITNESS, innerPanel);
		communicationPerNeighbourField = ConfigurationPanelTools.addField("CommunicationsPerNeighbour:", ""+ModelConfiguration.DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR, innerPanel);
		criticalPeriodField = ConfigurationPanelTools.addField("Critical Period:", ""+ModelConfiguration.DEFAULT_CRITICAL_PERIOD, innerPanel);
		numberRunsField = ConfigurationPanelTools.addField("Rumber of Runs", ""+ModelConfiguration.DEFAULT_NUMBER_RUNS,innerPanel);
		
		ConfigurationPanelTools.makeGrid(innerPanel);
	}
	
	public ModelConfiguration getConfiguration(){
		
		return new ModelConfiguration(
				agentConfigurationPanel.getConfiguration(),
				(PopulationModelType)populationModelTypeBox.getSelectedItem(),
				Integer.parseInt(generationCountField.getText().trim()),
				Integer.parseInt(populationSizeField.getText().trim()),
				Integer.parseInt(baseFitnessField.getText().trim()),
				Integer.parseInt(communicationPerNeighbourField.getText().trim()),
				Integer.parseInt(criticalPeriodField.getText().trim()),
				Integer.parseInt(numberRunsField.getText().trim()));
	}
	
}
