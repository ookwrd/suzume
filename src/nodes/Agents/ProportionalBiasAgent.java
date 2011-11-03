package nodes.Agents;
import java.util.ArrayList;

import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;

import autoconfiguration.Configurable;
import autoconfiguration.Configurable.Describable;

import simulation.RandomGenerator;

public class ProportionalBiasAgent extends AbstractGrammarAgent implements Describable{
	
	private enum StatisticsTypes {GENE_GRAMMAR_MATCH_PROB}
	
	protected static final String DIMENSIONS = "Dimensions";
	protected static final String MUTATION_RATE = "Mutation Rate";
	protected static final String INVENTION_PROB = "Invention Probability";
	
	public ArrayList<double[]> chromosome;
	
	public ProportionalBiasAgent(){
		setDefaultParameter(Node.STATISTICS_TYPE, StatisticsTypes.values(), StatisticsTypes.values());
		setDefaultParameter(DIMENSIONS, 2);
		setDefaultParameter(MUTATION_RATE, 0.01);
		setDefaultParameter(INVENTION_PROB, 0.1);
	}
	
	public ProportionalBiasAgent(Configurable config, RandomGenerator generator){
		super.initialize(config, randomGenerator);
		
		chromosome = new ArrayList<double[]>();
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			
			double[] biases = new double[getIntegerParameter(DIMENSIONS)];
			
			double biasSoFar = 0;
			//Random Biases
			for(int j = 0; j < getIntegerParameter(DIMENSIONS); j++){
				biases[j] = randomGenerator.nextDouble();
				biasSoFar += biases[j];
			}
			//Norminalization to add to 1
			for(int j = 0; j < getIntegerParameter(DIMENSIONS); j++){
				biases[j] = biases[j]/biasSoFar;
			}
			
			chromosome.add(biases);
		}
	}
	
	@Override
	public void initializeAgent(Node parentA, Node parentB){
		ProportionalBiasAgent parent1 = (ProportionalBiasAgent)parentA;
		ProportionalBiasAgent parent2 = (ProportionalBiasAgent)parentB;
		
		chromosome = new ArrayList<double[]>(getIntegerParameter(SEMANTIC_SPACE_SIZE));
		
		//Crossover
		int crossoverPoint = randomGenerator.nextInt(getIntegerParameter(SEMANTIC_SPACE_SIZE));
		int i = 0;
		while(i < crossoverPoint){
			chromosome.add(parent1.chromosome.get(i));
			i++;
		}
		while(i < getIntegerParameter(SEMANTIC_SPACE_SIZE)){
			chromosome.add(parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < getIntegerParameter(SEMANTIC_SPACE_SIZE); j++){//TODO different mutation stratergies... fixed values. 80% or something
			if(randomGenerator.nextDouble() < getDoubleParameter(MUTATION_RATE)){
				double[] gene = chromosome.get(j).clone();
				
			/*	double updateAmount = (randomGenerator.random()-0.5)/DIMENSIONS;
				
				int update1 = randomGenerator.randomInt(DIMENSIONS);
				int update2 = randomGenerator.randomInt(DIMENSIONS);
				
				if(gene[update1]+updateAmount <= 1   //TODO update as much as possible, this makes it less likely to approach edges
						&& gene[update1]+updateAmount >= 0
						&& gene[update2]-updateAmount <= 1
						&& gene[update2]-updateAmount >= 0){
					gene[update1] = gene[update1] + updateAmount;
					gene[update2] = gene[update2] - updateAmount;
				}*/
				
				double updateAmount = 0.1;
				
				int update1 = randomGenerator.nextInt(getIntegerParameter(DIMENSIONS));
				int update2 = randomGenerator.nextInt(getIntegerParameter(DIMENSIONS));
				while(update2 == update1){
					update2 = randomGenerator.nextInt(getIntegerParameter(DIMENSIONS));
				}
				
				
				if(gene[update1]+updateAmount <= 1   //TODO update as much as possible, this makes it less likely to approach edges
						&& gene[update1]+updateAmount >= 0
						&& gene[update2]-updateAmount <= 1
						&& gene[update2]-updateAmount >= 0){
					gene[update1] = gene[update1] + updateAmount;
					gene[update2] = gene[update2] - updateAmount;
				}
				
				chromosome.set(j, gene);
			}
		}
	}
	
	@Override
	public void invent() {
		
		if(grammar.contains(Utterance.SIGNAL_NULL_VALUE)){
			//Single iteration... max 1 invention per turn.
			if(randomGenerator.nextDouble() < getDoubleParameter(INVENTION_PROB)){
				
				//Collect indexes of all null elements
				ArrayList<Integer> nullIndexes = new ArrayList<Integer>();
				for(int i = 0; i < grammar.size(); i++){
					Integer allele = grammar.get(i);
					if(allele == Utterance.SIGNAL_NULL_VALUE){
						nullIndexes.add(i);
					}
				}
				
				//Choose a random null element to invent a new value for
				Integer index = nullIndexes.get(randomGenerator.nextInt(nullIndexes.size()));
				
				int dimensionIndex = -1;
				double seenSoFar = 0;
				double threshold = randomGenerator.nextDouble();
				do{
					dimensionIndex++;
					seenSoFar += chromosome.get(index)[dimensionIndex];
				}
				while(seenSoFar < threshold);

				grammar.set(index, dimensionIndex);
				
			}
		}
	}
	
	@Override
	public void learnUtterance(Utterance u) {
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(chromosome.get(u.meaning)[u.signal] <= randomGenerator.nextDouble()){
			grammar.set(u.meaning, u.signal);
		}
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
		
		switch((StatisticsTypes)statisticsKey){
		
		case GENE_GRAMMAR_MATCH_PROB:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness, "Gene-Grammar Match Probability") {
				@Override
				protected double getValue(Node agent) {
					return ((ProportionalBiasAgent)agent).geneGrammarMatch();
				}
			};
			
		default:
			System.err.println(ProportionalBiasAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	/**
	 * Sum of the probabilites of the grammar having the values that they do. 
	 */
	public Double geneGrammarMatch() {

		double count = 0;
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			if(grammar.get(i) != Utterance.SIGNAL_NULL_VALUE){
				count += chromosome.get(i)[grammar.get(i)];	
			}
		}

		return count;		
	}

	@Override
	public String getDescription() {
		return "Experiment agent whose UG biased towards particular values is not binary. Rather the agent is proportionally" +
				" towards a number of values, with the probability of learning each based on the degree of the bias towards " +
				"that particuar value. This agent is still being developed and its behaviour isn't guranteed to be correct.";
	}
}