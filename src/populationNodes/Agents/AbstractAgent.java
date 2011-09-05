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
	public static final String BASE_FITNESS = "Base fitness value:";
	
	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";

	{
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
		setDefaultParameter(BASE_FITNESS, new ConfigurationParameter(1));
	}
	
	private double fitness;
	
	protected ArrayList<Integer> grammar;
	
	public AbstractAgent(){		
		super();
		}
	
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initializeAgent(config, id, randomGenerator);

		setFitness(getParameter(BASE_FITNESS).getInteger());;
		
		grammar = new ArrayList<Integer>(config.getParameter(NUMBER_OF_MEANINGS).getInteger());
		for (int j = 0; j < config.getParameter(NUMBER_OF_MEANINGS).getInteger(); j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}

	public ArrayList<Integer> getGrammar() {
		return grammar;
	}
	
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
		
		retVal.add(new AbstractCountingAggregator("Gene Grammar Match") {
			@Override
			protected void updateCount(Node agent) {
				addToCount(((Agent)agent).geneGrammarMatch());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Learning Intensity") {
			@Override
			protected void updateCount(Node agent) {
				addToCount(((Agent)agent).learningIntensity());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Number of Nulls") {	
			@Override
			protected void updateCount(Node agent) {
				addToCount(((Agent)agent).numberOfNulls());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Fitness") {
			@Override
			public void updateCount(Node agent) {	
				addToCount(((Agent)agent).getFitness());
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>("Number of Genotypes") {
			@Override
			protected void checkUniqueness(Node agent) {
				addItem(((Agent)agent).getGenotype());
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>("Number of Phenotypes") {
			@Override
			protected void checkUniqueness(Node agent) {
				addItem(((Agent)agent).getPhenotype());
			}
		});

		return retVal;
	}
}
