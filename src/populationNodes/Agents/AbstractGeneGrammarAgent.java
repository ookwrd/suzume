package populationNodes.Agents;

import java.util.ArrayList;

import populationNodes.NodeConfiguration;
import simulation.RandomGenerator;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsCollectionPoint;

public abstract class AbstractGeneGrammarAgent extends AbstractGrammarAgent {

	protected static final String NUMBER_OF_TOKENS = "Token space size";
	
	protected ArrayList<Integer> chromosome;

	public AbstractGeneGrammarAgent(){
		setDefaultParameter(NUMBER_OF_TOKENS, new ConfigurationParameter(2));
	}
	
	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		
		chromosome = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		
		for (int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomInt(getIntegerParameter(NUMBER_OF_TOKENS)));
		}
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = super.getStatisticsAggregators();
	
		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication,"Gene Grammar Match") {
			@Override
			protected double getValue(Node agent) {
				return ((AbstractGeneGrammarAgent)agent).geneGrammarMatch();
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>(StatisticsCollectionPoint.PostCommunication,"Number of Genotypes") {
			@Override
			protected Object getItem(Node agent) {
				return ((AbstractGeneGrammarAgent)agent).chromosome;
			}
		});
		
		return retVal;
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
