package populationNodes.Agents;

import java.util.ArrayList;

import populationNodes.AbstractNode;
import populationNodes.NodeConfiguration;
import populationNodes.Utterance;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Node;

import simulation.RandomGenerator;
import tools.Pair;

public abstract class AbstractAgent extends AbstractNode implements Agent {
	
	public static final String FITNESS_STATISTICS = "Fitness";
	
	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";

	{
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
	}
	
	private double fitness;
	
	protected ArrayList<Integer> grammar;
	
	public AbstractAgent(){		
		super();
		}
	
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initializeAgent(config, id, randomGenerator);

		fitness = 0;
		
		grammar = new ArrayList<Integer>(config.getParameter(NUMBER_OF_MEANINGS).getInteger());
		for (int j = 0; j < config.getParameter(NUMBER_OF_MEANINGS).getInteger(); j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}

	public ArrayList<Integer> getGrammar() {
		return grammar;
	}
	
	@Override
	public Double numberOfNulls() {
		
		double count = 0;
		
		for(int i = 0; i < config.getParameter(NUMBER_OF_MEANINGS).getInteger(); i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
				count++;
			}
		}
		
		return count;
	}
	
	@Override
	public double getFitness() {
		return fitness;
	}
	
	@Override
	public void setFitness(double fitness){
		this.fitness = fitness;
	}
	
	@Override
	public boolean canStillLearn(){
		return true;
	}
	
	@Override
	public void invent(){
	}
	
	@Override
	public void teach(Node learner) {
		learner.learnUtterance(getRandomUtterance());
	}
	
	@Override
	public void communicate(Node partner){
		
		Utterance utterance = partner.getRandomUtterance();

		//If agent and neighbour agree update fitnes.
		if(!utterance.isNull() && (getGrammar().get(utterance.meaning) == utterance.signal)){
			setFitness(getFitness()+1);
		}
	}
	
	@Override
	public void adjustFinalFitnessValue(){
		//Do nothing.
	}
	
	@Override
	public ArrayList getPhenotype(){
		return grammar;
	}
	
	@Override
	public ArrayList<Agent> getBaseAgents(){
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		retAgents.add(this);
		return retAgents;
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = new ArrayList<StatisticsAggregator>();
		
		retVal.add(new FitnessAggregator());
		
		return retVal;
	}
	
	public class FitnessAggregator implements StatisticsAggregator {

		private ArrayList<Pair<Double, Double>> stats = new ArrayList<Pair<Double,Double>>();
		private double fitnessCount = 0;
		private int agentCount = 0;
		
		@Override
		public void collectStatistics(Node agent) {
			
			fitnessCount += ((Agent)agent).getFitness();
			agentCount++;
		}
		
		@Override
		public void endGeneration(Integer generation){
			stats.add(new Pair<Double,Double>(generation.doubleValue(),fitnessCount/agentCount));
			
			fitnessCount = 0;
			agentCount = 0;
		}

		@Override
		public ArrayList<Pair<Double, Double>> getStatistics() {
			return stats;
		}
		
	}
}
