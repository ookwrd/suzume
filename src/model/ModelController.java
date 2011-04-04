package model;

import java.util.ArrayList;
import java.util.Hashtable;

import Agents.Agent;
import Agents.AgentFactory;
import Launcher.Launcher;

public class ModelController implements Runnable {

	private static final double DEFAULT_DENSITY_GRANULARITY = 0.001;//should be set lower than 0.01 //TODO refactor

	//Configuration Settings
	private ModelConfiguration config;
	private VisualizationConfiguration visualConfig;
	private RandomGenerator randomGenerator;

	//Statistics
	private ArrayList<Double>[] totalNumberGenotypes;
	private ArrayList<Double>[] totalNumberPhenotypes;
	private ArrayList<Double>[] totalFitnesses;
	private ArrayList<Double>[] learningIntensities;
	private ArrayList<Double>[] geneGrammarMatches;
	private ArrayList<Double>[] numberNulls;

	//Model
	private PopulationModel population;

	//Progress counters
	private int currentGeneration = 0;
	private int currentRun = 0;

	public ModelController(ModelConfiguration configuration, VisualizationConfiguration visualizationConfiguration, RandomGenerator randomGenerator){
		this.config = configuration;
		this.visualConfig = visualizationConfiguration;
		this.randomGenerator = randomGenerator;

		resetModel();

		resetStatistics();
	}

	private void resetModel(){
		population = new OriginalPopulationModel(createIntialAgents(), createIntialAgents());
	}

