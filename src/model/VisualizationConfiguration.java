package model;

import java.util.StringTokenizer;

public class VisualizationConfiguration {

	public static final boolean DEFAULT_PRINT_SLICE_GENERATION = false;
	public static final int DEFAULT_SLICE_GENERATION = 500;
	
	public static final boolean DEFAULT_PRINT_GENERATIONS = true;
	public static final int DEFAULT_PRINT_GENERATIONS_EACH_X = 1000;
	
	public boolean printSliceGeneration;
	public int sliceGeneration;
	
	public boolean printGenerations;
	public int printGenerationsEachX;
	
	public VisualizationConfiguration(){
		this.printSliceGeneration = DEFAULT_PRINT_SLICE_GENERATION;
		this.sliceGeneration = DEFAULT_SLICE_GENERATION;
		this.printGenerations = DEFAULT_PRINT_GENERATIONS;
		this.printGenerationsEachX = DEFAULT_PRINT_GENERATIONS_EACH_X;
	}
	
	public VisualizationConfiguration(boolean printSliceGeneration, int sliceGeneration, boolean printGenerations, int printGenerationsEachX){
		this.printSliceGeneration = printSliceGeneration;
		this.sliceGeneration = sliceGeneration;
		this.printGenerations = printGenerations;
		this.printGenerationsEachX = printGenerationsEachX;
	}
	
	public VisualizationConfiguration(StringTokenizer tokenizer){
		//TODO
	}
	
	public String saveString(){
		return null; //TODO
	}
}
