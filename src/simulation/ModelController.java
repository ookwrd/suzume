package simulation;

import java.util.ArrayList;

import javax.swing.border.TitledBorder;

import populationNodes.NodeConfiguration;
import populationNodes.NodeFactory;

import runTimeVisualization.RuntimeVisualizer;
import runTimeVisualization.Visualizable.Stoppable;
import simulation.selectionModels.SelectionModel;
import simulation.selectionModels.SelectionModel.SelectionModels;
import statisticsVisualizer.StatisticsVisualizer;
import tools.Pair;

import AutoConfiguration.BasicConfigurable;
import AutoConfiguration.BasicConfigurationPanel;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.CompositePopulationModel;
import PopulationModel.Node;
import PopulationModel.Node.StatisticsAggregator;
import PopulationModel.PopulationModel;
import populationNodes.AbstractNode.NodeType;
import populationNodes.Agents.Agent;
import populationNodes.Agents.YamauchiHashimoto2010;

public class ModelController extends BasicConfigurable implements Runnable, Stoppable {

	public static final String AGENT_TYPE = "Agent1";
	public static final String GENERATION_COUNT = "Number of Generations:";
	public static final String RUN_COUNT = "Number of Runs";
	public static final String POPULATION_SIZE = "Population Size:";
	public static final String COMMUNICATIONS_PER_NEIGHBOUR = "CommunicationsPerNeighbour:";//TODO remove to population model
	public static final String CRITICAL_PERIOD = "Critical Period:";
	
	public static final String LEARN_TO_DISTANCE = "Max learning distance:";//TODO remove these should be recursive
	public static final String COMMUNICATE_TO_DISTANCE = "Max Communication distance:";
	public static final String REPRODUCE_TO_DISTANCE = "Max reproduction distance:";
	
	public static final String SELECTION_MODEL = "Selection model:";
	
	public static final String PRINT_GENERATION_COUNT = "Print generation count?";
	public static final String PRINT_EACH_X_GENERATIONS = "Print each X generations";
	
	{
		System.out.println("SimulationConfig" + new YamauchiHashimoto2010());
		
		setDefaultParameter(AGENT_TYPE, new ConfigurationParameter(new YamauchiHashimoto2010()));
		setDefaultParameter(GENERATION_COUNT, new ConfigurationParameter(5000));
		setDefaultParameter(RUN_COUNT, new ConfigurationParameter(10));
		setDefaultParameter(POPULATION_SIZE, new ConfigurationParameter(200));
		setDefaultParameter(COMMUNICATIONS_PER_NEIGHBOUR, new ConfigurationParameter(6));
		setDefaultParameter(CRITICAL_PERIOD, new ConfigurationParameter(200));
		setDefaultParameter(LEARN_TO_DISTANCE, new ConfigurationParameter(2));
		setDefaultParameter(COMMUNICATE_TO_DISTANCE, new ConfigurationParameter(1));
		setDefaultParameter(REPRODUCE_TO_DISTANCE, new ConfigurationParameter(-1));
		setDefaultParameter(SELECTION_MODEL, new ConfigurationParameter(SelectionModels.values()));

		setDefaultParameter(PRINT_GENERATION_COUNT, new ConfigurationParameter(true));
		setDefaultParameter(PRINT_EACH_X_GENERATIONS, new ConfigurationParameter(1000));
	}
	
	//Configuration Settings
	private RandomGenerator randomGenerator;

	//Statistics
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

	//Stoppable
	private boolean continueSimulation = true;
	
	public ModelController(){}
	
	public ModelController(BasicConfigurable baseConfig, 
			RandomGenerator randomGenerator){
		super(baseConfig);
		
		//this.config = configuration;
		this.randomGenerator = randomGenerator;
		
		this.selectionModel = SelectionModel.constructSelectionModel((SelectionModels)getParameter(SELECTION_MODEL).getSelectedValue(), randomGenerator);
		
		resetModel();
		
		resetStatistics();
	
		if(population.getVisualizationKeys().size() != 0){
			this.visualizer = new RuntimeVisualizer(getTitleString(),getIntegerParameter(GENERATION_COUNT), population, this);
		}
		
	}

	private void resetModel(){
		
		initializePopulation();
		
		//TODO temp hack for setting learning distance
		population.setParameter(CompositePopulationModel.LEARN_TO_DISTANCE, getParameter(LEARN_TO_DISTANCE));
		population.setParameter(CompositePopulationModel.COMMUNICATE_TO_DISTANCE, getParameter(COMMUNICATE_TO_DISTANCE));
		population.setParameter(CompositePopulationModel.REPRODUCE_TO_DISTANCE, getParameter(REPRODUCE_TO_DISTANCE));
	
		if(visualizer!=null){
			visualizer.updateModel(population);
		}
		
	}

	private void resetStatistics(){
		statsAggregators = getInitializedStatisticsAggregators();		 
	}
	
