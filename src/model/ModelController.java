package model;

import java.util.ArrayList;

import model.ModelConfiguration.AgentType;

import Agents.Agent;
import Agents.BiasAgent;
import Agents.OriginalAgent;
import Agents.SynonymAgent;

public class ModelController {
	
	private ModelConfiguration config;
	
 	//statistics
	private ArrayList<Double> totalNumberGenotypes = new ArrayList<Double>();
	private ArrayList<Double> totalFitnesses = new ArrayList<Double>();
	private ArrayList<Double> learningIntensities = new ArrayList<Double>();
	private ArrayList<Double> geneGrammarMatches = new ArrayList<Double>();
	private ArrayList<Double> numberNulls = new ArrayList<Double>();
	
	private int nextAgentID = 0; // keeps count of all the next agents from this world
	private PopulationModel population;
	
	private int currentGeneration = 0;
	
	private RandomGenerator randomGenerator = RandomGenerator.getGenerator();
	
	public ModelController(ModelConfiguration configuration){
		this.config = configuration;
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
			agents.add(createRandomAgent());
		}
		
		return agents;
	}
	
	/**
	 * Creates an agent with a new ID and a random genome.
	 * 
	 * @return
	 */
	private Agent createRandomAgent(){
		
		/*
		 * When adding new agent types make sure to add a sexual reproduction 
		 * constructor as well in the "selection " method.
		 */
		
		if(config.agentType == AgentType.OriginalAgent){
			return new OriginalAgent(nextAgentID++);
		}else if (config.agentType == AgentType.BiasAgent){
			return new BiasAgent(nextAgentID++);
		}else if (config.agentType == AgentType.SynonymAgent){
			return new SynonymAgent(nextAgentID, SynonymAgent.DEFAULT_MEMEORY_SIZE);
		}else{
			System.err.println("Unsupported Agent type");
			return null;
		}
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
			
			if(config.agentType == AgentType.OriginalAgent){
				newGenerationAgents.add(new OriginalAgent((OriginalAgent)parent1, (OriginalAgent)parent2, nextAgentID++));
			}else if (config.agentType == AgentType.BiasAgent){
				newGenerationAgents.add(new BiasAgent((BiasAgent)parent1, (BiasAgent)parent2, nextAgentID++));
			} else if (config.agentType == AgentType.SynonymAgent){
				newGenerationAgents.add(new SynonymAgent((SynonymAgent)parent1,(SynonymAgent)parent2,nextAgentID++));
			}else{
				System.err.println("Unsupported Agent type");
				return null;
			}
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
		
		ModelStatistics statsWindow = new ModelStatistics("[Seed: " + RandomGenerator.randomSeed + "   " + config + "]");
		
		statsWindow.plot(learningIntensities, "Learning Intensities");
		statsWindow.plot(numberNulls, "Number of Nulls");
		statsWindow.plot(totalNumberGenotypes, "Total Number of Genotypes");
		statsWindow.plot(geneGrammarMatches, "Gene Grammar Matches");
		statsWindow.plot(totalFitnesses, "Total Fitnesses");
		statsWindow.plot(totalNumberGenotypes, "Total Number of Genotypes");
		
		statsWindow.display();
	}
	
	
	public void run(){
		runSimulation();
		
		training();
		communication();
		
		plot();
	}
	
	public static void main(String[] args){
		
		//Test selection
		ModelController selector = new ModelController(new ModelConfiguration());

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
