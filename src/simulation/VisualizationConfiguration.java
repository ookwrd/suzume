package simulation;

import AutoConfiguration.BasicConfiguration;
import AutoConfiguration.ConfigurationParameter;

public class VisualizationConfiguration extends BasicConfiguration {
	
	public static final String PRINT_GENERATION_COUNT = "Print generation count?";
	public static final String PRINT_EACH_X_GENERATIONS = "Print each X generations";
	public static final String PAUSE_AFTER_VISUALIZATION = "Pause after visualization";
	public static final String ENABLE_TIMESERIES_VISUALIAZATION = "Enable continuous visualiazation?";
	public static final String VISUALIZATION_INTERVAL = "Visualization interval";
	
	{
		defaultParameters.put(PRINT_GENERATION_COUNT, new ConfigurationParameter(true));
		defaultParameters.put(PRINT_EACH_X_GENERATIONS, new ConfigurationParameter(1000));
		defaultParameters.put(ENABLE_TIMESERIES_VISUALIAZATION, new ConfigurationParameter(false));
		defaultParameters.put(VISUALIZATION_INTERVAL, new ConfigurationParameter(1));
		defaultParameters.put(PAUSE_AFTER_VISUALIZATION, new ConfigurationParameter(1));
	}
	
	public VisualizationConfiguration(){}
	
	public VisualizationConfiguration(BasicConfiguration baseConfig){
		super(baseConfig.parameters);
	}
}
