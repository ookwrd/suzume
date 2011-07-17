package simulation;

import java.util.Random;

import AutoConfiguration.BasicConfigurable;
import AutoConfiguration.ConfigurationParameter;


public class RandomGenerator extends BasicConfigurable {
	
	private static final String KEY_SET = "Use Current time as seed";
	private static final String SEED = "Seed";
	
	{
		defaultParameters.put(KEY_SET, new ConfigurationParameter(true));
		defaultParameters.put(SEED, new ConfigurationParameter(new Long(0)));
	}
	
	private Random random;
	private long randomSeed;
	
	public RandomGenerator(){};
	
	public RandomGenerator(BasicConfigurable config){
		if(!config.getParameter(KEY_SET).getBoolean()){
			randomSeed = config.getParameter(SEED).getLong();
		} else {
			randomSeed = System.currentTimeMillis();
		}
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
