package Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import runTimeVisualization.Visualizable.VisualizationType;
import simulation.RandomGenerator;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.PopulationNode;

public class ProbabalityAgent extends AbstractAgent implements Agent {

	protected static String[] visualizationTypes = {"numberNulls"};
	
	@SuppressWarnings("serial")
	private static HashMap<String, ConfigurationParameter> defaultParameters = new HashMap<String, ConfigurationParameter>(){{
		put("Learning probability match", new ConfigurationParameter(0.7));
		put("Learning probability mismatch", new ConfigurationParameter(0.5));
		put("Number of syntactic tokens", new ConfigurationParameter(2));
		put("Mutation Rate", new ConfigurationParameter(0.00025));
		put("Invention Probability", new ConfigurationParameter(0.01));
		put("Invention Chances", new ConfigurationParameter(5));
		put("Visualization Type", new ConfigurationParameter(visualizationTypes));
		//put("Meaning space size", new ConfigurationParameter(12));
	}};
	
	protected ArrayList<Integer> chromosome;
	
	@Override//TODO add abstract agent parameters
	public HashMap<String, ConfigurationParameter> getDefaultParameters(){
		return defaultParameters;
	}
	
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator) {
		super.initializeAgent(config, id, randomGenerator);

		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		for (int i = 0; i < NUMBER_OF_MEANINGS; i++) { // all alleles are initially set to a random value initially
			chromosome.add(randomGenerator.randomInt(config.get("Number of syntactic tokens").getInteger()));
		}
	}
	
	@Override
	public void initializeAgent(PopulationNode parentA, PopulationNode parentB,
			int id, RandomGenerator randomGenerator) {
		super.initializeAgent(parentA.getConfiguration(), id, randomGenerator);
		
		ProbabalityAgent parent1 = (ProbabalityAgent)parentA;
		ProbabalityAgent parent2 = (ProbabalityAgent)parentB;
		
		chromosome = new ArrayList<Integer>(NUMBER_OF_MEANINGS);
		
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
		for(int j = 0; j < NUMBER_OF_MEANINGS; j++){
			if(randomGenerator.random() < config.get("Mutation Rate").getDouble()){
				chromosome.set(j, randomGenerator.randomInt(config.get("Number of syntactic tokens").getInteger()));
			}
		}
	}
	
	@Override
	public String getName(){
		return "Probability Agent";
	}
	
	@Override
	public Utterance getRandomUtterance() {
		int index = randomGenerator.randomInt(chromosome.size());
		Integer value = grammar.get(index);
		return new Utterance(index, value);
	}
	
	@Override
	public void invent() {
		
		int chances = config.get("Invention Chances").getInteger();
		for(int j = 0; j < chances && grammar.contains(Utterance.SIGNAL_NULL_VALUE); j++){
			
			if(randomGenerator.random() < config.get("Invention Probability").getDouble()){
				
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
				
				grammar.set(index, randomGenerator.randomInt(config.get("Number of syntactic tokens").getInteger()));
			}
		}
	}

	@Override
	public void learnUtterance(Utterance u) {
		
		//agents agree on value or NULL utterance
		if(u.signal == grammar.get(u.meaning) || u.signal == Utterance.SIGNAL_NULL_VALUE){
			return;
		}
		
		if(u.signal == chromosome.get(u.meaning)){//Matches this agents UG

			if(randomGenerator.random() < config.get("Learning probability match").getDouble()){
				grammar.set(u.meaning, u.signal);
			}
		}else{//Doesn't match this agents UG

			if(randomGenerator.random() < config.get("Learning probability mismatch").getDouble()){
				grammar.set(u.meaning, u.signal);
			}
		}
		

	}

	@Override
	public double geneGrammarMatch() {
		
		int count = 0;
		
		for(int i = 0; i < NUMBER_OF_MEANINGS; i++){
			if(chromosome.get(i).equals(grammar.get(i))){
				count++;
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
	public ArrayList<Integer> getGenotype() {
		return chromosome;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationType type, Graphics g){
		
		Color c;
		
		if(config.parameters.get("Visualization Type").getString().equals("numberNulls")){
			int numberOfNulls = numberOfNulls();
			c = new Color(255, 255-numberOfNulls*16, 255-numberOfNulls*16);
		}else if (config.parameters.get("Visualization Type").getString().equals("genotype")){
			c = new Color(
					Math.abs(chromosome.get(0)*128+chromosome.get(1)*64+chromosome.get(2)*32+chromosome.get(3)*16),
					Math.abs(chromosome.get(4)*128+chromosome.get(5)*64+chromosome.get(6)*32+chromosome.get(7)*16),
					Math.abs(chromosome.get(8)*128+chromosome.get(9)*64+chromosome.get(10)*32+chromosome.get(11)*16)
			);
		}else if (config.parameters.get("Visualization Type").getString().equals("phenotype")){
			c = new Color(
					Math.abs(grammar.get(0)*128+grammar.get(1)*64+grammar.get(2)*32+grammar.get(3)*16),
					Math.abs(grammar.get(4)*128+grammar.get(5)*64+grammar.get(6)*32+grammar.get(7)*16),
					Math.abs(grammar.get(8)*128+grammar.get(9)*64+grammar.get(10)*32+grammar.get(11)*16)
					);
		} else if (config.parameters.get("Visualization Type").getString().equals("singleWord")) {
		
			if(grammar.get(0) == 0){
				c = Color.WHITE;
			} else if (grammar.get(0) == 1){
				c = Color.BLACK;
			} else{
				c = Color.RED;
			}
			
		}	 else if (config.parameters.get("Visualization Type").getString().equals("singleGene")) {
		
			if(chromosome.get(0) == 0){
				c = Color.WHITE;
			} else if (chromosome.get(0) == 1){
				c = Color.BLACK;
			} else{
				c = Color.RED;
			}
			
		}			else {
			System.out.println("Unrecognized visualization type");
			return;
		}

		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
		
	}

}
