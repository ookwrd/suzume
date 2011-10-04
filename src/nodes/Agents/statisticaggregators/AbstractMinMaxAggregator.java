package nodes.Agents.statisticaggregators;

import nodes.Node;
import nodes.Node.StatisticsCollectionPoint;
import tools.Pair;

public abstract class AbstractMinMaxAggregator extends BaseStatisticsAggregator {

	public enum Type {Min, Max}

	private double value = 0;
	private boolean unset = true;
	
	private Type type;
	
	public AbstractMinMaxAggregator(Type type, StatisticsCollectionPoint point, String name){
		super(point, name);
		this.type = type;
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

}
