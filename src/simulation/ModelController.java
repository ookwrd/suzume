package simulation;

import java.util.ArrayList;

import runTimeVisualization.StepwiseVisualizer;
import statisticsVisualizer.StatisticsVisualizer;
import tools.Pair;

import Agents.Agent;
import Agents.NodeFactory;
import Launcher.Launcher;
import PopulationModel.CompositePopulationModel;
import PopulationModel.Node;

public class ModelController implements Runnable {

	//Configuration Settings
	private SimulationConfiguration config;
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
	private SelectionModel selectionModel;
	
	//Visualization
	private StepwiseVisualizer visualizer;
	private StatisticsVisualizer statisticsWindow;

	//Progress counters
	private Integer currentGeneration = 0;
	private int currentRun = 0;
	private long simulationStart;

	public ModelController(SimulationConfiguration configuration, 
			VisualizationConfiguration visualizationConfiguration, 
			RandomGenerator randomGenerator){
		
		this.config = configuration;
		this.visualConfig = visualizationConfiguration;
		this.randomGenerator = randomGenerator;
		
		this.selectionModel = SelectionModel.constructSelectionModel(config.selectionModelType, randomGenerator);
		
		resetModel();

		resetStatistics();

		if(visualConfig.getParameter(VisualizationConfiguration.ENABLE_TIMESERIES_VISUALIAZATION).getBoolean()){
			this.visualizer = new StepwiseVisualizer(getTitleString(),config.getParameter(SimulationConfiguration.GENERATION_COUNT).getInteger(), population, visualizationConfiguration);
		}
		
	}

	private void resetModel(){
		
		population = new CompositePopulationModel(createIntialAgents(), createIntialAgents());
		
		//TODO temp hack for setting learning distance
		population.setParameter(CompositePopulationModel.LEARN_TO_DISTANCE, config.getParameter(SimulationConfiguration.LEARN_TO_DISTANCE));
		population.setParameter(CompositePopulationModel.COMMUNICATE_TO_DISTANCE, config.getParameter(SimulationConfiguration.COMMUNICATE_TO_DISTANCE));
		
		
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
		ArrayList<Pair<Double,Double>>[] arrayLists = new ArrayList[config.getParameter(SimulationConfiguration.RUN_COUNT).getInteger()];
		for(int i = 0;i < config.getParameter(SimulationConfiguration.RUN_COUNT).getInteger(); i++){
			arrayLists[i] = new ArrayList<Pair<Double,Double>>();
		}
		return arrayLists;
	}

	/**
	 * Initialize an initial population of agents.
	 * 
	 * @return
	 */
	private ArrayList<Node> createIntialAgents(){

		ArrayList<Node> agents = new ArrayList<Node>();
		for (int i = 1; i <= config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger(); i++) {
			agents.add(NodeFactory.constructPopulationNode(config.agentConfig, randomGenerator));
		}

		return agents;
	}


	@Override
	public void run(){
		
		startTimer();
		
		runSimulation();

		plotStatistics();
		
		
	//	clustering(geneGrammarMatches);//TODO add trimming to clustering.
		
		
		System.out.println("Execution completed in: " + longTimeToString(elapsedTime()));
	}

