package model;

import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.text.StyledEditorKit.ForegroundAction;

import tools.ClusteringTool;
import tools.StateTransitionVisualizer;

import Agents.Agent;
import Agents.AgentFactory;
import Launcher.Launcher;

public class ModelController implements Runnable {

	private static final double DEFAULT_DENSITY_GRANULARITY = 0.001;//should be set lower than 0.01 //TODO refactor

	private static final double MAX_DEVIATION_DECREASE = 1;

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
		ArrayList<Double>[] a = trimArrayLists(geneGrammarMatches,200,geneGrammarMatches[0].size());
		Hashtable<Double, Integer> clustering = cluster(a);
		Integer[] stateSequence = (Integer[]) stateTransitions(a, clustering, false);
		
		//render diagram
		stateTransitionsNormalized(stateSequence);
		//StateTransitionVisualizer.render(stateTransitionsNormalized(stateSequence));
	}
	
	/**
	 * Compute the state transition probability matrix 
	 * 
	 * @param stateSequence
	 * @return
	 */
	private double[][] stateTransitions(Integer[] stateSequence) {
		double[][] transitions = new double[stateSequence.length][stateSequence.length];
		int from, to = 0;
		if(stateSequence.length>1)
			from=stateSequence[0];
		else return transitions;
		int fromMax = 0;
		int toMax = 0;
		for (int i = 1; i < stateSequence.length; i++) {
			from = to;
			to = stateSequence[i];
			transitions[from][to] +=1;
			if (from > fromMax) fromMax = from; //TODO get it directly from the clustering
			if (to > toMax) toMax = to;
		}
		fromMax++; 
		toMax++;
		
		//return a smaller matrix
		double[][] result = new double[fromMax][toMax];
		
		System.out.println("transitions: ");
		for (int i = 0; i < fromMax; i++) {
			for (int j = 0; j < toMax; j++) {
				result[i][j] = transitions[i][j];
				System.out.print("("+(i+1)+"->"+(j+1)+"): "+transitions[i][j]+" ");
			}
			System.out.println();
		}
		return result;
	}
	
	/**
	 * Normalize the probability matrix so that every column sums up to 1
	 * 
	 * @param matrix
	 * @return
	 */
	private double[][] stateTransitionsNormalized(Integer[] stateSequence) {
		double[][] matrix = stateTransitions(stateSequence);
		double[][] result = new double[matrix.length][matrix[0].length]; 
		for (int i = 0; i < matrix.length; i++) {
			int colSum = 0;
			for (int j = 0; j < matrix[i].length; j++) {
				colSum += matrix[i][j];
			}
			for (int j = 0; j < matrix[i].length; j++) {
				result[i][j] = matrix[i][j]/colSum;
			}
		}
		DecimalFormat df = new DecimalFormat("########.00"); 
		System.out.println("normalized transitions: ");
		for (int i = 0; i < result.length; i++) {
			for (int j = 0; j < result[0].length; j++) {
				System.out.print("("+(i+1)+"->"+(j+1)+"): "+df.format(result[i][j])+" ");
			}
			System.out.println();
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
			//agent.setFitness(10);//just a test
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
		
		ModelStatistics densityWindow = new ModelStatistics(getTitleString());
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
	
	private String getTitleString(){
		return "[Seed: " + randomGenerator.getSeed() + "   " + config + "]";
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
	

    /**
     * Cluster points from an array and computes the standard deviation (sigma)
     * 
     * @param an array of values to cluster, each entry represents one
     *            occurrence of the value
     * @param i number of clusters
     * @return std deviation
     */
    public static double clusterSigma(ArrayList<Double>[] array, int i) {
            //Hashtable<Double, Integer> clusters = null; //null
            //for(int i=2; i<=10; i++) {
            
            double sigma = ClusteringTool.sigmaKmeans(array, i);
            System.out.println("total deviation = ["+Math.round(sigma*1000000.0)/10000.0+"%]");
            
            //}
            return sigma;
    }
    
    /**
     * Cluster points from an array
     * 
     * @param an array of values to cluster, each entry represents one
     *            occurrence of the value
     * @return a hashtable representing the mapping between each point (double
     *         key) and its cluster index (integer value)
     */
    public static Hashtable<Double, Integer> cluster(ArrayList<Double>[] array) {
                    
            Hashtable<Double, Integer> clusters = ClusteringTool.kmeansLimDevDecrease(array, MAX_DEVIATION_DECREASE);
            return clusters;
    }
    
    /** 
     * Extract non-state-repeating transitions (deprecated)
     * 
     * @param clustering
     * @return
     */
    public static ArrayList<Integer> extractStateTransitionsNoRepeat(ArrayList<Double>[] a, Hashtable<Double, Integer> clustering) {
    	ArrayList<Integer> stateSequence = new ArrayList<Integer>();
		/*
		 * //other method int previousState = 0; for (int i = 0; i < a.length;
		 * i++) { for (int j = 0; j < a[i].size(); j++) { double elt =
		 * a[i].get(i); int tmp = clustering.get(elt);
		 * System.out.print(elt+":"+tmp+" "); if (previousState != tmp) {
		 * stateSequence.add(tmp); previousState = tmp; } } }
		 */
		Enumeration<Double> e = clustering.keys();
		int previousState = 0;
		int count = 0;
		int stableCount = 0;
		int maxStableCount = 0;
		while (e.hasMoreElements()) {
			double elt = e.nextElement();
			int tmp = clustering.get(elt);
			System.out.print(elt + ":" + tmp + " ");
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
			System.out.print(stateSequence.get(i)+"->");
		}
    	//System.out.print("\n#data = "+count+" ");
    	//System.out.println("#successive states = "+stateSequence.size());
    	//System.out.println("#max successive stable transitions = "+maxStableCount);
		return stateSequence;
    }
    
    /**
     * Extract state transitions
     * 
     * @param data array of the original data
     * @param clustering result of the classification (values-to-classes table)
     * @param noRepeat if true, don't take any repeated state into account
     * @return
     */
    public static Integer[] stateTransitions(ArrayList<Double>[] data, Hashtable<Double, Integer> clustering, boolean noRepeat) {
    	ArrayList<Integer> stateSequence = new ArrayList<Integer>(0);
		
    	//Enumeration<Double> e = clustering.keys();
		int previousState = 0;
		
		for (int i=0; i<data.length; i++) {
			for (int j=0; j<data[0].size(); j++) { 
				double elt = data[i].get(j);
				int tmp = clustering.get(elt);
				System.out.print(elt + ":" + tmp + " ");
				previousState = tmp;
				
				if (j!=0) { // don't add transitions between successive runs
					
					if (!noRepeat)
						stateSequence.add(tmp); // if repeating states : add
												// anyways

					if (previousState != tmp) {
						if (noRepeat)
							stateSequence.add(tmp); // if not repeating states :
													// add
													// only if different from
													// previous state
					}
				}
			}
		}
		
    	System.out.print("\nState transitions ("+stateSequence.size()+" in total) : ");
    	for (int i = 0; i < stateSequence.size(); i++) {
			System.out.print(stateSequence.get(i)+"->");
		}
    	//System.out.print("\n#data = "+count+" ");
    	//System.out.println("#successive states = "+stateSequence.size());
    	
    	int clusterNum = stateSequence.size();
    	Integer[] states = new Integer[clusterNum];
    	for (int i = 0; i < clusterNum; i++) {
			states[i] = stateSequence.get(i);
		}
		return states;
    }

    
	public static void main(String[] args) {
		new Launcher();
	}

}		
		
