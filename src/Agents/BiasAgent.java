package Agents;
import java.util.ArrayList;

import model.RandomGenerator;


public class BiasAgent extends AbstractAgent implements Agent{
	
	private static final int DIMENSIONS = 2;
	
	private static final double MUTATION_RATE = 0.00025;
	private static final double INVENTION_PROBABILITY = 0.1;
	
	public ArrayList<double[]> chromosome;
	
	public int dimensions;
	
	private RandomGenerator randomGenerator;
	
	public BiasAgent(int id, RandomGenerator randomGenerator) {
		super(id);
		
		this.randomGenerator = randomGenerator;
		
		chromosome = new ArrayList<double[]>();
		for(int i = 0; i < NUMBER_OF_MEANINGS; i++){
			
			double[] biases = new double[DIMENSIONS];
			
			double biasSoFar = 0;
			//Random Biases
			for(int j = 0; j < DIMENSIONS; j++){
				biases[j] = randomGenerator.random();
				biasSoFar += biases[j];
			}
			//Norminalization to add to 1
			for(int j = 0; j < DIMENSIONS; j++){
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
	public BiasAgent(BiasAgent parent1, BiasAgent parent2, int id, RandomGenerator randomGenerator){
		super(id);
		chromosome = new ArrayList<double[]>(NUMBER_OF_MEANINGS);
		
		this.randomGenerator = randomGenerator;
		
		//Crossover
		int crossoverPoint = randomGenerator.randomInt(NUMBER_OF_MEANINGS);
		int i = 0;
		while(i < crossoverPoint){
			chromosome.add(parent1.chromosome.get(i));
			i++;
		}
		while(i < NUMBER_OF_MEANINGS){
			chromosome.add(parent2.chromosome.get(i));
			i++;
		}
		
		//Mutation
		for(int j = 0; j < NUMBER_OF_MEANINGS; j++){//TODO different mutation stratergies... fixed values. 80% or something
			if(randomGenerator.random() < MUTATION_RATE){
				
				double[] gene = chromosome.get(j);
				
				double updateAmount = (randomGenerator.random()-0.5)/DIMENSIONS;
				
				int update1 = randomGenerator.randomInt(DIMENSIONS);
				int update2 = randomGenerator.randomInt(DIMENSIONS);
				
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
	public Utterance getRandomUtterance() {
		int index = randomGenerator.randomInt(chromosome.size());
		Integer value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	@Override
	public void invent() {
		
		if(grammar.contains(Utterance.SIGNAL_NULL_VALUE)){//Single iteration... max 1 invention per turn.
			
			if(randomGenerator.random() < INVENTION_PROBABILITY){
				
				//Collect indexes of all null elements
				ArrayList<Integer> nullIndexes = new ArrayList<Integer>();
				for(int i = 0; i < grammar.size(); i++){
					
					Integer allele = grammar.get(i);
					if(allele == Utterance.SIGNAL_NULL_VALUE){
						nullIndexes.add(i);
					}
				}
				
				//Choose a random null element to invent a new value for
				Integer index = nullIndexes.get(randomGenerator.randomInt(nullIndexes.size()));
				
				int dimensionIndex = -1;
				double seenSoFar = 0;
				double threshold = randomGenerator.random();
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
		
		double random = randomGenerator.random();
		if(chromosome.get(u.meaning)[u.signal] <= random){
			grammar.set(u.meaning, u.signal);
		}
		
	}
	
	public void printAgent(){
		System.out.println("Agent " + getId() + ":");
		for(int j = 0; j < DIMENSIONS; j++){
			System.out.print("Dimension " + j + ":\t");
			for (int i = 0; i < NUMBER_OF_MEANINGS; i++) {
				System.out.print(chromosome.get(i)[j] + "\t");
			}
			System.out.println();
		}
		System.out.print("Grammar:\t");
		for(int i = 0; i < NUMBER_OF_MEANINGS; i++){
			System.out.print(grammar.get(i) + "\t\t\t");
		}
		
		System.out.println();
	}

	
	public static void main(String[] args){
	
		BiasAgent newAgent = new BiasAgent(11342134, RandomGenerator.getGenerator());
		for(int i = 0; i < 1000; i++){
			newAgent.invent();
		}
		
		BiasAgent newAgent1 = new BiasAgent(1132, RandomGenerator.getGenerator());
		for(int i = 0; i < 50; i++){
			newAgent1.learnUtterance(newAgent.getRandomUtterance());
		}
		
		newAgent.printAgent();
		newAgent1.printAgent();
		
	}
	
	/**
	 * Sum of the probabilites of the grammar having the values that they do. 
	 */
	@Override
	public double geneGrammarMatch() {

		double count = 0;
		
		for(int i = 0; i < NUMBER_OF_MEANINGS; i++){
			if(grammar.get(i) != Utterance.SIGNAL_NULL_VALUE){
				count += chromosome.get(i)[grammar.get(i)];	
			}
		}
		
		return count;
		
	}

	@Override
	public int learningIntensity() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public ArrayList getChromosome() {
		return chromosome;
	}
}