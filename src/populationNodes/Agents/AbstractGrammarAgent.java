package populationNodes.Agents;

import java.util.ArrayList;

import populationNodes.NodeConfiguration;
import populationNodes.Utterance;
import simulation.RandomGenerator;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;

public abstract class AbstractGrammarAgent extends AbstractAgent {

	protected static final String NUMBER_OF_MEANINGS = "Meaning space size";
	
	protected ArrayList<Integer> grammar;
	
	public AbstractGrammarAgent(){
		setDefaultParameter(NUMBER_OF_MEANINGS, new ConfigurationParameter(12));
	}

	@Override
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initializeAgent(config, id, randomGenerator);

		grammar = new ArrayList<Integer>(getIntegerParameter(NUMBER_OF_MEANINGS));
		for (int j = 0; j < getIntegerParameter(NUMBER_OF_MEANINGS); j++){
			grammar.add(Utterance.SIGNAL_NULL_VALUE);
		}
	}
	
	public ArrayList<Integer> getGrammar() {
		return grammar;
	}
	
	public Double numberOfNulls() {
		double count = 0;
		for(int i = 0; i < getIntegerParameter(NUMBER_OF_MEANINGS); i++){
			if(grammar.get(i).equals(Utterance.SIGNAL_NULL_VALUE)){
				count++;
			}
		}
		return count;
	}

	@Override
	public void communicate(Node partner){
		
		Utterance utterance = partner.getRandomUtterance();

		//If agent and neighbour agree update fitnes.
		if(!utterance.isNull() && (getGrammar().get(utterance.meaning) == utterance.signal)){
			setFitness(getFitness()+1);
		}
	}
	
	public ArrayList getPhenotype(){
		return grammar;
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = super.getStatisticsAggregators();
		
		retVal.add(new AbstractCountingAggregator("Gene Grammar Match") {
			@Override
			protected void updateCount(Node agent) {
				addToCount(((AbstractGrammarAgent)agent).geneGrammarMatch());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Learning Intensity") {
			@Override
			protected void updateCount(Node agent) {
				addToCount(((AbstractGrammarAgent)agent).learningIntensity());
			}
		});
		
		retVal.add(new AbstractCountingAggregator("Number of Nulls") {	
			@Override
			protected void updateCount(Node agent) {
				addToCount(((AbstractGrammarAgent)agent).numberOfNulls());
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>("Number of Phenotypes") {
			@Override
			protected void checkUniqueness(Node agent) {
				addItem(((AbstractGrammarAgent)agent).getPhenotype());
			}
		});
		
		retVal.add(new AbstractUniguenessAggregator<Object>("Number of Genotypes") {
			@Override
			protected void checkUniqueness(Node agent) {
				addItem(((AbstractGrammarAgent)agent).getGenotype());
			}
		});
		
		return retVal;
		
	}
	
	//Statistics
	public abstract Double geneGrammarMatch();
	public abstract  Double learningIntensity(); //TODO how do i make this more general??
	public abstract Object getGenotype();//TODO get rid of this
	
}
