package statisticsVisualizer;

import java.util.ArrayList;

public class ChartConfiguration {

	public static enum ChartType {LINE_CHART, HISTOGRAM, SCATTER_PLOT, AREA_CHART};
	
	private ArrayList<ConfigurationParameterChangedListener> listeners = new ArrayList<ConfigurationParameterChangedListener>();
	
	protected boolean average; //TODO getters and setters
	protected boolean density;
	
	private String configName = "Unnamed";
	
	private String title = "Untitled";
	private String xLabel = "X Label";
	private String yLabel = "Y Label";

	// a negative value means no limit
	private double histogramYMax = -1;
	private double histogramYMin = -1; 
	private double histogramXMax = 12;
	private double histogramXMin = -1;
	
	private int numberOfHistogramBins = 200;
	
	//Construct a configuration with the default setting values
	public ChartConfiguration(){
	}
	
	public ChartConfiguration(
			String title, 
			String xLabel, 
			String yLabel,
			String configName,
			boolean average, 
			boolean density
			) {
		
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.configName = configName;
		this.average = average;
		this.density = density;
		
	}
	
	public ChartConfiguration clone(){
		
		return new ChartConfiguration(
				title,
				xLabel,
				yLabel,
				configName,
				average,
				density
				);
		
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
		notifyConfigurationParameterChangeListeners();
	}
	
	public String getxLabel() {
		return xLabel;
	}

	public void setxLabel(String xLabel) {
		this.xLabel = xLabel;
		notifyConfigurationParameterChangeListeners();
	}

	public String getyLabel() {
		return yLabel;
	}

	public void setyLabel(String yLabel) {
		this.yLabel = yLabel;
		notifyConfigurationParameterChangeListeners();
	}
	
	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
		notifyConfigurationParameterChangeListeners();
	}
	
	public double getHistogramYMax() {
		return histogramYMax;
	}

	public void setHistogramYMax(double histogramYMax) {
		this.histogramYMax = histogramYMax;
		notifyConfigurationParameterChangeListeners();
	}

	public double getHistogramYMin() {
		return histogramYMin;
	}

	public void setHistogramYMin(double histogramYMin) {
		this.histogramYMin = histogramYMin;
		notifyConfigurationParameterChangeListeners();
	}

	public double getHistogramXMax() {
		return histogramXMax;
	}

	public void setHistogramXMax(double histogramXMax) {
		this.histogramXMax = histogramXMax;
		notifyConfigurationParameterChangeListeners();
	}

	public double getHistogramXMin() {
		return histogramXMin;
	}

	public void setHistogramXMin(double histogramXMin) {
		this.histogramXMin = histogramXMin;
		notifyConfigurationParameterChangeListeners();
	}
	
	public int getNumberOfHistogramBins() {
		return numberOfHistogramBins;
	}

	public void setNumberOfHistogramBins(int numberOfHistogramBins) {
		this.numberOfHistogramBins = numberOfHistogramBins;
		notifyConfigurationParameterChangeListeners();
	}

	public void registerParameterChangeListener(ConfigurationParameterChangedListener listener){
		listeners.add(listener);
	} 
	
	protected void notifyConfigurationParameterChangeListeners(){
		for(ConfigurationParameterChangedListener listener : listeners){
			listener.configurationParameterChanged();
		}
	}
	
}