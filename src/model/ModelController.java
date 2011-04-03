package model;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import Agents.Agent;
import Agents.AgentFactory;
import Launcher.Launcher;

public class ModelController implements Runnable {
        
        private static final double DEFAULT_DENSITY_GRANULARITY = 0.001;//should be set lower than 0.01

		private ModelConfiguration config;
        
        //statistics
        private ArrayList<Double>[] totalNumberGenotypes;
        private ArrayList<Double>[] totalNumberPhenotypes;
        private ArrayList<Double>[] totalFitnesses;
        private ArrayList<Double>[] learningIntensities;
        private ArrayList<Double>[] geneGrammarMatches;
        private ArrayList<Double>[] numberNulls;
        
        private PopulationModel population;
        
        private int currentGeneration = 0;
        private int currentRun = 0;
        
        private RandomGenerator randomGenerator;

		private ArrayList<Double> globalGeneGrammarMatches;
        
        public ModelController(ModelConfiguration configuration, RandomGenerator randomGenerator){
                this.config = configuration;
                this.randomGenerator = randomGenerator;
                
                resetModel();
                
                totalNumberGenotypes = initializeStatisticsArraylist();
                totalNumberPhenotypes = initializeStatisticsArraylist();
                totalFitnesses = initializeStatisticsArraylist();
                learningIntensities = initializeStatisticsArraylist();
                geneGrammarMatches = initializeStatisticsArraylist();
                numberNulls = initializeStatisticsArraylist();    
                
                
        }
        
        public void resetModel(){

            population = new OriginalPopulationModel(createIntialAgents(), createIntialAgents());
        }
        
        @SuppressWarnings("unchecked")
		private ArrayList<Double>[] initializeStatisticsArraylist(){
        	
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
        
        /**
         * Main method to run the simulation once constructed. 
         */
        public void runSimulation(){
                
                for(currentGeneration = 0; currentGeneration < config.generationCount; currentGeneration++){
                        iterateGeneration();
                        
                        //Print progress information
                        if(currentGeneration % 1000 == 0){
                                System.out.println("Run " + currentRun + " Generation " + currentGeneration);
                        }
                }
                
                if(++currentRun < config.numberRuns){
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
        
        
        @Override
        public void run(){
                runSimulation();
                
                training();
                communication();
                
                plotStatistics();
        }
        
        private ArrayList<Double>[] trimArrayLists(ArrayList<Double>[] arrays, int start, int finish){
        	
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
        private Hashtable<Double, Integer> calculateDensity(ArrayList<Double>[] array) {
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
        
        public static void main2(String[] args){
                
                //Test selection
                ModelController selector = new ModelController(new ModelConfiguration(), new RandomGenerator());

                System.out.println("Using seed:" + selector.randomGenerator.getSeed());
                
                //TODO print other simulation parameters
                System.out.println();
                
                selector.runSimulation();
                
                //Get to a point where the current generation has calculated fitness. TODO swap out with previous generation.
                selector.training();
                selector.communication();
                
                for(Agent agent : selector.population.getCurrentGeneration()){  
                        agent.printAgent();
                        System.out.println();
                }
                
                System.out.println();
                System.out.println("Fitnesses\tlearningResc\tGeneGrammarMatch\tNulls");
                for(int i = 0; i < selector.learningIntensities[0].size(); i++){
                        System.out.println(selector.totalFitnesses[0].get(i) + "\t" + selector.learningIntensities[0].get(i) + "\t" + selector.geneGrammarMatches[0].get(i) + "\t" + selector.numberNulls[0].get(i));
                }
                
                //Plot
                selector.plotStatistics();
                
                for(Agent agent : selector.population.getCurrentGeneration()){
                        System.out.println(agent.getGenotype());
                }
                
                Object double1 = new Double(12342.09);
                
                System.out.println(double1);
        }
        
        public static void main(String[] args) {
			new Launcher();
		}

}