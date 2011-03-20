package model;

import java.util.ArrayList;

public class SynonymAgent extends AbstractAgent {

	public static final int DEFAULT_MEMEORY_SIZE = 20;
	
	private static final double INVENTION_PROBABILITY = 0.01;
	
	private Utterance[] memory;
	private int memoryPointer = 0;
	private ArrayList<Utterance>[] meanings; 
	
	@SuppressWarnings("unchecked")
	public SynonymAgent(int id, int memorySize){
		super(id);
		memory = new Utterance[memorySize];
		meanings = new ArrayList[NUMBER_OF_MEANINGS];
	}
	
	@SuppressWarnings("unchecked")
	public SynonymAgent(SynonymAgent parent1, SynonymAgent parent2, int id) {
		super(id);
		
		int memorySize = parent1.memory.length + Math.random() > 0.5?1:-1;
		memory = new Utterance[memorySize];
		meanings = new ArrayList[NUMBER_OF_MEANINGS];
	}

	public void learnUtterance(Utterance word){
		
		if(word==null){
			return;
		}
		
		ArrayList<Utterance> synonyms = meanings[word.meaning];
		
		if(synonyms == null){//If no words with same meaning and space left, add the new word
			if(memoryPointer < memory.length){
				synonyms = new ArrayList<Utterance>();
				synonyms.add(word);
				meanings[word.meaning] = synonyms;
				memory[memoryPointer] = word;
				memoryPointer++;
			}
		}else if(!synonyms.contains(word)){//If words with same meaning, but not that one and there is space left, add it.
			
			if(memoryPointer < memory.length){
				synonyms.add(word);
				memory[memoryPointer] = word;
				memoryPointer++;
			}
		}
		
		//TODO move recently seen words to front.
		
	}
	
	private Utterance getUtteranceForMeaning(int meaning){
		
		if(meanings[meaning] == null){
			
			//In case of no word known, invent something maybe
			if(memoryPointer < memory.length && Math.random() < INVENTION_PROBABILITY){
				Utterance invention = new Utterance(meaning, forms++);
				ArrayList<Utterance> words = new ArrayList<Utterance>();
				words.add(invention);
				meanings[meaning] = words;
				memory[memoryPointer] = invention;
				memoryPointer++;
				return invention;
			}
			
			return null;//no new invention, return an empty utterance
		}else{
			ArrayList<Utterance> words = meanings[meaning];
			
			//Change this later, maybe make first one more likely to be used etc
			int index = (int)(Math.random() * words.size());
			
			return words.get(index);
		}
	}
	
	public int getWordsForMeaning(int meaning){
		return meanings[meaning]!=null?meanings[meaning].size():0;
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
		
		for(int i = 0; i < meanings.length; i++){
			
			ArrayList<Utterance> words = meanings[i];
			
			System.out.print("meaning " + i +" has " + (words==null?"0":words.size()) + " forms. ");
			
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
		
		for(ArrayList<Utterance> forms : meanings){
			
			if(forms==null){
				System.out.print("0\t");
			}else{
				System.out.print(forms.size()+"\t");
			}
			
		}
		
		System.out.println();
		
	}
	
	private static int forms = 0; //Unique static word form identifier, TODO should be replaced with a static get form method
	
	/*public void teach(SynonymAgent listener, SynonymAgent speaker){
		
		int meaning = Simulation.getMeaning();
		Utterance utterance = speaker.getUtteranceForMeaning(meaning);	
		listener.learnUtterance(utterance);
		
	}*/
	
	private static enum MeaningDistribution {LINEAR,SQUARED,LOG};
	private static MeaningDistribution selectionMethod = MeaningDistribution.LINEAR;
	private static final int GRADIENT = 1;
	@Override
	public Utterance getRandomUtterance() {
		
		double rand = Math.random();
		
		if(selectionMethod == MeaningDistribution.SQUARED){
			return getUtteranceForMeaning((int) (rand*rand*NUMBER_OF_MEANINGS));
		}else if (selectionMethod == MeaningDistribution.LINEAR){
			return getUtteranceForMeaning((int) (rand*NUMBER_OF_MEANINGS));
		}else if (selectionMethod == MeaningDistribution.LOG){
			return getUtteranceForMeaning((int) Math.log(rand)*NUMBER_OF_MEANINGS);	
		}else{
			System.out.println("Unhandled MeaningDistribution");
			return null;//this is a failure case
		}
	}
	
	
	public static void main(String[] args){
		
		SynonymAgent test = new SynonymAgent(1,10);
		
		Utterance wordOne = new Utterance(5, 1);
		Utterance wordTwo = new Utterance(5, 2);
		Utterance wordThree = new Utterance(4, 3);
		
		test.learnUtterance(wordOne);
		test.learnUtterance(wordTwo);
		test.learnUtterance(wordThree);
		
		test.printAgent();

	}

	@Override
	public double geneGrammarMatch() {
		return 0;
	}

	@Override
	public int learningIntensity() {
		return 0;
	}

	@Override
	public void communicate(Agent partner) {
		// TODO Auto-generated method stub
		
	}
	
}
