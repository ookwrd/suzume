package model;

import java.util.Random;

/**
 * Singleton wrapper for Random.
 * 
 * @author Luke McCrohon
 */
public class RandomGenerator {

	public static long randomSeed = System.currentTimeMillis();//9111111222111775807L;
	
	public static RandomGenerator randomGenerator;
	private Random random;
	
	private RandomGenerator(){
		random = new Random();
	}
	
	private RandomGenerator(long seed){
		random = new Random(seed);
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
	
	public static RandomGenerator getGenerator(){
		
		if(randomGenerator == null){
			randomGenerator = new RandomGenerator(randomSeed);
		}
		
		return randomGenerator;
	}
}
