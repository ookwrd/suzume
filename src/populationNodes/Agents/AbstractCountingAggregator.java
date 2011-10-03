package populationNodes.Agents;

import tools.Pair;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsCollectionPoint;

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
			count += getValue(agent);
		}
		
		protected abstract double getValue(Node agent);
		
		@Override
		public void endGeneration(Integer generation){
			stats.add(new Pair<Double,Double>(generation.doubleValue(),count/agentCount));
			count = 0;
			agentCount = 0;
		}
}
