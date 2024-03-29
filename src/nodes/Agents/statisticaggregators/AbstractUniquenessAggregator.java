package nodes.Agents.statisticaggregators;

import java.util.ArrayList;

import nodes.Node;
import nodes.Agents.Agent;
import nodes.Node.StatisticsCollectionPoint;

import tools.Pair;

public abstract class AbstractUniquenessAggregator<K> extends BaseStatisticsAggregator {

		private ArrayList<K> collection = new ArrayList<K>();  
		private int agentCount = 0;
		
		public AbstractUniquenessAggregator(StatisticsCollectionPoint point, String name){
			super(point, name);
		}
		
		@Override
		public final void collectStatistics(StatisticsCollectionPoint point, Node agent) {
			if(point != this.point){
				return;
			}
			
			agentCount++;
			addItem(getItem(agent));
		}
		
		private void addItem(K item){
			if(!collection.contains(item)){
				collection.add(item);
			}
		}
		
		protected abstract K getItem(Node agent);
		
		@Override
		public void endGeneration(Integer generation, ArrayList<Agent> agents){
			stats.add(new Pair<Double,Double>(generation.doubleValue(),(double)collection.size()));
			agentCount = 0;
			collection = new ArrayList<K>();
		}
}