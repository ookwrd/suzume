import java.util.ArrayList;


public class Agent {
	
	public enum allele { ZERO, ONE, NULL; }
	public ArrayList<allele> chromosome;
	public int learningResource;
	public int fitness;
	//public ILanguage;
	
	public Agent() {
		chromosome = new ArrayList<Agent.allele>();
		learningResource = 24;
		fitness = 1;
	}
	
	
}
