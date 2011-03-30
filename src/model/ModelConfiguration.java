package model;

import java.util.StringTokenizer;

public class ModelConfiguration {

	public enum AgentType { OriginalAgent, BiasAgent, SynonymAgent, TestAgent, AlteredAgent }
	public enum PopulationModelType {OriginalPopulationModel, TestModel}
	
	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.OriginalAgent;
	public static final PopulationModelType DEFAULT_POPULATION_MODEL = PopulationModelType.OriginalPopulationModel;
	
	public static final int DEFAULT_GENERATION_COUNT = 10000;
	public static final int DEFAULT_POPULATION_SIZE = 200; //Should be 200
	public static final int DEFAULT_BASE_FITNESS = 1;
	public static final int DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR = 6;
	public static final int DEFAULT_CRITICAL_PERIOD = 200; //Number of utterances available to learners
	
	protected AgentType agentType;
	protected PopulationModelType populationModelType;
	
	protected int generationCount;
	protected int populationSize;
	
	protected int baseFitness;
	protected int communicationsPerNeighbour;
	
	protected int criticalPeriod;
	
	/**
	 * Create a configuration based on the default values.
	 */
	public ModelConfiguration(){
		this.agentType = DEFAULT_AGENT_TYPE;
		this.populationModelType = DEFAULT_POPULATION_MODEL;
		
		this.generationCount = DEFAULT_GENERATION_COUNT;
		this.populationSize = DEFAULT_POPULATION_SIZE;
		
		this.baseFitness = DEFAULT_BASE_FITNESS;
		this.communicationsPerNeighbour = DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR;
		
		this.criticalPeriod= DEFAULT_CRITICAL_PERIOD;
	}
	
	/**
	 * Creates a ModelConfiguration based on a string of the fomat generated by ModelConfiguration.saveString()
	 * 
	 * @param inputString
	 */
	public ModelConfiguration(String inputString){
		StringTokenizer tokenizer = new StringTokenizer(inputString);
		
		if(tokenizer.countTokens()!= 7){
			//Invalid string
			System.err.println("Invalid Input string to ModelConfiguration Constructor");
			return;
		}
		
		this.agentType = AgentType.valueOf(tokenizer.nextToken());
		this.populationModelType = PopulationModelType.valueOf(tokenizer.nextToken());
		
		this.generationCount = Integer.parseInt(tokenizer.nextToken());
		this.populationSize = Integer.parseInt(tokenizer.nextToken());
		
		this.baseFitness = Integer.parseInt(tokenizer.nextToken());
		this.communicationsPerNeighbour = Integer.parseInt(tokenizer.nextToken());
		
		this.criticalPeriod = Integer.parseInt(tokenizer.nextToken());
		
	}
	
	/**
	 * Converts ModelConfiguration to a string suitable for saving.
	 * 
	 * @return
	 */
	public String saveString(){
		return "" + agentType 
		+ " " + populationModelType 
		+ " " + generationCount 
		+ " " + populationSize 
		+ " " + baseFitness
		+ " " + communicationsPerNeighbour
		+ " " + criticalPeriod;
	}
	
	@Override
	public String toString(){
		
		return "AgentType: " + agentType + "   GenerationCount: " + generationCount + "   PopulationSize: " + populationSize + "   CriticalPeriod: " + criticalPeriod;
	}
}

