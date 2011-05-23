package simulation;

import simulation.SelectionModel.SelectionModels;

import Agents.NodeConfiguration;
import AutoConfiguration.BasicConfigurable;
import AutoConfiguration.ConfigurationParameter;

/**
 * Class for storing Model Configuration parameters.
 * 
 * @author Luke McCrohon
 */
public class SimulationConfiguration extends BasicConfigurable {

	public static final String GENERATION_COUNT = "Number of Generations:";
	public static final String RUN_COUNT = "Number of Runs";
	public static final String POPULATION_SIZE = "Population Size:";
	public static final String BASE_FITNESS = "Base fitness value:";//TODO remove to abstract agent
	public static final String COMMUNICATIONS_PER_NEIGHBOUR = "CommunicationsPerNeighbour:";//TODO remove to population model
	public static final String CRITICAL_PERIOD = "Critical Period:";
	
	{
		defaultParameters.put(GENERATION_COUNT, new ConfigurationParameter(5000));
		defaultParameters.put(RUN_COUNT, new ConfigurationParameter(10));
		defaultParameters.put(POPULATION_SIZE, new ConfigurationParameter(200));
		defaultParameters.put(BASE_FITNESS, new ConfigurationParameter(1));
		defaultParameters.put(COMMUNICATIONS_PER_NEIGHBOUR, new ConfigurationParameter(6));
		defaultParameters.put(CRITICAL_PERIOD, new ConfigurationParameter(200));
	}
	
	public static final SelectionModels DEFAULT_SELECTION_MODEL = SelectionModels.RouletteWheelSelection;
	
	protected NodeConfiguration agentConfig;
	protected SelectionModels selectionModelType = DEFAULT_SELECTION_MODEL;
	
	
	public SimulationConfiguration(){
		this.agentConfig = new NodeConfiguration();//TODO make this top level node config
	}
	
	public SimulationConfiguration(NodeConfiguration agentConfig, 
			SelectionModels selectionModelType,
			BasicConfigurable baseConfig){
		super(baseConfig);
		this.agentConfig = agentConfig;
		this.selectionModelType = selectionModelType;
	}
	
	public String printName(){
		return "" + agentConfig.type + " " + "gen_" + getParameter(GENERATION_COUNT).getInteger() + "run_" + getParameter(RUN_COUNT).getInteger() + "pop_" + getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger() + "crit_" + getParameter(SimulationConfiguration.CRITICAL_PERIOD).getInteger();
	}
	
}

