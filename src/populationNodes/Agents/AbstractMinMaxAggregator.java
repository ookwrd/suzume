package populationNodes.Agents;

import java.util.ArrayList;

import tools.Pair;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;
import PopulationModel.Node.StatisticsCollectionPoint;

public abstract class AbstractMinMaxAggregator implements StatisticsAggregator {

	public enum Type {Min, Max}
	
	private ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
	
	private double value = 0;
	private boolean unset = true;
	
	private Type type;
	
	private String name;
	private StatisticsCollectionPoint point;
	
	public AbstractMinMaxAggregator(Type type,StatisticsCollectionPoint point, String name){
		this.type = type;
		this.name = name;
		this.point = point;
	}
	
	@Override
	public void collectStatistics(StatisticsCollectionPoint point, Node agent) {
		if(this.point != point){
			return;
		}
		
		double tempValue = statValue(agent);
		
		if(unset){
			value = tempValue;
			unset = false;
		}else{
			if(type == Type.Min){
				if(tempValue < value){
					value = tempValue;
				}
			}else{
				if(tempValue > value){
					value = tempValue;
				}
			}
			
		}
		
	}

	protected abstract double statValue(Node agent);
	
	@Override
	public void endGeneration(Integer generation) {
		stats.add(new Pair<Double,Double>(generation.doubleValue(),value));
		unset = true;
	}

	@Override
	public ArrayList<Pair<Double, Double>> getStatistics() {
		return stats;
	}

	@Override
	public String getTitle() {
		return name;
	}

}
