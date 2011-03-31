package Launcher;

import javax.swing.BoxLayout;
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
		
		ConfigurationPanelTools.makeGrid(innerPanel);
	}
	
	public ModelConfiguration getConfiguration(){
		
		return new ModelConfiguration(
				agentConfigurationPanel.getConfiguration(),
				(PopulationModelType)populationModelTypeBox.getSelectedItem(),
				Integer.parseInt(generationCountField.getText()),
				Integer.parseInt(populationSizeField.getText()),
				Integer.parseInt(baseFitnessField.getText()),
				Integer.parseInt(communicationPerNeighbourField.getText()),
				Integer.parseInt(criticalPeriodField.getText())
				);
	}
	
}
