package model;
import java.util.ArrayList;

import model.World.allele;



public class Agent {
	
	private static final int DEFAULT_LEARNING_RESOURCE = 24;
	private static final int DEFAULT_FITNESS = 1;
	
	public ArrayList<allele> chromosome;
	public int learningResource;
	public int fitness;
	//public ILanguage;
	
	public Agent() {
		chromosome = new ArrayList<allele>();
		learningResource = DEFAULT_LEARNING_RESOURCE;
		fitness = DEFAULT_FITNESS;
	}
	
	
}
