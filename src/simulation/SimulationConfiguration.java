package simulation;

import simulation.SelectionModel.SelectionModels;

import populationNodes.NodeConfiguration;
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
	
	public static final String LEARN_TO_DISTANCE = "Learn from agents upto max distance:";//TODO remove these should be recursive
	public static final String COMMUNICATE_TO_DISTANCE = "Communicate with agents to distance:";
	public static final String SELECTION_MODEL = "Selection model:";
	
	{
		setDefaultParameter(GENERATION_COUNT, new ConfigurationParameter(5000));
		setDefaultParameter(RUN_COUNT, new ConfigurationParameter(10));
		setDefaultParameter(POPULATION_SIZE, new ConfigurationParameter(200));
		setDefaultParameter(BASE_FITNESS, new ConfigurationParameter(1));
		setDefaultParameter(COMMUNICATIONS_PER_NEIGHBOUR, new ConfigurationParameter(6));
		setDefaultParameter(CRITICAL_PERIOD, new ConfigurationParameter(200));
		setDefaultParameter(LEARN_TO_DISTANCE, new ConfigurationParameter(2));
		setDefaultParameter(COMMUNICATE_TO_DISTANCE, new ConfigurationParameter(1));
		setDefaultParameter(SELECTION_MODEL, new ConfigurationParameter(SelectionModels.values()));
	}
	
	public static final SelectionModels DEFAULT_SELECTION_MODEL = SelectionModels.RouletteWheelSelection;
	
	protected NodeConfiguration agentConfig;
	protected SelectionModels selectionModelType = DEFAULT_SELECTION_MODEL;
	
	
	public SimulationConfiguration(){
		this.agentConfig = new NodeConfiguration();
	}
	
	public SimulationConfiguration(NodeConfiguration agentConfig, 
			BasicConfigurable baseConfig){
		super(baseConfig);
		this.agentConfig = agentConfig;
		this.selectionModelType = SelectionModels.valueOf(getParameter(SELECTION_MODEL).getString());
	}
	
	public String printName(){
		return "" + agentConfig.type + " " + "gen_" + getParameter(GENERATION_COUNT).getInteger() + "run_" + getParameter(RUN_COUNT).getInteger() + "pop_" + getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger() + "crit_" + getParameter(SimulationConfiguration.CRITICAL_PERIOD).getInteger();
	}
	
}

