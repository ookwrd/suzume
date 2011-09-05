package populationNodes.Agents;

import java.util.ArrayList;

import tools.Pair;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;

public abstract class AbstractUniguenessAggregator<K> implements StatisticsAggregator {

		private ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
		private ArrayList<K> collection = new ArrayList<K>();  
		private int agentCount = 0;
		
		private String name;
		
		public AbstractUniguenessAggregator(String name){
			this.name = name;
		}
		
		@Override
		public final void collectStatistics(Node agent) {
			agentCount++;
			checkUniqueness(agent);
		}
		
		protected abstract void checkUniqueness(Node agent);
		
		@Override
		public void endGeneration(Integer generation){
			stats.add(new Pair<Double,Double>(generation.doubleValue(),(double)collection.size()));
			agentCount = 0;
			collection = new ArrayList<K>();
		}

		@Override
		public final ArrayList<Pair<Double, Double>> getStatistics() {
			return stats;
		}
		
		@Override
		public String getTitle(){
			return name;
		}
		
		protected final void addItem(K item){
			if(!collection.contains(item)){
				collection.add(item);
			}
		}
	
}