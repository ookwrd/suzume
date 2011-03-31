package Launcher;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import Agents.AgentConfiguration;
import Agents.AgentConfiguration.AgentType;
import model.ModelConfiguration;
import model.ModelConfiguration.PopulationModelType;

@SuppressWarnings("serial")
public class ModelConfigurationPanel extends AbstractConfigurationPanel{

	private AgentConfigurationPanel agentConfigurationPanel;
	
	private JComboBox agentTypesBox;
	private JComboBox populationModelTypeBox;
	
	private JTextField generationCountField;
	private JTextField populationSizeField;
	
	private JTextField baseFitnessField;
	private JTextField communicationPerNeighbourField;
	
	private JTextField criticalPeriodField;
	
	public ModelConfigurationPanel(){
		super("Model Configuration");
		
	//	agentConfigurationPanel = new AgentConfigurationPanel();
		
		agentTypesBox = addComboBox("Agent type:", AgentConfiguration.AgentType.values(),this);
		populationModelTypeBox = addComboBox("Population Model:", ModelConfiguration.PopulationModelType.values(), this);
		generationCountField = addField("Number of Generations:", ""+ModelConfiguration.DEFAULT_GENERATION_COUNT, this);
		populationSizeField = addField("Population Size:", ""+ModelConfiguration.DEFAULT_POPULATION_SIZE, this); 
		baseFitnessField= addField("Base fitness value:", ""+ModelConfiguration.DEFAULT_BASE_FITNESS, this);
		communicationPerNeighbourField = addField("CommunicationsPerNeighbour:", ""+ModelConfiguration.DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR, this);
		criticalPeriodField = addField("Critical Period:", ""+ModelConfiguration.DEFAULT_CRITICAL_PERIOD, this);
		
		makeGrid(this);
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
