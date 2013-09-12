package nodes.Agents.statisticaggregators;

import java.util.ArrayList;

import nodes.Node;
import nodes.Agents.Agent;
import nodes.Node.StatisticsCollectionPoint;
import tools.Pair;

public abstract class AbstractCountingAggregator extends BaseStatisticsAggregator {

		private double count = 0;
		private int agentCount = 0;
		
		public AbstractCountingAggregator(StatisticsCollectionPoint point, String name){
			super(point, name);
		}
		
		@Override
		public final void collectStatistics(StatisticsCollectionPoint point, Node agent) {
			if(this.point != point){
				return;
			}
			
			agentCount++;
			double value = getValue(agent);
			if(value != Double.NaN){
				count += value;
			}
		}
		
		protected abstract double getValue(Node agent);
		
		@Override
		public void endGeneration(Integer generation, ArrayList<Agent> agents){
			stats.add(new Pair<Double,Double>(generation.doubleValue(),count/agentCount));
			count = 0;
			agentCount = 0;
		}
}
