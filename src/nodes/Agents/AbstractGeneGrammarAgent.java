package nodes.Agents;

import java.util.ArrayList;

import nodes.Node;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.AbstractUniquenessAggregator;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.ConfigurationParameter;

import simulation.RandomGenerator;

public abstract class AbstractGeneGrammarAgent extends AbstractGrammarAgent {

	protected enum StatisticsTypes {NUMBER_GENOTYPES, GENE_GRAMMAR_MATCH}
	
	protected static final String NUMBER_OF_TOKENS = "Token space size";
	
	protected ArrayList<Integer> chromosome;

	public AbstractGeneGrammarAgent(){
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),StatisticsTypes.values()));
		setDefaultParameter(NUMBER_OF_TOKENS, new ConfigurationParameter(2));
	}
	
	@Override
	public void initialize(BasicConfigurable config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		
		chromosome = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		
		for (int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomInt(getIntegerParameter(NUMBER_OF_TOKENS)));
		}
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
	
		switch ((StatisticsTypes)statisticsKey) {
		case NUMBER_GENOTYPES:
			return new AbstractUniquenessAggregator<Object>(StatisticsCollectionPoint.PostFinalizeFitness,"Number of Genotypes") {
				@Override
				protected Object getItem(Node agent) {
					return ((AbstractGeneGrammarAgent)agent).chromosome;
				}
			};
			
		case GENE_GRAMMAR_MATCH:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness,"Gene Grammar Match") {
				@Override
				protected double getValue(Node agent) {
					return ((AbstractGeneGrammarAgent)agent).geneGrammarMatch();
				}
			};

		default:
			System.err.println(AbstractGeneGrammarAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	public int geneGrammarMatch(){
		int count = 0;
		for(int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++){
			if(chromosome.get(i).equals(grammar.get(i))){
				count++;
			}
		}
		return count;
	}
	
	//TODO drawing!
	
}
