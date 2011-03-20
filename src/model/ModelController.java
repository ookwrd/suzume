package model;

import java.util.ArrayList;

import Agents.Agent;
import Agents.BiasAgent;
import Agents.OriginalAgent;
import Agents.SynonymAgent;

public class ModelController {
	
	private enum AgentType { OriginalAgent, BiasAgent, SynonymAgent, TestAgent };
	//public AgentType currentAgentType = AgentType.OriginalAgent;
	//public AgentType currentAgentType = AgentType.BiasAgent;
	public AgentType currentAgentType = AgentType.SynonymAgent;
	//public AgentType currentAgentType = AgentType.TestAgent;
	
	private static final int GENERATION_COUNT = 500; 
	private static final int POPULATION_SIZE = 200; //Should be 200
	
	private static final int BASE_FITNESS = 1;
	private static final int COMMUNICATIONS_PER_NEIGHBOUR = 6;
	
	private static final int CRITICAL_PERIOD = 200; //Number of utterances available to learners
	
 	//statistics
	private ArrayList<Double> totalFitnesses = new ArrayList<Double>();
	private ArrayList<Double> learningIntensities = new ArrayList<Double>();
	private ArrayList<Double> geneGrammarMatches = new ArrayList<Double>();
	private ArrayList<Double> numberNulls = new ArrayList<Double>();
	
	private int nextAgentID = 0; // keeps count of all the next agents from this world
	private PopulationModel population;
	
	private int currentGeneration = 0;
	
	private RandomGenerator randomGenerator = RandomGenerator.getGenerator();
	
	public ModelController(){
		population = new OriginalPopulationModel(createIntialAgents(), createIntialAgents());
	}
	
	/**
	 * Initialize an initial population of agents.
	 * 
	 * @return
	 */
	private ArrayList<Agent> createIntialAgents(){
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		for (int i = 1; i <= POPULATION_SIZE; i++) {
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
		
		if(currentAgentType == AgentType.OriginalAgent){
			return new OriginalAgent(nextAgentID++);
		}else if (currentAgentType == AgentType.BiasAgent){
			return new BiasAgent(nextAgentID++);
		}else if (currentAgentType == AgentType.SynonymAgent){
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
		
		for(currentGeneration = 0; currentGeneration < GENERATION_COUNT; currentGeneration++){
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
			
			for(int i = 0; i < CRITICAL_PERIOD; i++){
				
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
			agent.setFitness(BASE_FITNESS);
			
			//Communicate with all neighbours
			for(Agent neighbour : neighbouringAgents){	
				for(int i = 0; i < COMMUNICATIONS_PER_NEIGHBOUR; i++){
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
		
		ArrayList<Agent> selected = select(POPULATION_SIZE*2, population.getCurrentGeneration());
		
		ArrayList<Agent> newGenerationAgents = new ArrayList<Agent>();
		int i = 0;
		while(newGenerationAgents.size() < POPULATION_SIZE){
			Agent parent1 = selected.get(i++);
			Agent parent2 = selected.get(i++);
			
			if(currentAgentType == AgentType.OriginalAgent){
				newGenerationAgents.add(new OriginalAgent((OriginalAgent)parent1, (OriginalAgent)parent2, nextAgentID++));
			}else if (currentAgentType == AgentType.BiasAgent){
				newGenerationAgents.add(new BiasAgent((BiasAgent)parent1, (BiasAgent)parent2, nextAgentID++));
			} else if (currentAgentType == AgentType.SynonymAgent){
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
		
		double antiLearningIntensity = 0;
		double totalFitness = 0;
		double genomeGrammarMatch = 0;
		double numberNull = 0;
		
		for(Agent agent : population.getCurrentGeneration()){
			totalFitness += agent.getFitness();
			antiLearningIntensity += agent.learningIntensity();
			
			numberNull += agent.numberOfNulls();
			genomeGrammarMatch += agent.geneGrammarMatch();
		}
		
		double learningIntensity = POPULATION_SIZE*2*COMMUNICATIONS_PER_NEIGHBOUR - antiLearningIntensity; // opposite value
		
		totalFitnesses.add(totalFitness);
		learningIntensities.add(learningIntensity);
		geneGrammarMatches.add(genomeGrammarMatch);
		numberNulls.add(numberNull);
		
		//totalFitnesses.add(new Integer((int) (new Double(totalFitness)/POPULATION_SIZE)));
		//learningIntensities.add(new Integer((int) (new Double(learningIntensity)/POPULATION_SIZE)));
		//geneGrammarMatches.add(new Integer((int) (new Double(genomeGrammarMatch)/POPULATION_SIZE)));
		//numberNulls.add(new Integer((int) (new Double(numberNull)/POPULATION_SIZE)));
		
	}
	
	/**
	 * Show all plots
	 */
	private void plot() {
		
		ModelStatistics.plot(learningIntensities, "Learning Intensities");
		ModelStatistics.plot(numberNulls, "Number of Nulls");
		ModelStatistics.plot(geneGrammarMatches, "Gene Grammar Matches");
		ModelStatistics.plot(totalFitnesses, "Total Fitnesses");
		
	}
	
	public static void main(String[] args){
		
		//Test selection
		ModelController selector = new ModelController();

		System.out.println("Using seed:" + selector.randomGenerator.getSeed());
		
		//TODO print other simulation parameters
		System.out.println();
		
		selector.runSimulation();
		
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
	}

}
