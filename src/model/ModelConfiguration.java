package model;

import java.util.StringTokenizer;

import model.SelectionModel.SelectionModels;

import Agents.AgentConfiguration;

/**
 * Class for storing Model Configuration parameters.
 * 
 * @author Luke McCrohon
 */
public class ModelConfiguration {

	public enum PopulationModelType {OriginalPopulationModel}
	public static final PopulationModelType DEFAULT_POPULATION_MODEL = PopulationModelType.OriginalPopulationModel;
	
	public static final SelectionModels DEFAULT_SELECTION_MODEL = SelectionModels.RouletteWheelSelectionModel;
	
	public static final int DEFAULT_GENERATION_COUNT = 1000;
	public static final int DEFAULT_POPULATION_SIZE = 200;
	
	//The base fitness score assigned to all agents before communication round
	public static final int DEFAULT_BASE_FITNESS = 1;
	
	//Number of communication events with each neighbour during fitness evaluation
	public static final int DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR = 6;
	
	//Maximum number of utterances presented to agents during learning.
	public static final int DEFAULT_CRITICAL_PERIOD = 200; 
	
	public static final int DEFAULT_NUMBER_RUNS = 1;
	
	protected AgentConfiguration agentConfig;
	
	protected PopulationModelType populationModelType;
	protected SelectionModels selectionModelType;
	
	protected int generationCount;
	protected int populationSize;
	
	protected int baseFitness;
	protected int communicationsPerNeighbour;
	
	protected int criticalPeriod; //TODO move into agent.
	
	protected int numberRuns;
	
	/**
	 * Create a configuration based on the default values.
	 */
	public ModelConfiguration(){
		this.agentConfig = new AgentConfiguration();
		this.populationModelType = DEFAULT_POPULATION_MODEL;
		this.selectionModelType = DEFAULT_SELECTION_MODEL;
		
		this.generationCount = DEFAULT_GENERATION_COUNT;
		this.populationSize = DEFAULT_POPULATION_SIZE;
		
		this.baseFitness = DEFAULT_BASE_FITNESS;
		this.communicationsPerNeighbour = DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR;
		
		this.criticalPeriod= DEFAULT_CRITICAL_PERIOD;
		
		this.numberRuns = DEFAULT_NUMBER_RUNS;
	}
	
	/**
	 * Creates ModelConfiguration based on custom settings. 
	 * 
	 * @param agentType
	 * @param populationModelType
	 * @param generationCount
	 * @param populationSize
	 * @param baseFitness
	 * @param communicationsPerNeighbour
	 * @param criticalPeriod
	 */
	public ModelConfiguration(AgentConfiguration agentConfig, 
			PopulationModelType populationModelType, 
			SelectionModels selectionModelType,
			int generationCount, 
			int populationSize, 
			int baseFitness, 
			int communicationsPerNeighbour, 
			int criticalPeriod, 
			int numberRuns){
		this.agentConfig = agentConfig;
		this.populationModelType = populationModelType;
		this.selectionModelType = selectionModelType;
		this.generationCount = generationCount;
		this.populationSize = populationSize;
		this.baseFitness = baseFitness;
		this.communicationsPerNeighbour = communicationsPerNeighbour;
		this.criticalPeriod = criticalPeriod;
		this.numberRuns = numberRuns;
	}
	
	/**
	 * Creates a ModelConfiguration based on a string of the fomat generated by ModelConfiguration.saveString()
	 * 
	 * @param inputString
	 */
	public ModelConfiguration(String inputString){
		StringTokenizer tokenizer = new StringTokenizer(inputString);
		
		this.agentConfig = new AgentConfiguration(tokenizer);
		this.populationModelType = PopulationModelType.valueOf(tokenizer.nextToken());
		this.selectionModelType = SelectionModels.valueOf(tokenizer.nextToken());
		
		this.generationCount = Integer.parseInt(tokenizer.nextToken());
		this.populationSize = Integer.parseInt(tokenizer.nextToken());
		
		this.baseFitness = Integer.parseInt(tokenizer.nextToken());
		this.communicationsPerNeighbour = Integer.parseInt(tokenizer.nextToken());
		
		this.criticalPeriod = Integer.parseInt(tokenizer.nextToken());
		
		this.numberRuns = Integer.parseInt(tokenizer.nextToken());
	}
	
	/**
	 * Converts ModelConfiguration to a string suitable for saving.
	 * 
	 * @return
	 */
	public String saveString(){
		return "" + agentConfig.saveString() //TODO Make this work.
		+ " " + populationModelType 
		+ " " + selectionModelType
		+ " " + generationCount 
		+ " " + populationSize 
		+ " " + baseFitness
		+ " " + communicationsPerNeighbour
		+ " " + criticalPeriod
		+ " " + numberRuns;
	}
	
	
	/**
	 * Outputs a string suitable for display to end user.
	 * 
	 */
	@Override
	public String toString(){
		
		return agentConfig.toString() + 
		"   GenerationCount: " + generationCount + 
		"   PopulationSize: " + populationSize + 
		"   CriticalPeriod: " + criticalPeriod;
	}
	
	
	public String printName(){
		return "" + agentConfig.type + " " + "gen_" + generationCount + "pop_" + populationSize + "crit_" + criticalPeriod;
	}
	
}

