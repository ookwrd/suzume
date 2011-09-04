package populationNodes.Agents;

import java.util.ArrayList;

import PopulationModel.Node;

public interface Agent extends Node {
	
	//Fitness Calculation
	public void setFitness(double fitness);
	public double getFitness();
	public void adjustFinalFitnessValue();
	
	//Statistics
	public Double geneGrammarMatch();
	public Double numberOfNulls();
	public Double learningIntensity(); //TODO how do i make this more general??
	public Object getGenotype();//TODO get rid of this
	public Object getPhenotype();//TODO get rid of this as well
	
	public ArrayList<StatisticsType> getSupportedStatisticsTypes();
	public Object getStatisticsValue(StatisticsType type);
	
	public class StatisticsType{
		public enum ValueType {DOUBLE, UNIQUENESS}
		
		public final String ID;
		public final String description;
		public final ValueType type;
		
		public StatisticsType(String ID, String description, ValueType type){
			this.ID = ID;
			this.description = description;
			this.type = type;
		} 
	}
	
}
