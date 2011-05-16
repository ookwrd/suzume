package simulation;

import java.util.StringTokenizer;

public class VisualizationConfiguration {
	
	public static final boolean DEFAULT_PRINT_GENERATIONS = true;
	public static final int DEFAULT_PRINT_GENERATIONS_EACH_X = 1000;
	
	public static final boolean DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION = false;
	public static final int DEFAULT_VISUALIZATION_INTERVAL = 1;
	public static final int DEFAULT_VISUALIZATION_PAUSE = 500;
	
	public boolean printGenerations;
	public int printGenerationsEachX;
	
	public boolean enableContinuousVisualization;
	public int visualizationInterval;
	public int visualizationPause;
	
	public VisualizationConfiguration(){
		this.printGenerations = DEFAULT_PRINT_GENERATIONS;
		this.printGenerationsEachX = DEFAULT_PRINT_GENERATIONS_EACH_X;
		this.enableContinuousVisualization = DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION;
		this.visualizationInterval = DEFAULT_VISUALIZATION_INTERVAL;
		this.visualizationPause = DEFAULT_VISUALIZATION_PAUSE;
	}
	
	public VisualizationConfiguration (
			boolean printGenerations, 
			int printGenerationsEachX, 
			boolean enableContinuousVisualization, 
			int visualizationInterval, 
			int visualizationPause)
	{
		this.printGenerations = printGenerations;
		this.printGenerationsEachX = printGenerationsEachX;
		this.enableContinuousVisualization = enableContinuousVisualization;
		this.visualizationInterval = visualizationInterval;
		this.visualizationPause = visualizationPause;
	}
	
	public VisualizationConfiguration(StringTokenizer tokenizer){
		//TODO
	}
	
	public String saveString(){
		return null; //TODO
	}
}
