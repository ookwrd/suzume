package nodes.Agents.statisticaggregators;

import java.util.ArrayList;

import nodes.Node;
import nodes.Agents.Agent;
import nodes.Node.StatisticsAggregator;
import nodes.Node.StatisticsCollectionPoint;

import tools.Pair;

public abstract class BaseStatisticsAggregator implements StatisticsAggregator {

	protected ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
	private String name;
	protected StatisticsCollectionPoint point;
	
	public BaseStatisticsAggregator(StatisticsCollectionPoint point, String name){
		this.name = name;
		this.point = point;
	}	
	
	@Override
	public void collectStatistics(StatisticsCollectionPoint point, Node agent) {}
	
	@Override
	public void endGeneration(Integer generation, ArrayList<Agent> agents) {}

	@Override
	public void endRun(Integer run, ArrayList<Agent> agents) {}
	
	@Override
	public String getTitle() {
		return name;
	}
	
	@Override
	public ArrayList<Pair<Double, Double>> getStatistics() {
		return stats;
	}
}
