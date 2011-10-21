package nodes.Agents;

import java.util.ArrayList;

import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.AbstractUniquenessAggregator;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.ConfigurationParameter;

import simulation.RandomGenerator;

public abstract class AbstractGrammarAgent extends AbstractAgent {

	protected enum StatisticsTypes {NUMBER_NULLS, NUMBER_PHENOTYPES}
	
	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";
	
	protected ArrayList<Integer> grammar;
	
	public AbstractGrammarAgent(){
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),StatisticsTypes.values()));
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
	}

	@Override
	public void initialize(BasicConfigurable config, int id, RandomGenerator randomGenerator){
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
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
		
		switch((StatisticsTypes)statisticsKey){
		case NUMBER_NULLS:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Number of Nulls") {	
				@Override
				protected double getValue(Node agent) {
					return ((AbstractGrammarAgent)agent).numberOfNullsInGrammar();
				}
			};
			
		case NUMBER_PHENOTYPES:
			return new AbstractUniquenessAggregator<Object>(StatisticsCollectionPoint.PostFinalizeFitness, "Number of Phenotypes") {
				@Override
				protected Object getItem(Node agent) {
					return ((AbstractGrammarAgent)agent).grammar;
				}
			};
		
		default:
			System.err.println(AbstractGrammarAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
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
