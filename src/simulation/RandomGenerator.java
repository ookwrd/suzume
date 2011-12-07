package simulation;

import java.util.Random;

@SuppressWarnings("serial")
public class RandomGenerator extends Random {
	
	private long randomSeed;
	
	public RandomGenerator(long randomSeed){
		super(randomSeed);
		this.randomSeed = randomSeed;
	}
	
	public long getSeed(){
		return randomSeed;
	}
}