	/**
	 * Main method to run the simulation.
	 */
	public void runSimulation(){

		//Runs
		while(currentRun < config.getParameter(SimulationConfiguration.RUN_COUNT).getInteger()){
		
			//Generations
			while(currentGeneration < config.getParameter(SimulationConfiguration.GENERATION_COUNT).getInteger()){
	
				iterateGeneration();
	
				//Print progress information
				if(visualConfig.getParameter(VisualizationConfiguration.PRINT_GENERATION_COUNT).getBoolean() && currentGeneration % visualConfig.getParameter(VisualizationConfiguration.PRINT_EACH_X_GENERATIONS).getInteger() == 0){
					System.out.println("Run " + currentRun + "/" + config.getParameter(SimulationConfiguration.RUN_COUNT).getInteger() +"\tGeneration " + currentGeneration + "/"+config.getParameter(SimulationConfiguration.GENERATION_COUNT).getInteger()+ "\tElapsed time: " + longTimeToString(elapsedTime()));
				}
	
				//Update stepwise visualization
				if(visualConfig.getParameter(VisualizationConfiguration.ENABLE_TIMESERIES_VISUALIAZATION).getBoolean()){
					visualizer.update(currentRun, currentGeneration);
				}
	
				currentGeneration++;
				
			}
	
			currentRun++;
			currentGeneration = 0;
			
			resetModel();

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
		for(Node learner : population.getCurrentGeneration()){

			//get its ancestors (teachers)
			ArrayList<Node> teachers = population.getPossibleTeachers(learner);

			for(int i = 0; i < config.getParameter(SimulationConfiguration.CRITICAL_PERIOD).getInteger(); i++){

				//Get random teacher
				Node teacher = teachers.get(randomGenerator.randomInt(teachers.size()));

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

			ArrayList<Node> neighbouringAgents = population.getPossibleCommunicators(agent);

			//Set the agents fitness to the default base level 
			agent.setFitness(config.getParameter(SimulationConfiguration.BASE_FITNESS).getInteger());

			//Communicate with all neighbours
			for(Node neighbour : neighbouringAgents){      
				for(int i = 0; i < config.getParameter(SimulationConfiguration.COMMUNICATIONS_PER_NEIGHBOUR).getInteger(); i++){
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
	private ArrayList<Node> selection(){
		
		//TODO make selection dependent on the GetPossibleParents from the populationModel
		
		ArrayList<Agent> selected = selectionModel.selectAgents(population.getCurrentGeneration(), config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger()*2);

		ArrayList<Node> newGenerationAgents = new ArrayList<Node>();
		int i = 0;
		while(newGenerationAgents.size() < config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger()){
			Agent parent1 = selected.get(i++);
			Agent parent2 = selected.get(i++);

			newGenerationAgents.add(NodeFactory.constructPopulationNode(parent1, parent2, randomGenerator));
		}

		return newGenerationAgents;
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

		double learningIntensity = antiLearningIntensity; //(config.populationSize*2*config.communicationsPerNeighbour - antiLearningIntensity) / config.populationSize / 2 / config.communicationsPerNeighbour;

		totalFitnesses[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),totalFitness/config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger()));
		learningIntensities[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),learningIntensity/config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger()));
		geneGrammarMatches[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),genomeGrammarMatch/config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger()));
		numberNulls[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),numberNull/config.getParameter(SimulationConfiguration.POPULATION_SIZE).getInteger()));
		totalNumberGenotypes[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),(double)genotypes.size()));
		totalNumberPhenotypes[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),(double)phenotypes.size()));

	}
	
	/**
	 * 
	 */
	private void plotStatistics() {
		
		statisticsWindow = new StatisticsVisualizer(getTitleString());
		String configName = (config.printName()+"-"+randomGenerator.getSeed()).replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");

		statisticsWindow.addDataSeries(geneGrammarMatches, "Gene Grammar Match", "Gene Grammar Match", configName, false);
		statisticsWindow.addDataSeries(geneGrammarMatches, "Gene Grammar Match", "Gene Grammar Match", configName, true);
		
		statisticsWindow.addDataSeries(learningIntensities, "Learning Intensity", "Learning Intensity", configName, false);
		statisticsWindow.addDataSeries(numberNulls, "Number of Nulls", "Number of Nulls",  configName, false);
		statisticsWindow.addDataSeries(totalFitnesses, "Fitness", "Fitness",  configName, false);
		statisticsWindow.addDataSeries(totalNumberGenotypes, "Number of Genotypes","Number of Genotypes",  configName, false);
		statisticsWindow.addDataSeries(totalNumberPhenotypes, "Number of Phenotypes", "Number of Phenotypes",  configName, false);
		
		statisticsWindow.display();
		
	}
	
	private String getTitleString(){
		return "[Seed: " + randomGenerator.getSeed() + "   " + config.printName() + "]";
	}
    
	private void startTimer(){
		simulationStart = System.currentTimeMillis();
	}
	
	private long elapsedTime(){
		return System.currentTimeMillis() - simulationStart;
	}
	
	private String longTimeToString(long period){
		long seconds = period/1000;
		return "Seconds " + seconds;//TODO
	}
	
	//TODO put this somewhere else.
	private void clustering(ArrayList<Pair<Double, Double>>[] data) {
		ArrayList<Pair<Double, Double>>[] pairData = data;
		
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
		
		//geneClustering.findMarkov();
		this.statisticsWindow.addGraph(geneClustering.visualize("Clustering Graph (step=20)", 20), "Clustering Graph (step=20)");
		this.statisticsWindow.addGraph(geneClustering.visualize("Clustering Graph (step=50)", 50), "Clustering Graph (step=50)");
		this.statisticsWindow.addGraph(geneClustering.visualize("Clustering Graph (step=100)", 100), "Clustering Graph (step=100)");
		this.statisticsWindow.addGraph(geneClustering.visualize("Clustering Graph (step=200)", 200), "Clustering Graph (step=200)");
		this.statisticsWindow.addGraph(geneClustering.visualize("Clustering Graph (step=500)", 500), "Clustering Graph (step=500)");
		
		this.statisticsWindow.saveGraphs("state-transition-graphs-"+this.getTitleString());
		this.statisticsWindow.updateConsoleText(geneClustering.clusteringConsole); // has to be done after the graph rendering
	}
	
	
   	public static void main(String[] args) {
	    new Launcher();
	}

}		
		
