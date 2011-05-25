package Agents;

import java.util.ArrayList;

import PopulationModel.Node;

import simulation.RandomGenerator;

public class SynonymAgent extends AbstractAgent {

	public static final int DEFAULT_MEMEORY_SIZE = 10;	
	private static final double INVENTION_PROBABILITY = 0.01;
	
	private static final boolean ALLOW_BIOLOGICAL_MUTATION = false;
	
	private Utterance[] memory;
	private int memoryPointer = 0;
	private ArrayList<Utterance>[] wordsPerMeaning; 
	
	@SuppressWarnings("unchecked")
	public SynonymAgent(NodeConfiguration config, int id, int memorySize){
		//super(config, id);//TODO
		memory = new Utterance[memorySize];
		wordsPerMeaning = new ArrayList[config.getParameter(NUMBER_OF_MEANINGS).getInteger()];
		for(int i = 0; i < wordsPerMeaning.length; i++){
			wordsPerMeaning[i] = new ArrayList<Utterance>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public SynonymAgent(SynonymAgent parent1, SynonymAgent parent2, int id) {
		//super(parent1.getConfiguration(), id);
		
		//Currently asexual reproduction
		int memorySize;
		if(ALLOW_BIOLOGICAL_MUTATION){
			memorySize = parent1.memory.length + ((Math.random() > 0.5)?1:-1);
			if(memorySize < 1){
				memorySize = 1;
			}
		}else{
			memorySize = parent1.memory.length;
		}
		
		memory = new Utterance[memorySize];
		wordsPerMeaning = new ArrayList[config.getParameter(NUMBER_OF_MEANINGS).getInteger()];
		for(int i = 0; i < wordsPerMeaning.length; i++){
			wordsPerMeaning[i] = new ArrayList<Utterance>();
		}
	}

	public void learnUtterance(Utterance word){
		
		if(word.isNull()){
			return;
		}
	
		remember(word);
		
		
		//TODO move recently seen words to front as an option
		
	}

	/**
	 * Adds an utterance to both structures representing the memory.
	 * 
	 * @param toRemember
	 */
	private void remember(Utterance toRemember){
		
		ArrayList<Utterance> synonyms = wordsPerMeaning[toRemember.meaning];
		if(memoryPointer < memory.length && !synonyms.contains(toRemember)){//Have memory space and non-duplicate
			synonyms.add(toRemember);
			memory[memoryPointer] = toRemember;
			memoryPointer++;
		}
	}
	
	/**
	 * Full printout of the agents memory and the number of forms for each meaning.
	 */
	public void printAgent(){
		
		System.out.println("Agent: " + getId() + "\nmemory:");

		for(int i = 0; i < memory.length; i++){			
			if(memory[i] != null){System.out.println(i + " contains " + memory[i].signal);}
			else{System.out.println(i + " is empty");}
		}
		
		System.out.println("Meanings:");
		
		for(int i = 0; i < wordsPerMeaning.length; i++){
			
			ArrayList<Utterance> words = wordsPerMeaning[i];
			
			System.out.print("meaning " + i +" has " + words.size() + " forms. ");
			
			if(words!= null){
				for(Utterance word : words){
					System.out.print(word.signal + " ");
				}
			}
			System.out.println();
			
		}
		
		System.out.println();
		
	}
	
	/**
	 * Prints a single line with the number of forms per meaning
	 */
	public void printForms(){
		
		for(ArrayList<Utterance> forms : wordsPerMeaning){
			if(forms==null){
				System.out.print("0\t");
			}else{
				System.out.print(forms.size()+"\t");
			}
		}
		
		System.out.println();
		
	}
	
	private static int forms = 0; //Unique static word form identifier, TODO should be replaced with a static get form method
	
	@Override
	public void communicate(Node partner) {
		
		Utterance utterance = partner.getRandomUtterance();
		
		//If agent and neighbour agree update fitness.
		if(!utterance.isNull()){
			
			ArrayList<Utterance> synonyms = wordsPerMeaning[utterance.meaning];

			for(Utterance knowledge : synonyms){
				if(knowledge.signal == utterance.signal){
					setFitness(getFitness()+1);
					return;
				}
			}
		}
		
	}
	
	private static enum MeaningDistribution {LINEAR,SQUARED,LOG};
	private static MeaningDistribution selectionMethod = MeaningDistribution.LINEAR;
	@Override
	public Utterance getRandomUtterance() {

		double rand = Math.random();
		
		if(selectionMethod == MeaningDistribution.SQUARED){
			return getUtteranceForMeaning((int) (rand*rand*config.getParameter(NUMBER_OF_MEANINGS).getInteger()));
		}else if (selectionMethod == MeaningDistribution.LINEAR){
			return getUtteranceForMeaning((int) (rand*config.getParameter(NUMBER_OF_MEANINGS).getInteger()));
		}else if (selectionMethod == MeaningDistribution.LOG){
			return getUtteranceForMeaning((int) Math.log(rand)*config.getParameter(NUMBER_OF_MEANINGS).getInteger());	
		}else{
			System.out.println("Unhandled MeaningDistribution");
			return null;//this is a failure case
		}
	}
	
	private Utterance getUtteranceForMeaning(int meaning){
		
		if(wordsPerMeaning[meaning].size() == 0){
			
			//In case of no word known, invent something maybe
			if(memoryPointer < memory.length && Math.random() < INVENTION_PROBABILITY){
				Utterance invention = new Utterance(meaning, forms++);
				ArrayList<Utterance> words = new ArrayList<Utterance>();
				words.add(invention);
				wordsPerMeaning[meaning] = words;
				memory[memoryPointer] = invention;
				memoryPointer++;
				return invention;
			}
			
			return new Utterance(meaning, Utterance.SIGNAL_NULL_VALUE);//no new invention, return an empty utterance
		}else{
			ArrayList<Utterance> words = wordsPerMeaning[meaning];
			
			//TODO Change this later, maybe make first one more likely to be used etc
			int index = (int)(Math.random() * words.size());
			
			return words.get(index);
		}
	}
	
	@Override
	public double geneGrammarMatch() {
		return memory.length; //TODO
	}

	@Override
	public int learningIntensity() {
		return 0; //TODO
	}
	
	@Override
	public int numberOfNulls(){
		return 0; //TODO
	}

	@Override
	public ArrayList getGenotype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeAgent(Node parentA, Node parentB, int id,
			RandomGenerator randomGenerator) {
		// TODO Auto-generated method stub
		
	}
	
}
