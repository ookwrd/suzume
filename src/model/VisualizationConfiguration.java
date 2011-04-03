package model;

import java.util.StringTokenizer;

public class VisualizationConfiguration {

	public static final boolean DEFAULT_PRINT_SLICE_GENERATION = false;
	public static final int DEFAULT_SLICE_GENERATION = 500;
	
	public boolean printSliceGeneration;
	public int sliceGeneration;
	
	public VisualizationConfiguration(){
		this.printSliceGeneration = DEFAULT_PRINT_SLICE_GENERATION;
		this.sliceGeneration = DEFAULT_SLICE_GENERATION;
	}
	
	public VisualizationConfiguration(boolean printSliceGeneration, int sliceGeneration){
		this.printSliceGeneration = printSliceGeneration;
		this.sliceGeneration = sliceGeneration;
	}
	
	public VisualizationConfiguration(StringTokenizer tokenizer){
		//TODO
	}
	
	public String saveString(){
		return null; //TODO
	}
}
