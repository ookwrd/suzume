package model;

import java.util.ArrayList;
import Agents.Agent;
import Agents.AgentFactory;

public class ModelController implements Runnable {
	
	private ModelConfiguration config;
	
 	//statistics
	private ArrayList<Double> totalNumberGenotypes = new ArrayList<Double>();
	private ArrayList<Double> totalFitnesses = new ArrayList<Double>();
	private ArrayList<Double> learningIntensities = new ArrayList<Double>();
	private ArrayList<Double> geneGrammarMatches = new ArrayList<Double>();
	private ArrayList<Double> numberNulls = new ArrayList<Double>();
	
	private PopulationModel population;
	
	private int currentGeneration = 0;
	
	private RandomGenerator randomGenerator;
	
	public ModelController(ModelConfiguration configuration, RandomGenerator randomGenerator){
		this.config = configuration;
		this.randomGenerator = randomGenerator;
		population = new OriginalPopulationModel(createIntialAgents(), createIntialAgents());
	}
	
	/**
	 * Initialize an initial population of agents.
	 * 
	 * @return
	 */
	private ArrayList<Agent> createIntialAgents(){
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		for (int i = 1; i <= config.populationSize; i++) {
			agents.add(AgentFactory.constructAgent(config.agentConfig, randomGenerator));
		}
		
		return agents;
	}
	
	/**
	 * Main method to run the simulation once constructed. 
	 */
	public void runSimulation(){
		
		for(currentGeneration = 0; currentGeneration < config.generationCount; currentGeneration++){
			iterateGeneration();
			
			//Print progress information
			if(currentGeneration % 1000 == 0){
				System.out.println("Generation " + currentGeneration);
			}
		}
	}
	
	/**
	 * Runs a single round of the simulation. 
	 */
	private void iterateGeneration(){
		
		training();
		
		communication();
		
		gatherStatistics();
		
		population.switchGenerations(selection());
		
	}
	
	/**
	 * Training and invention phase of a single round of the simulation.
	 */
	private void training(){
		
		//for each agent
		for(Agent learner : population.getCurrentGeneration()){
			
			//get its ancestors (teachers)
			ArrayList<Agent> teachers = population.getAncestors(learner, 2);
			
			for(int i = 0; i < config.criticalPeriod; i++){
				
				//Get random teacher
				Agent teacher = teachers.get(randomGenerator.randomInt(teachers.size()));
	
				teacher.teach(learner);
				
				if(!learner.canStillLearn()){
					break;
				}
			}
	
			//Use leftover learning resource to potentially invent new grammar items.
			learner.invent();
		}
		
	}
	
	/**
	 * Communication Phase  which calculates the fitness of all agents in the population.
	 */
	private void communication(){
		
		for(Agent agent : population.getCurrentGeneration()){
		
			ArrayList<Agent> neighbouringAgents = population.getNeighbors(agent, 1);
		
			//Set the agents fitness to the default base level 
			agent.setFitness(config.baseFitness);
			
			//Communicate with all neighbours
			for(Agent neighbour : neighbouringAgents){	
				for(int i = 0; i < config.communicationsPerNeighbour; i++){
					agent.communicate(neighbour);
				}
			}
			
			agent.adjustCosts();
		}
	}
	
	/**
	 * Selection and construction of the new generation.
	 * 
	 * @return
	 */
	private ArrayList<Agent> selection(){
		
		ArrayList<Agent> selected = select(config.populationSize*2, population.getCurrentGeneration());
		
		ArrayList<Agent> newGenerationAgents = new ArrayList<Agent>();
		int i = 0;
		while(newGenerationAgents.size() < config.populationSize){
			Agent parent1 = selected.get(i++);
			Agent parent2 = selected.get(i++);

			newGenerationAgents.add(AgentFactory.constructAgent(parent1, parent2, randomGenerator));
		}
		
		return newGenerationAgents;
	}
	
