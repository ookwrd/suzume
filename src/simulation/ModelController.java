package simulation;

import java.sql.Time;
import java.util.ArrayList;

import nodes.AbstractNode;
import nodes.Node;
import nodes.NodeFactory;
import nodes.AbstractNode.NodeType;
import nodes.Agents.Agent;
import nodes.Node.StatisticsAggregator;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;

import static nodes.Node.StatisticsCollectionPoint;

import runTimeVisualization.RuntimeVisualizer;
import runTimeVisualization.RuntimeVisualizer.Stoppable;
import simulation.selectionModels.SelectionModel;
import simulation.selectionModels.SelectionModel.SelectionModels;
import statisticsVisualizer.StatisticsVisualizer;
import tools.Pair;

import PopulationModel.AdvancedConfigurableModel;
import PopulationModel.PopulationModel;

public class ModelController extends BasicConfigurable implements Runnable, Stoppable {

	public static final String TOP_LEVEL_MODEL = "Population Model:";
	public static final String GENERATION_COUNT = "Number of Generations:";
	public static final String RUN_COUNT = "Number of Runs";
	
	public static final String SELECTION_MODEL = "Selection model:";
	
	public static final String PRINT_TO_CONSOLE = "Print details to console?";
	public static final String PRINT_EACH_X_GENERATIONS = "Print each X generations";
	
	private static final String KEY_SET = "Use Current time as seed";
	private static final String SEED = "Manual Random Seed";
	
	//Configuration Settings
	private RandomGenerator randomGenerator;

	//Statistics
	private ArrayList<StatisticsAggregator>[] statsAggregators;
	
	//Model
	private PopulationModel population;
	private SelectionModel selectionModel;
	
	//Visualization
	private RuntimeVisualizer visualizer;

	//Progress counters
	private Integer currentGeneration = 0;
	private int currentRun = 1;
	private long simulationStartTime;

	//Stoppable
	private boolean continueSimulation = true;
	
	public ModelController(){
		setDefaultParameter(GENERATION_COUNT, new ConfigurationParameter(5000));
		setDefaultParameter(RUN_COUNT, new ConfigurationParameter(5));
		
		setDefaultParameter(SELECTION_MODEL, new ConfigurationParameter(SelectionModels.values(), SelectionModels.RouletteWheelSelection));

		setDefaultParameter(TOP_LEVEL_MODEL, new ConfigurationParameter(NodeFactory.constructUninitializedNode(AbstractNode.NodeType.AdvancedConfigurableModel).getConfiguration()));
		
		setDefaultParameter(PRINT_TO_CONSOLE, new ConfigurationParameter(true));
		setDefaultParameter(PRINT_EACH_X_GENERATIONS, new ConfigurationParameter(100));
		
		setDefaultParameter(KEY_SET, new ConfigurationParameter(true));
		setDefaultParameter(SEED, new ConfigurationParameter(new Long(0)));
	}
	
	public ModelController(Configurable baseConfig){
		super(baseConfig);
		
		if(!getBooleanParameter(KEY_SET)){
			randomGenerator = new RandomGenerator(getLongParameter(SEED));
		} else {
			randomGenerator = new RandomGenerator(System.currentTimeMillis());
		}
		
		this.selectionModel = SelectionModel.constructSelectionModel((SelectionModels)getParameter(SELECTION_MODEL).getSelectedValue(), randomGenerator);
		
		resetRun();

		statsAggregators = getInitializedStatisticsAggregators();
	}

	private void resetRun(){
		currentGeneration = 0;
		initializePopulation();
	}

	private void initializePopulation(){
		Configurable nodeConfiguration = getParameter(TOP_LEVEL_MODEL).getNodeConfiguration();
		
		AdvancedConfigurableModel node = (AdvancedConfigurableModel)NodeFactory.constructUninitializedNode((NodeType) nodeConfiguration.getParameter(AbstractNode.NODE_TYPE).getSelectedValue());
		node.initialize(nodeConfiguration, NodeFactory.getNewNodeId(), randomGenerator);
		population = node;
	}
	
