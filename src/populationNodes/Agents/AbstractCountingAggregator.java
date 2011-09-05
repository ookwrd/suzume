package populationNodes.Agents;

import java.util.ArrayList;

import tools.Pair;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;

public abstract class AbstractCountingAggregator implements StatisticsAggregator {

		private ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
		private double count = 0;
		private int agentCount = 0;
		
		@Override
		public final void collectStatistics(Node agent) {
			agentCount++;
			updateCount(agent);
		}
		
		protected abstract void updateCount(Node agent);
		
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
		
		protected final void setCount(double count){
			this.count = count;
		}
		
		protected final void addToCount(double count){
			this.count += count;
		}
		
		protected final double getCount(){
			return count;
		}
}
