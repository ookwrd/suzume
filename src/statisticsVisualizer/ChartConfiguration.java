package statisticsVisualizer;

public class ChartConfiguration {

	public static enum ChartType {LINE_CHART, HISTOGRAM, SCATTER_PLOT, AREA_CHART};
	
	protected boolean average;
	protected boolean density;
	
	protected String title = "Untitled";
	protected String xLabel = "X Label";
	protected String yLabel = "Y Label";
	
	public ChartConfiguration(){
		
	}
	
	public ChartConfiguration(
			String title, 
			String xLabel, 
			String yLabel, 
			boolean average, 
			boolean density
			) {
		
		this.title = title;
		this.xLabel = xLabel;
		this.yLabel = yLabel;
		this.average = average;
		this.density = density;
		
	}
	
	public ChartConfiguration clone(){
		
		return new ChartConfiguration(
				title,
				xLabel,
				yLabel,
				average,
				density
				);
		
	}
	
}