	private void resetStatistics(){
		totalNumberGenotypes = getInitializedStatisticsArraylist();
		totalNumberPhenotypes = getInitializedStatisticsArraylist();
		totalFitnesses = getInitializedStatisticsArraylist();
		learningIntensities = getInitializedStatisticsArraylist();
		geneGrammarMatches = getInitializedStatisticsArraylist();
		numberNulls = getInitializedStatisticsArraylist(); 
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Double>[] getInitializedStatisticsArraylist(){
		ArrayList<Double>[] arrayLists = new ArrayList[config.numberRuns];
		for(int i = 0;i < config.numberRuns; i++){
			arrayLists[i] = new ArrayList<Double>();
		}
		return arrayLists;
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


	@Override
	public void run(){
		runSimulation();

		//Extra steps so we can get to where we can plot statistics.
		training();
		communication();

		plotStatistics();
	}

	/**
	 * Main method to run the simulation.
	 */
	public void runSimulation(){

		currentGeneration = 0;

		while(currentGeneration < config.generationCount){

			iterateGeneration();

			//Print progress information
			if(visualConfig.printGenerations && currentGeneration % visualConfig.printGenerationsEachX == 0){
				System.out.println("Run " + currentRun + " Generation " + currentGeneration);
			}

			//Print slice generation
			if(visualConfig.printSliceGeneration && currentGeneration == visualConfig.sliceGeneration){
				printGeneration();
			}

			currentGeneration++;
		}

		currentRun++;

		//Have we completed the required number of runs?
		if (currentRun < config.numberRuns) {
			resetModel();
			runSimulation();
		}
	}

	/**
	 * Print a generations worth of agents. 
	 * 
	 *  //TODO re-factor into the model interface.
	 */
	private void printGeneration(){
		System.out.println("Printing Previous Generation");	
		for(Agent agent : population.getAncestorGeneration()){  
			agent.printAgent();
			System.out.println();
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
		ArrayList phenotypes = new ArrayList();
		double antiLearningIntensity = 0;
		double totalFitness = 0;
		double genomeGrammarMatch = 0;
		double numberNull = 0;

		for(Agent agent : population.getCurrentGeneration()){

			ArrayList<Integer> chromosome = agent.getGenotype();
			if (!genotypes.contains(chromosome)){
				genotypes.add(chromosome);
			}

			ArrayList<Integer> phenotype = agent.getPhenotype();
			if(!phenotypes.contains(phenotype)){
				phenotypes.add(phenotype);
			}

			totalFitness += agent.getFitness();
			antiLearningIntensity += agent.learningIntensity();

			numberNull += agent.numberOfNulls();
			genomeGrammarMatch += agent.geneGrammarMatch();
		}

		double learningIntensity = (config.populationSize*2*config.communicationsPerNeighbour - antiLearningIntensity) / config.populationSize / 2 / config.communicationsPerNeighbour;

		totalFitnesses[currentRun].add(totalFitness/config.populationSize);
		learningIntensities[currentRun].add(learningIntensity/config.populationSize);
		geneGrammarMatches[currentRun].add(genomeGrammarMatch/config.populationSize);
		numberNulls[currentRun].add(numberNull/config.populationSize);
		totalNumberGenotypes[currentRun].add((double)genotypes.size());
		totalNumberPhenotypes[currentRun].add((double)phenotypes.size());

	}
	
	/**
	 * 
	 */
	private void plotStatistics() {
		
		ModelStatistics densityWindow = new ModelStatistics("[Seed: " + randomGenerator.getSeed() + "   " + config + "]");
		String printName = (config.printName()+"-seed"+randomGenerator.getSeed()).replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");

		densityWindow.plot(geneGrammarMatches, "Gene Grammar Matches", "Occurrences", "Gene Grammar Matches", printName);
		densityWindow.plot(calculateDensity(geneGrammarMatches), "Density (Gene Grammar Matches)", "Occurrences", "Gene Grammar Matches", printName);
		densityWindow.plot(calculateDensity(trimArrayLists(geneGrammarMatches,200,geneGrammarMatches[0].size())), "200 onwards...Density (Gene Grammar Matches)", "Occurrences", "Gene Grammar Matches", printName);
		densityWindow.plot(learningIntensities, "Learning Intensity", "Occurrences", "Learning Intensity", printName);
		densityWindow.plot(calculateDensity(learningIntensities), "Density (Learning Intensity)", "Occurrences", "Learning Intensity", printName);
		densityWindow.plot(numberNulls, "Number of Nulls", "Occurrences", "Number of Nulls", printName);
		densityWindow.plot(totalFitnesses, "Fitnesses", "Occurrences", "Fitnesses", printName);
		densityWindow.plot(totalNumberGenotypes, "Number of Genotypes", "Occurrences", "Number of Genotypes", printName);
		densityWindow.plot(totalNumberPhenotypes, "Number of Phenotypes", "Occurrences", "Number of Phenotypes", printName);

		densityWindow.display();
	}

	/**
	 * Internally statistics are handled as time ordered arraylists, this method allows the user to 
	 * take a subsection of these arrays to focus on a section of interest.
	 * 
	 * @param arrays
	 * @param start
	 * @param finish
	 * @return
	 */
	public static ArrayList<Double>[] trimArrayLists(ArrayList<Double>[] arrays, int start, int finish){

		@SuppressWarnings("unchecked")
		ArrayList<Double>[] outputArrays = new ArrayList[arrays.length];

		for(int i = 0; i < arrays.length && i < finish; i++){
			outputArrays[i] = new ArrayList<Double>();

			for(int j = start; j < arrays[i].size(); j++){
				outputArrays[i].add(arrays[i].get(j));
			}
		}

		return outputArrays;
	}


	/**
	 * Calculate density for an array
	 * @param array
	 */
	public static Hashtable<Double, Integer> calculateDensity(ArrayList<Double>[] array) {
		//globalGeneGrammarMatches.addAll(geneGrammarMatches[currentRun]); // total for several runs

		Hashtable<Double, Integer> numOccurrences = 
			new Hashtable<Double, Integer>(); // k:value->v:count
		double pace;

		// find max-min
		Double min = 1000000000.0;
		Double max = -1000000000.0;
		for (int i = 0; i < array.length; i++) { 
			//Double minCandidate = Collections.min(array[i]);
			//if ((Double) minCandidate < (Double) min) min = minCandidate;
			for (int j = 0; j < array[i].size(); j++) {
				if ((Double) array[i].get(j) > (Double) max) 
					max = array[i].get(j);
			}
		}
		System.out.println(max);

		min=0.0;//quick fix TODO
		pace = DEFAULT_DENSITY_GRANULARITY*(max-min);
		//System.out.println("max-min: "+(max-min)+">< pace: "+pace);

		for (int i = 0; i < array.length; i++) { // for every run
			for (int j = 0; j < array[0].size(); j++) {
				// cluster value
				double clusterVal = pace*(double) Math.round(array[i].get(j)/pace);

				// count
				if(numOccurrences.containsKey(clusterVal))
					numOccurrences.put(clusterVal, numOccurrences.get(clusterVal)+1);
				else
					numOccurrences.put(clusterVal, 1);
			}
		}

		/*
        	ArrayList<Double>[] dataSets = new ArrayList[numOccurrences.size()];
        	for(int j = 0; j<dataSets.length; j++) {
        		dataSets[0] = new ArrayList<Double>();
	        	Enumeration<Double> e = numOccurrences.keys();
	        	int i = 0;
	        	while(e.hasMoreElements()) {
	        		Double val = e.nextElement();
	        		//dataSets[0].add(i, (double) val);
	        		dataSets[j].add(i, (double) numOccurrences.get(val));
	        		i++;
	        	}

        	}*/
		return numOccurrences;
	}
	
	public static void main(String[] args) {
		new Launcher();
	}

}