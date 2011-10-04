package populationNodes.Agents;

import java.util.ArrayList;

import auto_configuration.ConfigurationParameter;

import populationNodes.NodeConfiguration;
import populationNodes.Utterance;
import simulation.RandomGenerator;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsCollectionPoint;

public abstract class AbstractGrammarAgent extends AbstractAgent {

	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";
	
	protected ArrayList<Integer> grammar;
	
	public AbstractGrammarAgent(){
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
	}

	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initialize(config, id, randomGenerator);

		grammar = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		for (int j = 0; j < getIntegerParameter(NUMBER_OF_MEANINGS); j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}
		
	@Override
	public void teach(Node learner) {
		learner.learnUtterance(getRandomUtterance());
	}

	@Override
	public void communicate(Node partner){
		Utterance utterance = ((AbstractGrammarAgent)partner).getRandomUtterance();

		//If agent and neighbour agree update fitnes.
		if(!utterance.isNull() && (grammar.get(utterance.meaning) == utterance.signal)){
			setFitness(getFitness()+1);
		}
	}

	public Utterance getRandomUtterance() {
		int index = randomGenerator.randomInt(grammar.size());
		Integer value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = super.getStatisticsAggregators();
		
		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication, "Number of Nulls") {	
			@Override
			protected double getValue(Node agent) {
				return ((AbstractGrammarAgent)agent).numberOfNullsInGrammar();
			}
		});
		
		retVal.add(new AbstractUniquenessAggregator<Object>(StatisticsCollectionPoint.PostCommunication, "Number of Phenotypes") {
			@Override
			protected Object getItem(Node agent) {
				return ((AbstractGrammarAgent)agent).grammar;
			}
		});
		
		return retVal;	
	}
	
	public Double numberOfNullsInGrammar() {
		double count = 0;
		for(int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
				count++;
			}
		}
		return count;
	}
}
