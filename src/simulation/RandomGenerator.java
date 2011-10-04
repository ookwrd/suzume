package simulation;

import java.util.Random;

import javax.swing.border.TitledBorder;

import auto_configuration.BasicConfigurable;
import auto_configuration.ConfigurationPanel;
import auto_configuration.ConfigurationParameter;



public class RandomGenerator extends BasicConfigurable {
	
	private static final String KEY_SET = "Use Current time as seed";
	private static final String SEED = "Seed";
	
	{
		setDefaultParameter(KEY_SET, new ConfigurationParameter(true));
		setDefaultParameter(SEED, new ConfigurationParameter(new Long(0)));
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
	
	@Override
	public ConfigurationPanel getConfigurationPanel(){
		ConfigurationPanel ret = super.getConfigurationPanel();
		ret.setBorder(new TitledBorder("Random Number Generator"));
		return ret;
	}
}
