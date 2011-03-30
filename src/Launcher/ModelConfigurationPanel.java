package Launcher;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import model.ModelConfiguration;

@SuppressWarnings("serial")
public class ModelConfigurationPanel extends AbstractConfigurationPanel{

	private JComboBox agentTypesBox;
	private JComboBox populationModelTypeBox;
	
	private JTextField generationCountField;
	private JTextField populationSizeField;
	
	private JTextField baseFitnessField;
	private JTextField communicationPerNeighbourField;
	
	private JTextField criticalPeriodField;
	
	public ModelConfigurationPanel(){
		super("Model Configuration");
		
		agentTypesBox = addComboBox("Agent type:", ModelConfiguration.AgentType.values());
		populationModelTypeBox = addComboBox("Population Model:", ModelConfiguration.PopulationModelType.values());
		generationCountField = addField("Number of Generations:", ""+ModelConfiguration.DEFAULT_GENERATION_COUNT);
		populationSizeField = addField("Population Size:", ""+ModelConfiguration.DEFAULT_POPULATION_SIZE); 
		baseFitnessField= addField("Base fitness value:", ""+ModelConfiguration.DEFAULT_BASE_FITNESS);
		communicationPerNeighbourField = addField("CommunicationsPerNeighbour:", ""+ModelConfiguration.DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR);
		criticalPeriodField = addField("Critical Period:", ""+ModelConfiguration.DEFAULT_CRITICAL_PERIOD);
		
		makeGrid();
	}
	
	public ModelConfiguration getConfiguration(){
		return new ModelConfiguration(); //TODO
	}
	
}
