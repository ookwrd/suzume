package simulation;

import java.util.Random;

public class RandomGenerator {
	
	private Random random;
	private long randomSeed;
	
	public RandomGenerator(long randomSeed){
		this.randomSeed = randomSeed;
		random = new Random(randomSeed);
	}
	
	public double random(){
		return random.nextDouble();
	}
	
	public int randomInt(int range){
		return random.nextInt(range);
	}
	
	public boolean randomBoolean(){
		return random.nextBoolean();
	}
	
	public long getSeed(){
		return randomSeed;
	}
}
