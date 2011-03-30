package model;

public class ModelConfiguration {

	public enum AgentType { OriginalAgent, BiasAgent, SynonymAgent, TestAgent }
	public enum PopulationModelType {OriginalPopulationModel, TestModel}
	
	public static final AgentType DEFAULT_AGENT_TYPE = AgentType.OriginalAgent;
	public static final PopulationModelType DEFAULT_POPULATION_MODEL = PopulationModelType.OriginalPopulationModel;
	
	public static final int DEFAULT_GENERATION_COUNT = 10000;
	public static final int DEFAULT_POPULATION_SIZE = 200; //Should be 200
	public static final int DEFAULT_BASE_FITNESS = 1;
	public static final int DEFAULT_COMMUNICATIONS_PER_NEIGHBOUR = 6;
	public static final int DEFAULT_CRITICAL_PERIOD = 200; //Number of utterances available to learners
	
	public AgentType agentType;
	public PopulationModelType populationModelType;
	
	public int generationCount;
	public int populationSize;
	
	public int baseFitness;
	public int communicationsPerNeighbour;
	
	public int criticalPeriod;
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
	
	@Override
	public String toString(){
		
		return "AgentType: " + agentType + "   GenerationCount: " + generationCount + "   PopulationSize: " + populationSize + "   CriticalPeriod: " + criticalPeriod;
	}
}

