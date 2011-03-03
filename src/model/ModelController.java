package model;

import java.util.ArrayList;

public class ModelController {
	
	private static final int GENERATION_COUNT = 5000; 
	private static final int POPULATION_SIZE = 100; //Should be 200
	
	private static final int BASE_FITNESS = 1;
	private static final int COMMUNICATIONS_PER_NEIGHBOUR = 6;
	
	private static final int CRITICAL_PERIOD = 200; //Number of utterances available to learners
	
	public enum Allele {
		ZERO, ONE, NULL;
	}
	
	//statistics
	private ArrayList<Integer> maxFitnesses = new ArrayList<Integer>();
	private ArrayList<Integer> learningIntensities = new ArrayList<Integer>();
	private ArrayList<Integer> geneGrammarMatches = new ArrayList<Integer>();
	private ArrayList<Integer> numberNulls = new ArrayList<Integer>();
	
	private int nextAgentID = 0; // keeps count of all the next agents from this world
	private PopulationModel population;
	
	private int currentGeneration = 0;
	
	public ModelController(){
		population = new OriginalPopulationModel(createIntialAgents());
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
		return new Agent(nextAgentID++);
	}
	
	/**
	 * Main method to run the simulation once constructed. 
	 */
	public void runSimulation(){
		
		for(; currentGeneration < GENERATION_COUNT; currentGeneration++){
			iterateGeneration();	
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
				Agent teacher = teachers.get((int)(Math.random()*teachers.size()));
	
				teacher.teach(learner);
				
				if(learner.learningResource <= 0){
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
		
			//TODO this is redundant, as the affect of agent n on n+1 are symetrical. 
			//TODO also doesn't match the paper as potential for different utterances used for calculating fitness of agent n and n+1 
			
			ArrayList<Agent> neighbouringAgents = population.getNeighbors(agent, 1);
		
			//Set the agents fitness to the default base level 
			agent.fitness = BASE_FITNESS;
			
			//Communicate with all neighbours
			for(Agent neighbour : neighbouringAgents){
				
				for(int i = 0; i < COMMUNICATIONS_PER_NEIGHBOUR; i++){//TODO the devide by 2 and th adjusting neighbour fitness below is a crude fix
					Utterance utterance = neighbour.getRandomUtterance();

					//If agent and neighbour agree update fitness.
					if(!utterance.isNull() && (agent.grammar.get(utterance.index) == utterance.value)){
						agent.fitness += 1;
					}
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
			newGenerationAgents.add(new Agent(parent1, parent2, nextAgentID++));
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
			totalFitness += agent.fitness;
		}
		
		//Loop once for each individual
		for(int i = 0; i < toSelect; i++){
		
			int selectionPoint = (int)(Math.random() * totalFitness);
			int pointer = 0;

			for(Agent agent : agents){
				//move the pointer along to the next agents borderline
				pointer += agent.fitness;
				
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
		
		int learningIntensity = 0;
		int totalFitness = 0;
		int genomeGrammarMatch = 0;
		int numberNull = 0;
		
		for(Agent agent : population.getCurrentGeneration()){
			totalFitness += agent.fitness;
			learningIntensity += agent.learningResource;
			
			ArrayList<Allele> genomeArrayList = agent.chromosome; 
			ArrayList<Allele> grammarArrayList = agent.grammar;
			for(int i = 0; i < genomeArrayList.size(); i++){
				genomeGrammarMatch += (genomeArrayList.get(i) == grammarArrayList.get(i)?1:0);
				
				if(grammarArrayList.get(i) == Allele.NULL){
					numberNull++;
				}
			}
		}
		
		maxFitnesses.add(totalFitness);
		learningIntensities.add(learningIntensity);
		geneGrammarMatches.add(genomeGrammarMatch);
		numberNulls.add(numberNull);
		
	}
	
	public static void main(String[] args){
		
		//Test selection
		ModelController selector = new ModelController();
		
		selector.runSimulation();
		
		selector.training();
		selector.communication();
		
		System.out.println("Grammars:");
		for(Agent agent : selector.population.getCurrentGeneration()){
			
			System.out.println("Agent " + agent.id + " has fitness of " + agent.fitness );
			System.out.println(agent.grammar);
			System.out.println(agent.chromosome);
			System.out.println();
		}

	
		System.out.println();
		System.out.println("Fitnesses\tlearningResc\tGeneGrammarMatch\tNulls");
		for(int i = 0; i < selector.learningIntensities.size(); i++){
			System.out.println(selector.maxFitnesses.get(i) + "\t" + selector.learningIntensities.get(i) + "\t" + selector.geneGrammarMatches.get(i) + "\t" + selector.numberNulls.get(i));
		}

	}
}