	private ArrayList<StatisticsAggregator>[] getInitializedStatisticsAggregators(){
		@SuppressWarnings("unchecked")
		ArrayList<StatisticsAggregator>[] arrayLists = new ArrayList[getIntegerParameter(RUN_COUNT)];
		for(int i = 0;i < getIntegerParameter(RUN_COUNT); i++){
			arrayLists[i] = new ArrayList<StatisticsAggregator>();
			for(Object key : getParameter(TOP_LEVEL_MODEL).getNodeConfiguration().getParameter(AdvancedConfigurableModel.SUB_NODE).getNodeConfiguration().getParameter(Node.STATISTICS_TYPE).getSelectedValues()){
				arrayLists[i].add(population.getStatisticsAggregator(key));
			}
		}
		return arrayLists;
	}

	@Override
	public void run(){
		
		startTimer();
		
		if(population.getVisualizationKeys().size() != 0){
			this.visualizer = new RuntimeVisualizer("Suzume: Runtime Visualizer - " +getTitleString(),getIntegerParameter(GENERATION_COUNT), population, this);
		}
		
		runSimulation();
		
		if(getBooleanParameter(PRINT_TO_CONSOLE)){
			System.out.println("Execution completed in: " + longTimeToString(elapsedTime()));
		}
		
		plotStatistics();
		
	}

	/**
	 * Main method to run the simulation.
	 */
	public void runSimulation(){

		//Runs
		while(currentRun <= getIntegerParameter(RUN_COUNT)){
			
			//Generations
			while(continueSimulation && currentGeneration < getIntegerParameter(GENERATION_COUNT)){
	
				iterateGeneration();
	
				//Print progress information
				if(getBooleanParameter(PRINT_TO_CONSOLE) && currentGeneration % getIntegerParameter(PRINT_EACH_X_GENERATIONS) == 0){
					printGenerationCount();
				}
	
				currentGeneration++;
			}
			
			if(getBooleanParameter(PRINT_TO_CONSOLE)){
				printGenerationCount();
				System.out.println();
			}

			currentRun++;

			resetRun();
		}
	}

	/**
	 * Runs a single round of the simulation. 
	 */
	private void iterateGeneration(){
		
		resetPhase();
		gatherStatistics(StatisticsCollectionPoint.PostIntialization);
		
		trainingPhase();
		gatherStatistics(StatisticsCollectionPoint.PostTraining);
		
		inventionPhase();
		gatherStatistics(StatisticsCollectionPoint.PostInvention);
		
		communicationPhase();
		gatherStatistics(StatisticsCollectionPoint.PostCommunication);
		
		finalizeFitnessPhase();
		gatherStatistics(StatisticsCollectionPoint.PostFinalizeFitness);
		
		killingPhase();
		gatherStatistics(StatisticsCollectionPoint.PostKilling);

		visualize();
		endGenerationStatistics();
		
		reproductionPhase();
		gatherStatistics(StatisticsCollectionPoint.PostReproduction);
	
	}

	private void resetPhase(){
		for(Agent agent : population.getBaseAgents()){
			agent.reset();
		}
	}
	
	private void trainingPhase(){
		//for each agent
		for(Agent learner : population.getBaseAgents()){

			//get its teachers
			ArrayList<Node> teachers = population.getPossibleTeachers(learner);

			for(int utterancesSeen = 0; learner.canStillLearn(utterancesSeen); utterancesSeen++){//Get random teacher
				Node teacher = teachers.get(randomGenerator.nextInt(teachers.size()));
				teacher.teach(learner);
			}
		}
	}
	
	private void inventionPhase(){		
		for(Agent agent : population.getBaseAgents()){
			agent.invent();
		}
	}

	private void communicationPhase(){
		for(Agent agent : population.getBaseAgents()){
			ArrayList<Node> neighbouringAgents = population.getPossibleCommunicators(agent);

			//Communicate with all neighbours
			for(Node neighbour : neighbouringAgents){      
				agent.communicate(neighbour);
			}
		}
	}
	