	/**
	 * TODO optimize this class
	 * TODO allow ability to disable the selection of multiples
	 * 
	 * @param toSelect
	 * @param agents
	 * @return
	 */
	public ArrayList<Agent> select(int toSelect, ArrayList<Agent> agents){
		
		ArrayList<Agent> toReturn = new ArrayList<Agent>();
		
		//Calculate total fitness of all agents.
		int totalFitness = 0;
		for(Agent agent : agents){
			totalFitness += agent.getFitness();
		}
		
		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){
		
			int selectionPoint = randomGenerator.randomInt(totalFitness);
			int pointer = 0;

			for(Agent agent : agents){
				//move the pointer along to the next agents borderline
				pointer += agent.getFitness();
				
				//have we gone past the selectionPoint?
				if(pointer > selectionPoint){
					toReturn.add(agent);
					break;
				}
			}
		}
		
		return toReturn;
	}
	
	/**
	 * Gather statistics on the population at this point.
	 */
	private void gatherStatistics(){
		
		ArrayList genotypes = new ArrayList();
		double antiLearningIntensity = 0;
		double totalFitness = 0;
		double genomeGrammarMatch = 0;
		double numberNull = 0;
		
		for(Agent agent : population.getCurrentGeneration()){
			
			ArrayList<Integer> chromosome = agent.getChromosome();
			if (!genotypes.contains(chromosome)){
				genotypes.add(chromosome);
			}
			
			totalFitness += agent.getFitness();
			antiLearningIntensity += agent.learningIntensity();
			
			numberNull += agent.numberOfNulls();
			genomeGrammarMatch += agent.geneGrammarMatch();
		}
		
		double learningIntensity = (config.populationSize*2*config.communicationsPerNeighbour - antiLearningIntensity) / config.populationSize / 2 / config.communicationsPerNeighbour;
		totalFitnesses.add(totalFitness/config.populationSize);
		learningIntensities.add(learningIntensity/config.populationSize);
		geneGrammarMatches.add(genomeGrammarMatch/config.populationSize);
		numberNulls.add(numberNull/config.populationSize);
		totalNumberGenotypes.add((double)genotypes.size());
		
	}
	
	
	
	/**
	 * Show all plots
	 */
	private void plot() {
		
		ModelStatistics statsWindow = new ModelStatistics("[Seed: " + randomGenerator.getSeed() + "   " + config + "]");
		String printName = config.printName().replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");
		statsWindow.plot(learningIntensities, "Learning Intensities", printName);
		statsWindow.plot(numberNulls, "Number of Nulls", printName);
		statsWindow.plot(geneGrammarMatches, "Gene Grammar Matches", printName);
		statsWindow.plot(totalFitnesses, "Total Fitnesses", printName);
		statsWindow.plot(totalNumberGenotypes, "Total Number of Genotypes", printName);
		
		statsWindow.display();
	}
	
	@Override
	public void run(){
		runSimulation();
		
		training();
		communication();
		
		plot();
	}
	
	public static void main(String[] args){
		
		//Test selection
		ModelController selector = new ModelController(new ModelConfiguration(), new RandomGenerator());

		System.out.println("Using seed:" + selector.randomGenerator.getSeed());
		
		//TODO print other simulation parameters
		System.out.println();
		
		selector.runSimulation();
		
		//Get to a point where the current generation has calculated fitness. TODO swap out with previous generation.
		selector.training();
		selector.communication();
		
		for(Agent agent : selector.population.getCurrentGeneration()){	
			agent.printAgent();
			System.out.println();
		}
		
		System.out.println();
		System.out.println("Fitnesses\tlearningResc\tGeneGrammarMatch\tNulls");
		for(int i = 0; i < selector.learningIntensities.size(); i++){
			System.out.println(selector.totalFitnesses.get(i) + "\t" + selector.learningIntensities.get(i) + "\t" + selector.geneGrammarMatches.get(i) + "\t" + selector.numberNulls.get(i));
		}
		
		//Plot
		selector.plot();
		
		for(Agent agent : selector.population.getCurrentGeneration()){
			System.out.println(agent.getChromosome());
		}
		
		Object double1 = new Double(12342.09);
		
		System.out.println(double1);
	}

}
