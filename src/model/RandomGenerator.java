package model;

import java.util.Random;

/**
 * Singleton wrapper for Random. //TODO no longer singleton, is this needed?
 * 
 * @author Luke McCrohon
 */
public class RandomGenerator {
	
	private Random random;
	private long randomSeed;
	
	public RandomGenerator(){
		this(System.currentTimeMillis());
	}
	
	public RandomGenerator(long seed){
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
}
