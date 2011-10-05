package nodes.Agents;

import java.util.ArrayList;

import autoconfiguration.ConfigurationParameter;
import nodes.Node;
import nodes.NodeConfiguration;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import simulation.RandomGenerator;

public class SynonymAgent extends AbstractAgent {
	
	public final String INIT_LEXICAL_CAPACITY = "Initial lexical capacity:";
	public final String MEANING_SPACE_SIZE = "Meaning space size";
	public final String LEXICON_CAPACITY_COST = "Cost of lexical capacity:";
	
	private ArrayList<Integer>[] lexicon;
	
	private int lexiconCapacity;
	private int lexiconSize;
	
	private int utterancesSeen = 0;
	
	public SynonymAgent(){
		setDefaultParameter(INIT_LEXICAL_CAPACITY, new ConfigurationParameter(10));
		setDefaultParameter(MEANING_SPACE_SIZE, new ConfigurationParameter(100));
		setDefaultParameter(LEXICON_CAPACITY_COST, new ConfigurationParameter(0.1));
	}

	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		this.lexiconCapacity = getIntegerParameter(INIT_LEXICAL_CAPACITY);
		this.lexiconSize = 0;
		
		initializeLexicon();
	}
	
	@SuppressWarnings("unchecked")
	private void initializeLexicon(){
		lexicon = new ArrayList[getIntegerParameter(MEANING_SPACE_SIZE)];
		for(int i = 0; i < lexicon.length; i++){
			lexicon[i] = new ArrayList<Integer>();
		}
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB, int id,
			RandomGenerator randomGenerator) {
		SynonymAgent agent1 = (SynonymAgent)parentA;
		SynonymAgent agent2 = (SynonymAgent)parentB;
		
		super.initialize(agent1, id, randomGenerator);

		int size;
		//Heredity
		if(randomGenerator.randomBoolean()){
			size = agent1.lexiconCapacity;
		}else{
			size = agent2.lexiconCapacity;
		}
		
		//Mutation
		size += randomGenerator.randomInt(3) - 1;
		if(size < 1){
			size = 1;
		}
		
		lexiconCapacity = size;
		
		initializeLexicon();
	}
	
	@Override
	public String getName(){
		return "Synonym Agent";
	}

	@Override
	public void learnUtterance(Utterance utterance) {
		if(utterance.signal != Utterance.SIGNAL_NULL_VALUE && !lexicon[utterance.meaning].contains(utterance.signal)){
			lexicon[utterance.meaning].add(utterance.signal);
			lexiconSize++;
		}
		utterancesSeen++;
	}

	@Override
	public void invent(){
		int meaning = randomGenerator.randomInt(lexicon.length);
		int value = randomGenerator.randomInt(10000);
		learnUtterance(new Utterance(meaning, value));
	}
	
	@Override
	public boolean canStillLearn() {
		return lexiconSize < lexiconCapacity && utterancesSeen < lexiconCapacity*5;
	}

	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = super.getStatisticsAggregators();
	
		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication, "Lexicon Size:") {
			@Override
			protected double getValue(Node agent) {
				return ((SynonymAgent)agent).lexiconSize;
			}
		});

		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication, "Lexicon Capacity:") {
			@Override
			protected double getValue(Node agent) {
				return ((SynonymAgent)agent).lexiconCapacity;
			}
		});
		return retVal;
	}
	
	@Override
	public void teach(Node learner) {
		learner.learnUtterance(getRandomUtterance());
	}
	
	private Utterance getRandomUtterance(){
		int meaning = randomGenerator.randomInt(lexicon.length);
		int tokensForMeaning = lexicon[meaning].size();
		int token;
		if(tokensForMeaning == 0){
			token = Utterance.SIGNAL_NULL_VALUE;
		} else{
			token = randomGenerator.randomInt(tokensForMeaning);
		}
		return new Utterance(meaning, token);
	}

	@Override
	public void communicate(Node partner) {
		SynonymAgent opposite = (SynonymAgent)partner;
		
		if(opposite.canYouUnderstand(getRandomUtterance())){
			setFitness(getFitness()+1);
		}
		
	}

	private boolean canYouUnderstand(Utterance utterance){
		boolean retVal = lexicon[utterance.meaning].contains(utterance.signal);
		if(retVal){
			setFitness(getFitness()+1);
		}
		return retVal;
	}
	
	@Override
	public void finalizeFitnessValue() {
		setFitness(getFitness()-lexiconCapacity*getDoubleParameter(LEXICON_CAPACITY_COST));
	}
}
