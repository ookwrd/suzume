package populationNodes.Agents;

import java.util.ArrayList;

import tools.Pair;
import PopulationModel.Node.StatisticsAggregator;
import PopulationModel.Node.StatisticsCollectionPoint;

public abstract class BaseStatisticsAggregator implements StatisticsAggregator {

	protected ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
	private String name;
	protected StatisticsCollectionPoint point;
	
	public BaseStatisticsAggregator(StatisticsCollectionPoint point, String name){
		this.name = name;
		this.point = point;
	}	
	
	@Override
	public void endGeneration(Integer generation) {}

	@Override
	public String getTitle() {
		return name;
	}
	
	@Override
	public ArrayList<Pair<Double, Double>> getStatistics() {
		return stats;
	}
}
