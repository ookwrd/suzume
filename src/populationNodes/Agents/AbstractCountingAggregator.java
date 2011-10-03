package populationNodes.Agents;

import java.util.ArrayList;

import tools.Pair;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;
import PopulationModel.Node.StatisticsCollectionPoint;

public abstract class AbstractCountingAggregator implements StatisticsAggregator {

		private ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
		private double count = 0;
		private int agentCount = 0;
		
		private String name;
		private StatisticsCollectionPoint point;
		
		public AbstractCountingAggregator(StatisticsCollectionPoint point, String name){
			this.name = name;
			this.point = point;
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

		@Override
		public final ArrayList<Pair<Double, Double>> getStatistics() {
			return stats;
		}
		
		@Override
		public String getTitle(){
			return name;
		}
		
		protected final double getCount(){
			return count;
		}
}
