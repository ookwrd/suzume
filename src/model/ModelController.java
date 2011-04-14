package model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;

import model.ChartPanel.ChartType;
import model.ModelStatistics.PlotType;

import tools.Clustering;
import tools.KmeansClustering;
import tools.Pair;
import tools.StateTransitionVisualizer;
import tools.Statistics;

import Agents.Agent;
import Agents.AgentFactory;
import Launcher.Launcher;

public class ModelController implements Runnable {

	//Configuration Settings
	private ModelConfiguration config;
	private VisualizationConfiguration visualConfig;
	private RandomGenerator randomGenerator;

	//Statistics
	private ArrayList<Pair<Double,Double>>[] totalNumberGenotypes;
	private ArrayList<Pair<Double,Double>>[] totalNumberPhenotypes;
	private ArrayList<Pair<Double,Double>>[] totalFitnesses;
	private ArrayList<Pair<Double,Double>>[] learningIntensities;
	private ArrayList<Pair<Double,Double>>[] geneGrammarMatches;
	private ArrayList<Pair<Double,Double>>[] numberNulls;

	//Model
	private PopulationModel population;
	
	//Visualization
	private StepwiseVisualizer visualizer;
	private ModelStatistics statisticsWindow;

	//Progress counters
	private Integer currentGeneration = 0;
	private int currentRun = 0;

	public ModelController(ModelConfiguration configuration, VisualizationConfiguration visualizationConfiguration, RandomGenerator randomGenerator){
		this.config = configuration;
		this.visualConfig = visualizationConfiguration;
		this.randomGenerator = randomGenerator;
		
		resetModel();

		resetStatistics();

		if(visualConfig.enableContinuousVisualization){
			this.visualizer = new StepwiseVisualizer(getTitleString(),population, visualizationConfiguration);
		}
	}

	private void resetModel(){
		population = new OriginalPopulationModel(createIntialAgents(), createIntialAgents());
		if(visualizer!=null){
			visualizer.updateModel(population);
		}
		
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
	private ArrayList<Pair<Double,Double>>[] getInitializedStatisticsArraylist(){
		ArrayList<Pair<Double,Double>>[] arrayLists = new ArrayList[config.numberRuns];
		for(int i = 0;i < config.numberRuns; i++){
			arrayLists[i] = new ArrayList<Pair<Double,Double>>();
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
		
		clustering(geneGrammarMatches);
	}
	
	private void clustering(ArrayList<Pair<Double, Double>>[] data) {
		ArrayList<Pair<Double, Double>>[] pairData = data;//Statistics.trimArrayLists(data,2000,data[0].size());
		
		ArrayList<Double>[] array = new ArrayList[pairData.length];
		
		for (int i = 0; i < pairData.length; i++) {
			array[i] = new ArrayList<Double>();
			for (int j = 0; j < pairData[0].size(); j++) {
				ArrayList<Pair<Double, Double>> tmp = pairData[i];
				Pair<Double, Double> tmp2 = tmp.get(j);
				double d = tmp2.second;
				array[i].add(d);
			}
		}
		SimpleClustering geneClustering = new SimpleClustering(array);
		geneClustering.findMarkov();
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

			//Update stepwise visualization
			if(visualConfig.enableContinuousVisualization){
				visualizer.update(currentRun, currentGeneration);
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

		totalFitnesses[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),totalFitness/config.populationSize));
		learningIntensities[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),learningIntensity/config.populationSize));
		geneGrammarMatches[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),genomeGrammarMatch/config.populationSize));
		numberNulls[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),numberNull/config.populationSize));
		totalNumberGenotypes[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),(double)genotypes.size()));
		totalNumberPhenotypes[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),(double)phenotypes.size()));

	}
	
	/**
	 * 
	 */
	private void plotStatistics() {
		
		statisticsWindow = new ModelStatistics(getTitleString());
		String printName = (config.printName()+"-"+randomGenerator.getSeed()).replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");

		statisticsWindow.plot(geneGrammarMatches, "Gene Grammar Matches", "Occurrences", "Gene Grammar Matches", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(Statistics.calculateDensity(Statistics.aggregateArrayLists(geneGrammarMatches)), "Density (Gene Grammar Matches)", "Occurrences", "Gene Grammar Matches", printName, PlotType.DENSITY);
		statisticsWindow.plot(Statistics.calculateDensity(Statistics.aggregateArrayLists(Statistics.trimArrayLists(geneGrammarMatches,200,geneGrammarMatches[0].size()))), "200 onwards...Density (Gene Grammar Matches)", "Occurrences", "Gene Grammar Matches", printName, PlotType.DENSITY);
		statisticsWindow.plot(learningIntensities, "Learning Intensity", "Occurrences", "Learning Intensity", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(numberNulls, "Number of Nulls", "Occurrences", "Number of Nulls", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(totalFitnesses, "Fitnesses", "Occurrences", "Fitnesses", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(totalNumberGenotypes, "Number of Genotypes", "Occurrences", "Number of Genotypes", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(totalNumberPhenotypes, "Number of Phenotypes", "Occurrences", "Number of Phenotypes", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(Statistics.trimArrayLists(totalNumberGenotypes, 200, totalNumberGenotypes[0].size()), "Number of Genotypes (trim)", "Occurrences", "Number of Genotypes", printName,PlotType.TIMESERIES);
		statisticsWindow.plot(Statistics.trimArrayLists(totalNumberPhenotypes, 200, totalNumberPhenotypes[0].size()), "Number of Phenotypes (trim)", "Occurrences", "Number of Phenotypes", printName,PlotType.TIMESERIES);
		
		statisticsWindow.display();
	}
	
	private String getTitleString(){
		return "[Seed: " + randomGenerator.getSeed() + "   " + config + "]";
	}
    
   	public static void main(String[] args) {
	    new Launcher();
	}

}		
		
