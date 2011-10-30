package nodes.Agents;
import java.util.ArrayList;

import nodes.Node;
import nodes.Utterance;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;
import autoconfiguration.Configurable.Describable;

import simulation.RandomGenerator;

public class BiasAgent extends AbstractGrammarAgent implements Describable{
	
	private enum StatisticsTypes {GENE_GRAMMAR_MATCH_PROB}
	
	private int dimensions;
	private double mutationRate;
	private double inventionProbability;
	
	public ArrayList<double[]> chromosome;
	
	private RandomGenerator randomGenerator;
	
	public BiasAgent(){
		setDefaultParameter(Node.STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(), StatisticsTypes.values()));
		setDefaultParameter("Dimensions", new ConfigurationParameter(2));
		setDefaultParameter("Mutation rate", new ConfigurationParameter(0.01));
		setDefaultParameter("Invention Probability", new ConfigurationParameter(0.1));
	}
	
	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator) {
		super.initialize(config, id, randomGenerator);
		initializeParameters(config);
		
		this.randomGenerator = randomGenerator;
		
		chromosome = new ArrayList<double[]>();
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			
			double[] biases = new double[dimensions];
			
			double biasSoFar = 0;
			//Random Biases
			for(int j = 0; j < dimensions; j++){
				biases[j] = randomGenerator.nextDouble();
				biasSoFar += biases[j];
			}
			//Norminalization to add to 1
			for(int j = 0; j < dimensions; j++){
				biases[j] = biases[j]/biasSoFar;
			}
			
			chromosome.add(biases);
		}
	}
	
	/**
	 * Sexual reproduction of a new agent.
	 * 
	 * @param parent1
	 * @param Parent2
	 * @param id
	 */
	@Override
	public void initializeAgent(Node parentA, Node parentB, int id, RandomGenerator randomGenerator){
		BiasAgent parent1 = (BiasAgent)parentA;
		BiasAgent parent2 = (BiasAgent)parentB;
		super.initialize(parent1, id, randomGenerator);
		initializeParameters((BiasAgent)parentA);
		
		chromosome = new ArrayList<double[]>(getIntegerParameter(SEMANTIC_SPACE_SIZE));
		
		this.randomGenerator = randomGenerator;
		
		//Crossover
		int crossoverPoint = randomGenerator.nextInt(getParameter(SEMANTIC_SPACE_SIZE).getInteger());
		int i = 0;
		while(i < crossoverPoint){
			chromosome.add(parent1.chromosome.get(i));
			i++;
		}
		while(i < getParameter(SEMANTIC_SPACE_SIZE).getInteger()){
			chromosome.add(parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < getParameter(SEMANTIC_SPACE_SIZE).getInteger(); j++){//TODO different mutation stratergies... fixed values. 80% or something
			if(randomGenerator.nextDouble() < mutationRate){
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
				
				int update1 = randomGenerator.nextInt(dimensions);
				int update2 = randomGenerator.nextInt(dimensions);
				while(update2 == update1){
					update2 = randomGenerator.nextInt(dimensions);
				}
				
				
				if(gene[update1]+updateAmount <= 1   //TODO update as much as possible, this makes it less likely to approach edges
						&& gene[update1]+updateAmount >= 0
						&& gene[update2]-updateAmount <= 1
						&& gene[update2]-updateAmount >= 0){
					//System.out.println("Before" + gene[update1]);
					gene[update1] = gene[update1] + updateAmount;
					gene[update2] = gene[update2] - updateAmount;
					//System.out.println("after" + gene[update1]);
				}
				
				chromosome.set(j, gene);
			}
		}
	}
	
	private void initializeParameters(Configurable config){
		dimensions = config.getParameter("Dimensions").getInteger();
		mutationRate = config.getParameter("Mutation rate").getDouble();
		inventionProbability= config.getParameter("Invention Probability").getDouble();
	}
	
	@Override
	public void invent() {
		
		if(grammar.contains(Utterance.SIGNAL_NULL_VALUE)){//Single iteration... max 1 invention per turn.
			
			if(randomGenerator.nextDouble() < inventionProbability){
				
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
		
		double random = randomGenerator.nextDouble();
		if(chromosome.get(u.meaning)[u.signal] <= random){
			grammar.set(u.meaning, u.signal);
		}
		
	}
	
	public void printAgent(){
		System.out.println("Agent " + getId() + ":");
		for(int j = 0; j < dimensions; j++){
			System.out.print("Dimension " + j + ":\t");
			for (int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++) {
				System.out.print(chromosome.get(i)[j] + "\t");
			}
			System.out.println();
		}
		System.out.print("Grammar:\t");
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			System.out.print(grammar.get(i) + "\t\t\t");
		}
		
		System.out.println();
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
					return ((BiasAgent)agent).geneGrammarMatch();
				}
			};
			
		default:
			System.err.println(BiasAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}
	
	/**
	 * Sum of the probabilites of the grammar having the values that they do. 
	 */
	public Double geneGrammarMatch() {

		double count = 0;
		
		double antiCount = 0;
		
		for(int i = 0; i < getIntegerParameter(SEMANTIC_SPACE_SIZE); i++){
			if(grammar.get(i) != Utterance.SIGNAL_NULL_VALUE){
				count += chromosome.get(i)[grammar.get(i)];	
				antiCount += chromosome.get(i)[grammar.get(i)==0?1:0];
			}
		}

		return count;		
	}

	@Override
	public String getDescription() {
		return "Unfinished agent that isn't biased in a binary fashion, but proportionally in a number of generations, with learning probability based on the degree";
	}
}