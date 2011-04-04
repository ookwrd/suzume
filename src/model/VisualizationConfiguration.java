package model;

import java.util.StringTokenizer;

public class VisualizationConfiguration {

	public static final boolean DEFAULT_PRINT_SLICE_GENERATION = false;
	public static final int DEFAULT_SLICE_GENERATION = 500;
	
	public static final boolean DEFAULT_PRINT_GENERATIONS = true;
	public static final int DEFAULT_PRINT_GENERATIONS_EACH_X = 1000;
	
	public static final boolean DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION = false;
	public static final boolean DEFAULT_INTERACTIVE_MODE = true;
	public static final int DEFAULT_VISUALIZATION_INTERVAL = 1;
	public static final int DEFAULT_VISUALIZATION_PAUSE = 500;
	
	public boolean printSliceGeneration;
	public int sliceGeneration;
	
	public boolean printGenerations;
	public int printGenerationsEachX;
	
	public boolean enableContinuousVisualization;
	public boolean interactiveMode;
	public int visualizationInterval;
	public int visualizationPause;
	
	public VisualizationConfiguration(){
		this.printSliceGeneration = DEFAULT_PRINT_SLICE_GENERATION;
		this.sliceGeneration = DEFAULT_SLICE_GENERATION;
		this.printGenerations = DEFAULT_PRINT_GENERATIONS;
		this.printGenerationsEachX = DEFAULT_PRINT_GENERATIONS_EACH_X;
		this.enableContinuousVisualization = DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION;
		this.interactiveMode = DEFAULT_INTERACTIVE_MODE;
		this.visualizationInterval = DEFAULT_VISUALIZATION_INTERVAL;
		this.visualizationPause = DEFAULT_VISUALIZATION_PAUSE;
	}
	
	public VisualizationConfiguration (
			boolean printSliceGeneration, 
			int sliceGeneration, 
			boolean printGenerations, 
			int printGenerationsEachX, 
			boolean enableContinuousVisualization, 
			boolean interactiveMode,
			int visualizationInterval, 
			int visualizationPause)
	{
		this.printSliceGeneration = printSliceGeneration;
		this.sliceGeneration = sliceGeneration;
		this.printGenerations = printGenerations;
		this.printGenerationsEachX = printGenerationsEachX;
		this.enableContinuousVisualization = enableContinuousVisualization;
		this.interactiveMode= interactiveMode;
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
