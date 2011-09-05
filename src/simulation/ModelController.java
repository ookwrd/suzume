package simulation;

import java.util.ArrayList;

import populationNodes.NodeConfiguration;
import populationNodes.NodeFactory;

import runTimeVisualization.RuntimeVisualizer;
import simulation.selectionModels.SelectionModel;
import simulation.selectionModels.SelectionModel.SelectionModels;
import statisticsVisualizer.StatisticsVisualizer;
import tools.Pair;

import Launcher.Launcher;
import PopulationModel.CompositePopulationModel;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;
import PopulationModel.PopulationModel;
import populationNodes.AbstractNode.NodeType;
import populationNodes.Agents.Agent;

import static simulation.SimulationConfiguration.*;

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
	private ArrayList<StatisticsAggregator>[] statsAggregators;
	
	
	//Model
	private PopulationModel population;
	private SelectionModel selectionModel;
	
	//Visualization
	private RuntimeVisualizer visualizer;
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
		
		this.selectionModel = SelectionModel.constructSelectionModel(SelectionModels.valueOf(config.getParameter(SELECTION_MODEL).getString()), randomGenerator);
		
		resetModel();
		
		resetStatistics();

		if(visualConfig.getParameter(VisualizationConfiguration.ENABLE_TIMESERIES_VISUALIAZATION).getBoolean()){
			this.visualizer = new RuntimeVisualizer(getTitleString(),config.getParameter(GENERATION_COUNT).getInteger(), population, visualizationConfiguration);
		}
		
	}

	private void resetModel(){
		
		initializePopulation();
		
		//TODO temp hack for setting learning distance
		population.setParameter(CompositePopulationModel.LEARN_TO_DISTANCE, config.getParameter(LEARN_TO_DISTANCE));
		population.setParameter(CompositePopulationModel.COMMUNICATE_TO_DISTANCE, config.getParameter(COMMUNICATE_TO_DISTANCE));
		
		
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
		
		statsAggregators = getInitializedStatisticsAggregators();		 
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Pair<Double,Double>>[] getInitializedStatisticsArraylist(){
		ArrayList<Pair<Double,Double>>[] arrayLists = new ArrayList[config.getParameter(RUN_COUNT).getInteger()];
		for(int i = 0;i < config.getParameter(RUN_COUNT).getInteger(); i++){
			arrayLists[i] = new ArrayList<Pair<Double,Double>>();
		}
		return arrayLists;
	}
	
	private ArrayList<StatisticsAggregator>[] getInitializedStatisticsAggregators(){
		@SuppressWarnings("unchecked")
		ArrayList<StatisticsAggregator>[] arrayLists = new ArrayList[config.getParameter(RUN_COUNT).getInteger()];
		for(int i = 0;i < config.getParameter(RUN_COUNT).getInteger(); i++){
			arrayLists[i] = new ArrayList<StatisticsAggregator>();
			arrayLists[i].addAll(population.getStatisticsAggregators());
		}
		return arrayLists;
	}

	private void initializePopulation(){
		
		NodeConfiguration nodeConfiguration = config.getParameter(AGENT_TYPE).getNodeConfiguration();
		
		if(NodeType.valueOf(nodeConfiguration.getParameter(NodeConfiguration.NODE_TYPE).getString()) == NodeType.ConfigurablePopulation){
			
			CompositePopulationModel node = (CompositePopulationModel)NodeFactory.constructPopulationNode(nodeConfiguration);
			node.initializeAgent(nodeConfiguration, NodeFactory.nextNodeID++, randomGenerator);
			population = node;
			
			
		} else {
		
			//THis case needs to be gotten rid of.
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (int i = 1; i <= config.getParameter(POPULATION_SIZE).getInteger(); i++) {
				
				Node node = NodeFactory.constructPopulationNode(nodeConfiguration);
				node.initializeAgent(nodeConfiguration, NodeFactory.nextNodeID++, randomGenerator);
				nodes.add(node);
			}
		
			 population = new CompositePopulationModel(nodes, nodes);
		}
		
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
		while(currentRun < config.getParameter(RUN_COUNT).getInteger()){
		
			//Generations
			while(currentGeneration < config.getParameter(GENERATION_COUNT).getInteger()){
	
				iterateGeneration();
	
				//Print progress information
				if(visualConfig.getParameter(VisualizationConfiguration.PRINT_GENERATION_COUNT).getBoolean() && currentGeneration % visualConfig.getParameter(VisualizationConfiguration.PRINT_EACH_X_GENERATIONS).getInteger() == 0){
					System.out.println("Run " + currentRun + "/" + config.getParameter(RUN_COUNT).getInteger() +"\tGeneration " + currentGeneration + "/"+config.getParameter(GENERATION_COUNT).getInteger()+ "\tElapsed time: " + longTimeToString(elapsedTime()));
				}
	
				//Update stepwise visualization
				if(visualConfig.getParameter(VisualizationConfiguration.ENABLE_TIMESERIES_VISUALIAZATION).getBoolean()){
					visualizer.update(currentRun, currentGeneration);
				}
				
				for(StatisticsAggregator agg: statsAggregators[currentRun]){
					agg.endGeneration(currentGeneration);
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

			for(int i = 0; i < config.getParameter(CRITICAL_PERIOD).getInteger(); i++){

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
			agent.setFitness(config.getParameter(BASE_FITNESS).getInteger());

			//Communicate with all neighbours
			for(Node neighbour : neighbouringAgents){      
				for(int i = 0; i < config.getParameter(COMMUNICATIONS_PER_NEIGHBOUR).getInteger(); i++){
					agent.communicate(neighbour);
				}
			}

			agent.adjustFinalFitnessValue();
		}
	}

	/**
	 * Selection and construction of the new generation.
	 * 
	 * @return
	 */
	private ArrayList<Node> selection(){
		
		//TODO make selection dependent on the GetPossibleParents from the populationModel
		
		ArrayList<Agent> selected = selectionModel.selectAgents(population.getCurrentGeneration(), config.getParameter(POPULATION_SIZE).getInteger()*2);

		ArrayList<Node> newGenerationAgents = new ArrayList<Node>();
		int i = 0;
		while(newGenerationAgents.size() < config.getParameter(POPULATION_SIZE).getInteger()){
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

		ArrayList<Agent> agents = population.getCurrentGeneration();
	
		ArrayList<Object> genotypes = new ArrayList<Object>();
		ArrayList<Object> phenotypes = new ArrayList<Object>();
		double antiLearningIntensity = 0;
		double totalFitness = 0;
		double genomeGrammarMatch = 0;
		double numberNull = 0;

		for(Agent agent : agents){
			
			for(StatisticsAggregator aggregator : statsAggregators[currentRun]){
				aggregator.collectStatistics(agent);
			}

			Object chromosome = agent.getGenotype();
			if (!genotypes.contains(chromosome)){
				genotypes.add(chromosome);
			}

			Object phenotype = agent.getPhenotype();
			if(!phenotypes.contains(phenotype)){
				phenotypes.add(phenotype);
			}

			totalFitness += agent.getFitness();
			antiLearningIntensity += agent.learningIntensity();

			numberNull += agent.numberOfNulls();
			genomeGrammarMatch += agent.geneGrammarMatch();
		}

		double learningIntensity = antiLearningIntensity; 
		
		totalFitnesses[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),totalFitness/config.getParameter(POPULATION_SIZE).getInteger()));
		learningIntensities[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),learningIntensity/config.getParameter(POPULATION_SIZE).getInteger()));
		geneGrammarMatches[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),genomeGrammarMatch/config.getParameter(POPULATION_SIZE).getInteger()));
		numberNulls[currentRun].add(new Pair<Double,Double>(currentGeneration.doubleValue(),numberNull/config.getParameter(POPULATION_SIZE).getInteger()));
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
		

		System.out.println("Size" + statsAggregators[0].size());
		for(int i = 0; i < statsAggregators[0].size(); i++){
			ArrayList[] array = new ArrayList[config.getParameter(RUN_COUNT).getInteger()];
			for(int run = 0; run < statsAggregators.length; run++){
				StatisticsAggregator aggregator = statsAggregators[run].get(i);
				array[run] = aggregator.getStatistics();
			}
			statisticsWindow.addDataSeries(array, "Test", "A", "B", false);
		}
		
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
		
