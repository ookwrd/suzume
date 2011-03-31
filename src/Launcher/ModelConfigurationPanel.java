package Launcher;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Agents.AgentConfiguration;
import Agents.AgentConfiguration.AgentType;
import model.ModelConfiguration;
import model.ModelConfiguration.PopulationModelType;

@SuppressWarnings("serial")
public class ModelConfigurationPanel extends JPanel {

	//private AgentConfigurationPanel agentConfigurationPanel;
	
	private JComboBox agentTypesBox;
	private JComboBox populationModelTypeBox;
	
	private JTextField generationCountField;
	private JTextField populationSizeField;
	
	private JTextField baseFitnessField;
	private JTextField communicationPerNeighbourField;
	
	private JTextField criticalPeriodField;
	
	public ModelConfigurationPanel(){
		ConfigurationPanelTools.configurePanel("Model Configuration", this);
		
	//	agentConfigurationPanel = new AgentConfigurationPanel();
		
		agentTypesBox = ConfigurationPanelTools.addComboBox("Agent type:", AgentConfiguration.AgentType.values(),this);
		populationModelTypeBox = ConfigurationPanelTools.addComboBox("Population Model:", ModelConfiguration.PopulationModelType.values(), this);
		generationCountField = ConfigurationPanelTools.addField("Number of Generations:", ""+ModelConfiguration.DEFAULT_GENERATION_COUNT, this);
		populationSizeField = ConfigurationPanelTools.addField("Population Size:", ""+ModelConfiguration.DEFAULT_POPULATION_SIZE, this); 
		baseFitnessField= ConfigurationPanelTools.addField("Base fitness value:", ""+ModelConfiguration.DEFAULT_BASE_FITNESS, this);
		communicationPerNeighbourField = ConfigurationPanelTools.addField("CommunicationsPerNeighbour:", ""+ModelConfiguration.DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR, this);
		criticalPeriodField = ConfigurationPanelTools.addField("Critical Period:", ""+ModelConfiguration.DEFAULT_CRITICAL_PERIOD, this);
		
		ConfigurationPanelTools.makeGrid(this);
	}
	
	public ModelConfiguration getConfiguration(){
		
		return new ModelConfiguration(
				new AgentConfiguration((AgentType)agentTypesBox.getSelectedItem(), 0.05),//TODO
				(PopulationModelType)populationModelTypeBox.getSelectedItem(),
				Integer.parseInt(generationCountField.getText()),
				Integer.parseInt(populationSizeField.getText()),
				Integer.parseInt(baseFitnessField.getText()),
				Integer.parseInt(communicationPerNeighbourField.getText()),
				Integer.parseInt(criticalPeriodField.getText())
				);
	}
	
}