	private ArrayList<StatisticsAggregator>[] getInitializedStatisticsAggregators(){
		@SuppressWarnings("unchecked")
		ArrayList<StatisticsAggregator>[] arrayLists = new ArrayList[getIntegerParameter(RUN_COUNT)];
		for(int i = 0;i < getParameter(RUN_COUNT).getInteger(); i++){
			arrayLists[i] = new ArrayList<StatisticsAggregator>();
			arrayLists[i].addAll(population.getStatisticsAggregators());
		}
		return arrayLists;
	}

	private void initializePopulation(){
		
		NodeConfiguration nodeConfiguration = getParameter(AGENT_TYPE).getNodeConfiguration();
		
		if(nodeConfiguration.getParameter(NodeConfiguration.NODE_TYPE).getSelectedValue() == NodeType.ConfigurablePopulation){
			
			CompositePopulationModel node = (CompositePopulationModel)NodeFactory.constructPopulationNode(nodeConfiguration);
			node.initializeAgent(nodeConfiguration, NodeFactory.nextNodeID++, randomGenerator);
			population = node;
			
			
		} else {
		
			//THis case needs to be gotten rid of.
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (int i = 1; i <= getIntegerParameter(POPULATION_SIZE); i++) {
				
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
		
		System.out.println("Execution completed in: " + longTimeToString(elapsedTime()));
	}

	/**
	 * Main method to run the simulation.
	 */
	public void runSimulation(){

		//Runs
		while(currentRun < getIntegerParameter(RUN_COUNT)){
		
			//Generations
			while(continueSimulation && currentGeneration < getIntegerParameter(GENERATION_COUNT)){
	
				iterateGeneration();
	
				//Print progress information
				if(getParameter(PRINT_GENERATION_COUNT).getBoolean() && currentGeneration % getIntegerParameter(PRINT_EACH_X_GENERATIONS) == 0){
					System.out.println("Run " + currentRun + "/" + getIntegerParameter(RUN_COUNT) +"\tGeneration " + currentGeneration + "/"+getIntegerParameter(GENERATION_COUNT)+ "\tElapsed time: " + longTimeToString(elapsedTime()));
				}
	
				//Update stepwise visualization
				if(visualizer != null){
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

			for(int i = 0; i < getIntegerParameter(CRITICAL_PERIOD); i++){

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

			//Communicate with all neighbours
			for(Node neighbour : neighbouringAgents){      
				for(int i = 0; i < getIntegerParameter(COMMUNICATIONS_PER_NEIGHBOUR); i++){
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
		
		ArrayList<Node> newGenerationAgents = new ArrayList<Node>();
		
		for(Agent agent : population.getCurrentGeneration()){
			ArrayList<Node> possibleParents = population.getPossibleParents(agent);
			ArrayList<Node> parents = selectionModel.selectAgents(possibleParents, 2);
			
			newGenerationAgents.add(NodeFactory.constructPopulationNode((Agent)parents.get(0), (Agent)parents.get(1), randomGenerator));
		}

		return newGenerationAgents;
	}

	/**
	 * Gather statistics on the population at this point.
	 */
	private void gatherStatistics(){

		ArrayList<Agent> agents = population.getCurrentGeneration();

		for(Agent agent : agents){
			for(StatisticsAggregator aggregator : statsAggregators[currentRun]){
				aggregator.collectStatistics(agent);
			}
		}
		
	}
	
	/**
	 * 
	 */
	private void plotStatistics() {
		
		statisticsWindow = new StatisticsVisualizer(getTitleString());
		String configName = (printName()+"-"+randomGenerator.getSeed()).replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");

		for(int i = 0; i < statsAggregators[0].size(); i++){
			ArrayList[] array = new ArrayList[getIntegerParameter(RUN_COUNT)];
			for(int run = 0; run < statsAggregators.length; run++){
				StatisticsAggregator aggregator = statsAggregators[run].get(i);
				array[run] = aggregator.getStatistics();
			}
			statisticsWindow.addDataSeries(array, statsAggregators[0].get(i).getTitle(), statsAggregators[0].get(i).getTitle(), configName, false);
	
			//TODO remove temp code to add atleast a single density plot
			if(i ==0){
				statisticsWindow.addDataSeries(array, statsAggregators[0].get(i).getTitle(), statsAggregators[0].get(i).getTitle(), configName, true);
			}
		}
		
		statisticsWindow.display();
	}
	
	private String getTitleString(){
		return "[Seed: " + randomGenerator.getSeed() + "   " + printName() + "]";
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
	
	public String printName(){
		return "" + getParameter(AGENT_TYPE).getNodeConfiguration().getParameter(NodeConfiguration.NODE_TYPE).getSelectedValue() + " " + "gen_" + getIntegerParameter(GENERATION_COUNT) + "run_" + getIntegerParameter(RUN_COUNT) + "pop_" + getIntegerParameter(POPULATION_SIZE) + "crit_" + getIntegerParameter(CRITICAL_PERIOD);
	}
	
	@Override
	public BasicConfigurationPanel getConfigurationPanel(){
		BasicConfigurationPanel ret = super.getConfigurationPanel();
		ret.setBorder(new TitledBorder("Simulation Configuration"));
		return ret;
	}

	@Override
	public void stopRequest() {
		continueSimulation = false;
	}

}		
		
