package nodes.Agents;

import java.util.ArrayList;

import autoconfiguration.ConfigurationParameter;
import nodes.Node;
import nodes.NodeConfiguration;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.BaseStatisticsAggregator;
import simulation.RandomGenerator;
import tools.Pair;

public class SynonymAgent extends AbstractAgent {
	
	private enum MeaningDistribution {UNIFORM, SQUARED}
	private enum FitnessAdjustment {CAPACITY_COST, NO_ADJUSTMENT, COVERAGE}
	
	public final String INIT_LEXICAL_CAPACITY = "Initial lexical capacity:";
	public final String MEANING_SPACE_SIZE = "Meaning space size";
	public final String MEANING_DISTRIBUTION = "Meaning distribution";
	public final String FITNESS_ADJUSTMENT = "Fitness adjustment stratergy:";
	public final String LEXICON_CAPACITY_COST = "Cost of lexical capacity:";
	
	private ArrayList<Integer>[] lexicon;
	
	private int lexiconCapacity;
	private int lexiconSize;
	
	private int utterancesSeen = 0;
	
	public SynonymAgent(){
		setDefaultParameter(INIT_LEXICAL_CAPACITY, new ConfigurationParameter(10));
		setDefaultParameter(MEANING_SPACE_SIZE, new ConfigurationParameter(100));
		setDefaultParameter(MEANING_DISTRIBUTION, new ConfigurationParameter(MeaningDistribution.values(),true));
		setDefaultParameter(FITNESS_ADJUSTMENT, new ConfigurationParameter(FitnessAdjustment.values(),true));
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
		int meaning = getRandomMeaning();
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
		
		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication, "Semantic Coverage:") {
			@Override
			protected double getValue(Node agent) {
				int count = 0;
				for(ArrayList<Integer> meaningArrayList : ((SynonymAgent)agent).lexicon){
					if(meaningArrayList.size() > 0){
						count++;
					}
				}
				return count;
			}
		});
		
		retVal.add(new BaseStatisticsAggregator(null, "Final Lexical Distribution:") {
			@Override
			public void endRun(Integer run, ArrayList<Agent> agents) {
				for(int i = 0; i < ((SynonymAgent)agents.get(0)).lexicon.length; i++){
					double count = 0;
					for(Agent agent : agents){
						SynonymAgent synonymAgent = (SynonymAgent)agent;
						count += synonymAgent.lexicon[i].size();
					}
					stats.add(new Pair<Double, Double>(new Double(i), count/agents.size()));
				}
			}
		});
		
		return retVal;
	}
	
	@Override
	public void teach(Node learner) {
		learner.learnUtterance(getRandomUtterance());
	}
	
	private Utterance getRandomUtterance(){
		int meaning = getRandomMeaning();
		int tokensForMeaning = lexicon[meaning].size();
		int token;
		if(tokensForMeaning == 0){
			token = Utterance.SIGNAL_NULL_VALUE;
		} else{
			token = randomGenerator.randomInt(tokensForMeaning);
		}
		return new Utterance(meaning, token);
	}
	
	private int getRandomMeaning(){
		
		switch ((MeaningDistribution)getListParameter(MEANING_DISTRIBUTION)[0]) {
		case SQUARED:
			return (int)(randomGenerator.random()*randomGenerator.random()*lexicon.length);

		case UNIFORM:
			return randomGenerator.randomInt(lexicon.length);
			
		default:
			System.err.println("Shouldn't be here");
			return -1;
		}
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
		
		switch ((FitnessAdjustment)getListParameter(FITNESS_ADJUSTMENT)[0]) {
		case CAPACITY_COST:
			setFitness(getFitness()-lexiconCapacity*getDoubleParameter(LEXICON_CAPACITY_COST));
			break;
			
		case COVERAGE:
			int count = 0;
			for(ArrayList<Integer> meaning : lexicon){
				if(meaning.size() > 0){
					count++;
				}
			}
			setFitness(getFitness()+count);
			break;
			
		default:
			System.err.println("Unknown FitnessAdjustment type in " + SynonymAgent.class.getName());
			break;
			
		case NO_ADJUSTMENT:
		}
	}
}
