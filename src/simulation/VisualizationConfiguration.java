package simulation;

import javax.swing.border.TitledBorder;

import AutoConfiguration.BasicConfigurable;
import AutoConfiguration.BasicConfigurationPanel;
import AutoConfiguration.ConfigurationParameter;

public class VisualizationConfiguration extends BasicConfigurable {
	
	public static final String PRINT_GENERATION_COUNT = "Print generation count?";
	public static final String PRINT_EACH_X_GENERATIONS = "Print each X generations";
	public static final String PAUSE_AFTER_VISUALIZATION = "Pause after visualization";
	public static final String ENABLE_TIMESERIES_VISUALIAZATION = "Enable continuous visualiazation?";
	public static final String VISUALIZATION_INTERVAL = "Visualization interval";
	
	{
		setDefaultParameter(PRINT_GENERATION_COUNT, new ConfigurationParameter(true));
		setDefaultParameter(PRINT_EACH_X_GENERATIONS, new ConfigurationParameter(1000));
		setDefaultParameter(ENABLE_TIMESERIES_VISUALIAZATION, new ConfigurationParameter(false));
		setDefaultParameter(VISUALIZATION_INTERVAL, new ConfigurationParameter(1));
		setDefaultParameter(PAUSE_AFTER_VISUALIZATION, new ConfigurationParameter(1));
	}
	
	public VisualizationConfiguration(){
	}
	
	public VisualizationConfiguration(BasicConfigurable baseConfig){
		super(baseConfig);	
	}
	
	@Override
	public BasicConfigurationPanel getConfigurationPanel(){
		BasicConfigurationPanel ret = super.getConfigurationPanel();
		ret.setBorder(new TitledBorder("Visualization"));
		return ret;
	}
}
