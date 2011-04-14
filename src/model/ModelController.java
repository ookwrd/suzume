package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import tools.Clustering;
import tools.KmeansClustering;
import tools.Pair;
import tools.StateTransitionVisualizer;
import tools.Statistics;

import Agents.Agent;
import Agents.AgentFactory;
import Launcher.Launcher;

public class ModelController implements Runnable {
	
	private static final int NUMBER_DENSITY_BUCKETS = 100;
	
	private static final double DEFAULT_DENSITY_GRANULARITY = 0.01;//should be set lower than 0.01 //TODO refactor

	private static final int DEFAULT_STATE_TRANSITION_STEP = 1;

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
	
	//Visualization
	private StepwiseVisualizer visualizer;
	private ModelStatistics statisticsWindow;

	//Progress counters
	private int currentGeneration = 0;
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
		findMarkov();
	}
	
	/**
	 * Compute the Markov probabilistic model for the data
	 */
	private void findMarkov() {
		ArrayList<Double>[] data = Statistics.trimArrayLists(geneGrammarMatches,200,geneGrammarMatches[0].size());
		Hashtable<Double, Integer> clusteringRes = cluster(data);
		//System.out.print("OH data size "+data[0].size());
		//System.out.println("OH clustering size : "+clustering.size());
		// render diagram
		// stateTransitionsNormalized(stateSequence, DEFAULT_STATE_TRANSITION_STEP);
		stateTransitionsNormalized(data, clusteringRes, 1);
		stateTransitionsNormalized(data, clusteringRes, 10);
		stateTransitionsNormalized(data, clusteringRes, 50);
		stateTransitionsNormalized(data, clusteringRes, 100);
		stateTransitionsNormalized(data, clusteringRes, 200);
		
		// plot the sequence 
		/*ArrayList<Double>[] clusteringVal = new ArrayList[config.numberRuns];
		
		for(int i = 0; i < data.length; i++){
			clusteringVal[i] = new ArrayList<Double>();
			for(int j =0; j < data[0].size(); j++) {
				clusteringVal[i].add((double) clusteringRes.get(data[i].get(j)));
			}
		}
		statisticsWindow.plot(clusteringVal, "Clustering 200-trimmed Gene Grammar Matches", "State", "", "States sequence");
		*/
		//StateTransitionVisualizer.render(stateTransitionsNormalized(stateSequence, DEFAULT_STATE_TRANSITION_STEP));
		
	}


	/**
	 * Normalize the probability matrix so that every column sums up to 1
	 * @param i 
	 * @param clustering 
	 * 
	 * @param matrix
	 * @return
	 */
	private double[][] stateTransitionsNormalized(ArrayList<Double>[] ar, Hashtable<Double, Integer> clustering, int step) {
		Short[] stateSequence = stateSequence(ar, clustering, false);
		double[][] matrix = stateTransitions(stateSequence, step);
		double[][] result = new double[matrix.length][matrix[0].length]; 
		for (int i = 0; i < matrix.length; i++) {
			int colSum = 0;
			for (int j = 0; j < matrix[i].length; j++) {
				colSum += matrix[i][j];
			}
			for (int j = 0; j < matrix[i].length; j++) {
				if (colSum == 0) colSum = 1;
				result[i][j] = matrix[i][j]/colSum;
			}
		}
		DecimalFormat df = new DecimalFormat("########.00"); 
		System.out.println("\nTransition probabilities (single step: "+step+", X:gap)");
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				System.out.print("("+(i==0?"X":i)+"->"+(j==0?"X":j)+"): "+df.format(result[i][j])+" ");
			}
			System.out.println();
		}
		return result;
	}

	/**
	 * Compute the state transition probability matrix 
	 * 
	 * @param stateSequence
	 * @return
	 */
	private double[][] stateTransitions(Short[] stateSequence, int step) {
		double[][] transitions = new double[Clustering.MAX_NUM_CLUSTERS][Clustering.MAX_NUM_CLUSTERS];
		/*for (int i=0; i<MAX_NUM_STATES; i++) {
			for (int j=0; j<MAX_NUM_STATES; j++) {
				transitions[i][j] = 0.0;
			}
		}*/
		int from, to = 0;
		if(stateSequence.length>1)
			from=stateSequence[0];
		else { 
			System.out.println("Empty state sequence !");
			return transitions;
		}
		int fromMax = 0;
		int toMax = 0;
		if (step<1) step = 1;
		int start = 1;
		System.out.print("\nState transitions: "+from);
		for (int i = start; i < stateSequence.length; i+=step) {
			from = to;
			to = stateSequence[i];
			System.out.print(", "+to);
			if ((i-step)/config.generationCount == (i)/config.generationCount) // if did not reach another run
			{	
				transitions[from][to] +=1;
			}
			//else System.out.println("reached another run (from "+(i-step)+" to "+i+" in the state sequence)");
			if (from > fromMax) fromMax = from; //TODO get it directly from the clustering
			if (to > toMax) toMax = to;
		}
		System.out.println();
		fromMax++; // matrix length
		toMax++; // matrix width
		
		//return a smaller matrix
		double[][] result = new double[fromMax][toMax];
		
		//System.out.println("\nTransition count (single step: "+step+", X:gap)");
		for (int i = 0; i < fromMax; i++) {
			for (int j = 0; j < toMax; j++) {
				result[i][j] = transitions[i][j];
				//System.out.print("("+(i==0?"X":i)+"->"+(j==0?"X":j)+"):"+transitions[i][j]+" ");
			}
			//System.out.println();
		}
		return result;
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
		
		statisticsWindow = new ModelStatistics(getTitleString());
		String printName = (config.printName()+"-"+randomGenerator.getSeed()).replaceAll("  "," ").replaceAll("  "," ").replaceAll(":", "").replaceAll(" ", "-");

		statisticsWindow.plot(geneGrammarMatches, "Gene Grammar Matches", "Occurrences", "Gene Grammar Matches", printName);
		statisticsWindow.plot(Statistics.calculateDensity(Statistics.aggregateArrayLists(geneGrammarMatches)), "Density (Gene Grammar Matches)", "Occurrences", "Gene Grammar Matches", printName);
		statisticsWindow.plot(Statistics.calculateDensity(Statistics.aggregateArrayLists(Statistics.trimArrayLists(geneGrammarMatches,200,geneGrammarMatches[0].size()))), "200 onwards...Density (Gene Grammar Matches)", "Occurrences", "Gene Grammar Matches", printName);
		statisticsWindow.plot(learningIntensities, "Learning Intensity", "Occurrences", "Learning Intensity", printName);
		statisticsWindow.plot(numberNulls, "Number of Nulls", "Occurrences", "Number of Nulls", printName);
		statisticsWindow.plot(totalFitnesses, "Fitnesses", "Occurrences", "Fitnesses", printName);
		statisticsWindow.plot(totalNumberGenotypes, "Number of Genotypes", "Occurrences", "Number of Genotypes", printName);
		statisticsWindow.plot(totalNumberPhenotypes, "Number of Phenotypes", "Occurrences", "Number of Phenotypes", printName);
		statisticsWindow.plot(Statistics.trimArrayLists(totalNumberGenotypes, 200, totalNumberGenotypes[0].size()), "Number of Genotypes (trim)", "Occurrences", "Number of Genotypes", printName);
		statisticsWindow.plot(Statistics.trimArrayLists(totalNumberPhenotypes, 200, totalNumberPhenotypes[0].size()), "Number of Phenotypes (trim)", "Occurrences", "Number of Phenotypes", printName);
		
		statisticsWindow.display();
	}
	
	private String getTitleString(){
		return "[Seed: " + randomGenerator.getSeed() + "   " + config + "]";
	}
	
	public static ArrayList<Pair<Double, Integer>> smooth(ArrayList<Pair<Double, Integer>> series, int maxWindow){
		
		ArrayList<Pair<Double, Integer>> retVal = new ArrayList<Pair<Double,Integer>>();
		Collections.sort(series);
		
		for(int i = maxWindow; i < series.size()-maxWindow; i++){

			double count = 0;

			for(int window = 1; window <= maxWindow; window++){
				for(int j = 0; j < window; j++){
					count += series.get(i-j).second * (window - j);
					count += series.get(i+j).second * (window - j);
				}
				
				count += series.get(i).second;
			}
			retVal.add(new Pair<Double, Integer>(series.get(i).first, (int)(count/*/(window*2+1)*/)));
		}
		
		return retVal;
		
	}

    /**
     * Cluster points from an array and computes the standard deviation (sigma)
     * 
     * @param an array of values to cluster, each entry represents one
     *            occurrence of the value
     * @param i number of clusters
     * @return std deviation
     */
    /*public static double clusterSigma(ArrayList<Double>[] array, int i) {
            //Hashtable<Double, Integer> clusters = null; //null
            //for(int i=2; i<=10; i++) {
            
            double sigma = KmeansClustering.sigmaKmeans(array, i);
            System.out.println("total deviation = ["+Math.round(sigma*1000000.0)/10000.0+"%]");
            
            //}
            return sigma;
    }*/
    
    /**
     * Cluster points from an array
     * 
     * @param an array of values to cluster, each entry represents one
     *            occurrence of the value
     * @return a hashtable representing the mapping between each point (double
     *         key) and its cluster index (integer value)
     */
    public static Hashtable<Double, Integer> cluster(ArrayList<Double>[] array) {
            SimpleClustering clustering = new SimpleClustering(SimpleClustering.DEFAULT_CENTERS); // using a very simple version of clustering TODO to be improved later
            Hashtable<Double, Integer> clusters = clustering.cluster(array);
            return clusters;
    }
    
    /** 
     * Extract non-state-repeating transitions (deprecated)
     * 
     * @param clustering
     * @return
     */
    /*public static ArrayList<Integer> extractStateTransitionsNoRepeat(ArrayList<Double>[] a, Hashtable<Double, Integer> clustering) {
    	ArrayList<Integer> stateSequence = new ArrayList<Integer>();
		
		Enumeration<Double> e = clustering.keys();
		int previousState = 0;
		int count = 0;
		int stableCount = 0;
		int maxStableCount = 0;
		while (e.hasMoreElements()) {
			double elt = e.nextElement();
			int tmp = clustering.get(elt);
			//System.out.print(elt + ":" + tmp + " ");
			count++;
			if (previousState != tmp) {
				stateSequence.add(tmp);
				previousState = tmp;
				stableCount = 0;
			}
			else { 
				stableCount++;
				if (maxStableCount<stableCount) maxStableCount=stableCount;
			}
			

		} //TODO : virer les states qui n'ont pas lieu, quand on passe au run suivant
		
    	System.out.print("\nState transitions: ");
    	for (int i = 0; i < stateSequence.size(); i++) {
			System.out.print((int) stateSequence.get(i)+"->");
		}
    	//System.out.print("\n#data = "+count+" ");
    	//System.out.println("#successive states = "+stateSequence.size());
    	//System.out.println("#max successive stable transitions = "+maxStableCount);
		return stateSequence;
    }*/
    
    /**
     * Extract state transitions
     * 
     * @param data array of the original data
     * @param clustering result of the classification (values-to-classes table)
     * @param noRepeat if true, don't take any repeated state into account
     * @return state sequence
     */
    public static Short[] stateSequence(ArrayList<Double>[] data, Hashtable<Double, Integer> clustering, boolean noRepeat) {
    	ArrayList<Integer> stateSequence = new ArrayList<Integer>(0);
		
		short previousState = 0;
		for (int i = 0; i < data.length; i += 1) { // for every run
			for (int j = 0; j < data[0].size(); j++) { // for every generation
				double elt = data[i].get(j);
				int tmp = clustering.get(elt);
				//System.out.print(elt + ":" + tmp + " ");
				previousState = (short) tmp;

				if (!noRepeat)
					stateSequence.add(tmp); // if repeating states : add
											// anyway

				if (previousState != tmp) {
					if (noRepeat)
						stateSequence.add(tmp); // if not repeating states :
												// add
												// only if different from
												// previous state
				}
			}
		}

    	/*System.out.print("\nState transitions ("+stateSequence.size()+" in total) : ");
    	for (int i = 0; i < stateSequence.size(); i++) {
			System.out.print(stateSequence.get(i)+"->"); //showing every single transition
		}*/
    	//System.out.print("\n#data = "+count+" ");
    	System.out.println("Number of states: "+stateSequence.size());
    	
    	int clusterNum = stateSequence.size();
    	Short[] states = new Short[clusterNum];
    	for (int i = 0; i < clusterNum; i++) {
			states[i] = stateSequence.get(i).shortValue();
		}
		return states;
    }

    
	public static void main(String[] args) {
		//long heapSize = Runtime.getRuntime().totalMemory();
		//System.out.println("Java heap size = "+ heapSize);
	    new Launcher();
	}

}		
		
