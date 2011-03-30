package model;

import java.util.Random;

/**
 * Singleton wrapper for Random.
 * 
 * @author Luke McCrohon
 */
public class RandomGenerator {
	
	private RandomGenerator randomGenerator;
	private Random random;
	private long randomSeed;
	
	private RandomGenerator(long seed){
		random = new Random(seed);
		randomSeed = seed;
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
	
	//TODO turn these into standard constructors.
	
	public static RandomGenerator getGenerator(){
		
		return new RandomGenerator(System.currentTimeMillis());
		
	}
	
	public static RandomGenerator getGenerator(long seed){
		
		return new RandomGenerator(seed);
	}
}