	private void finalizeFitnessPhase(){
		for(Agent agent : population.getBaseAgents()){
			agent.finalizeFitnessValue();
		}
	}
	
	private void killingPhase(){
		for(Agent agent : population.getBaseAgents()){
			agent.killPhase();
		}
	}

	/**
	 * Selection and construction of the new generation.
	 */
	private void reproductionPhase(){
		ArrayList<Node> newGenerationAgents = new ArrayList<Node>();
		for(Agent agent : population.getBaseAgents()){
			if(agent.isAlive()){
				newGenerationAgents.add(agent);
			}else{
				ArrayList<Node> possibleParents = population.getPossibleParents(agent);
				ArrayList<Node> parents = selectionModel.select(possibleParents, 2);
				newGenerationAgents.add(NodeFactory.constructPopulationNode((Agent)parents.get(0), (Agent)parents.get(1), randomGenerator));
			}
		}
		population.setNewSubNodes(newGenerationAgents);
	}

	/**
	 * Gather statistics on the population at the current point in time.
	 */
	private void gatherStatistics(StatisticsCollectionPoint collectionPoint){
		ArrayList<Agent> agents = population.getBaseAgents();
		for(Agent agent : agents){
			for(StatisticsAggregator aggregator : statsAggregators[currentRun-1]){
				aggregator.collectStatistics(collectionPoint, agent);
			}
		}
	}
	
	/**
	 * Update StepwiseVisualizer
	 */
	private void visualize(){
		if(visualizer != null){
			visualizer.update(currentRun, currentGeneration, population);
		}
	}
	
	private void endGenerationStatistics(){
		for(StatisticsAggregator agg : statsAggregators[currentRun-1]){
			agg.endGeneration(currentGeneration, population.getBaseAgents());
		}
		
		if(currentGeneration + 1 >= getIntegerParameter(GENERATION_COUNT)){
			endRunStatistics();
		}
	}
	
	private void endRunStatistics(){
		for(StatisticsAggregator agg : statsAggregators[currentRun-1]){
			agg.endRun(currentRun, population.getBaseAgents());
		}
	}
	
	private void plotStatistics() {
		
		StatisticsVisualizer statisticsWindow = new StatisticsVisualizer("Statistics Visualizer: " +getTitleString());
		String configName = (getPrintName()+"-"+randomGenerator.getSeed()).replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");

		for(int i = 0; i < statsAggregators[0].size(); i++){
			ArrayList<Pair<Double, Double>>[] array = new ArrayList[getIntegerParameter(RUN_COUNT)];
			for(int run = 0; run < statsAggregators.length; run++){
				StatisticsAggregator aggregator = statsAggregators[run].get(i);
				array[run] = aggregator.getStatistics();
			}
			statisticsWindow.addDataSeries(array, statsAggregators[0].get(i).getTitle(), statsAggregators[0].get(i).getTitle(), configName, false);
		}
		
		statisticsWindow.display();
	}
	
	private String getTitleString(){
		return "Start time: " + new Time(simulationStartTime) + " Seed: " + randomGenerator.getSeed() + "   " + getPrintName();
	}
	
	private String getPrintName(){
		return ""  + getParameter(TOP_LEVEL_MODEL).getNodeConfiguration().getParameter(AbstractNode.NODE_TYPE).getSelectedValue() + " " + "gen_" + getIntegerParameter(GENERATION_COUNT) + "run_" + getIntegerParameter(RUN_COUNT);
	}

	private void printGenerationCount(){
		System.out.println("Run " + currentRun + "/" + getIntegerParameter(RUN_COUNT) +"\tGeneration " + currentGeneration + "/"+getIntegerParameter(GENERATION_COUNT)+ "\tElapsed time: " + longTimeToString(elapsedTime()));
	}
	private void startTimer(){
		simulationStartTime = System.currentTimeMillis();
	}
	
	private long elapsedTime(){
		return System.currentTimeMillis() - simulationStartTime;
	}
	
	private String longTimeToString(long period){
		long seconds = period/1000;
		return "Seconds " + seconds;
	}

	@Override
	public void stopRequest() {
		continueSimulation = false;
	}
}		