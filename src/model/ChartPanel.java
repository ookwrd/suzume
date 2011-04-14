package model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import tools.Pair;
import tools.Statistics;

@SuppressWarnings("serial")
public class ChartPanel extends JPanel {

	private static final Dimension SAVE_DIMENSION = new Dimension(1000, 600);
	private static final Dimension THUMBNAIL_DIMENSION = new Dimension(500,300);
	
	public static enum ChartType {LINE_CHART, HISTOGRAM, SCATTER_PLOT};
	
	private JFreeChart chart;
	private String filename;
	
	public ChartPanel(ArrayList<Pair<Double, Double>> data, ChartType type, String title,
			String yLabel, String xLabel, String printName){
		this(wrapArrayList(data), type, title, yLabel, xLabel, printName);			
	}
	
	public ChartPanel(ArrayList<Pair<Double, Double>>[] data, ChartType type, String title,
			String yLabel, String xLabel, String printName){
		super();
		
		this.filename = title.replaceAll(" ", "") + "-" + printName + ".jpg";
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.chart = createChart(data, type, title, xLabel, yLabel);
		add(createImageJLabel(chart));
		
	}
	
	public void addAdditionalChart(ArrayList<Pair<Double, Double>>[] data, ChartType type, String title,
			String yLabel, String xLabel){
		JFreeChart chart = createChart(data, type, title, xLabel, yLabel);
		add(createImageJLabel(chart));
	}
	
	private JLabel createImageJLabel(JFreeChart chart) {
		
		BufferedImage image = chart.createBufferedImage(
				THUMBNAIL_DIMENSION.width, 
				THUMBNAIL_DIMENSION.height
				);

		JLabel chartLabel = new JLabel();
		chartLabel.setIcon(new ImageIcon(image));

		return chartLabel;
	}
	
	private static <E> ArrayList<E>[] wrapArrayList(ArrayList<E> input){
		@SuppressWarnings("unchecked")
		ArrayList<E>[] wrapper = new ArrayList[1];
		wrapper[0] = input;
		return wrapper;
	}
	
	public JFreeChart getChart() {
		return chart;
	}

	public String getFilename() {
		return filename;
	}

	private JFreeChart createChart(ArrayList<Pair<Double, Double>>[] series, ChartType type, String title, String xLabel, String yLabel){
		
		JFreeChart chart;
		switch (type) {

		case HISTOGRAM:
			chart = ChartFactory.createXYBarChart(title, // Title
					xLabel, // X-Axis label
					false,
					yLabel, // Y-Axis label
					createHistogramDataset(series), // Dataset
					PlotOrientation.VERTICAL, // Plot orientation
					false, // Show legend
					false, // Tooltips
					false); // URL
			XYPlot catPlot = chart.getXYPlot();
			catPlot.setRenderer(new ClusteredXYBarRenderer());
			//((XYBarRenderer)catPlot.getRenderer()).setShadowVisible(false);
			break;
			
		case SCATTER_PLOT:
			
			chart = ChartFactory.createScatterPlot(title, 
					xLabel, 
					yLabel, 
					createXyDataset(series), 
					PlotOrientation.VERTICAL, 
					false, 
					false, 
					false);
			XYPlot plot = (XYPlot)chart.getPlot();
			XYDotRenderer renderer = new XYDotRenderer();
			renderer.setDotWidth(2);
			renderer.setDotHeight(2);
			plot.setRenderer(renderer);
			break;

		case LINE_CHART:
		default:
			chart = ChartFactory.createXYLineChart(title,
					xLabel,
					yLabel,
					createXyDataset(series),
					PlotOrientation.VERTICAL, 
					false, // Show legend
					false, // Tooltips
					false); // URL;
		}

		return chart;

	}
	
	private XYSeriesCollection createXyDataset(ArrayList<Pair<Double, Double>>[] data){
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		int key = 0;
		for(ArrayList<Pair<Double, Double>> series : data){
			dataset.addSeries(createXySeries(series, key++));
		}
		
		return dataset;
	}
	
	private XYSeries createXySeries(ArrayList<Pair<Double, Double>> data, Integer key){
		
		XYSeries series = new XYSeries(key);
		for(Pair<Double, Double> value : data){
			series.add(value.first, value.second);
		}
		
		return series;
	}
	
	private /*HistogramDataset*/ XYSeriesCollection createHistogramDataset(ArrayList<Pair<Double, Double>>[] series){
		
		return createXyDataset(series);
		
		//return null;//TODO
	}
	
	public void saveThumbNailSizeChart(){
		saveChartToFile(THUMBNAIL_DIMENSION);
	}
	
	public void saveFullSizeChart(){
		saveChartToFile(SAVE_DIMENSION);
	}

	public void saveChartToFile(Dimension printSize) {

		cd("/");
		mkdir("/suzume-charts");//TODO extract to configuration file
		try {

			ChartUtilities.saveChartAsJPEG(
					new File("/suzume-charts/" + filename), 
					chart,
					printSize.width, 
					printSize.height
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Change to a directory
	 * 
	 * @param name
	 */
	private void cd(String name) {
		System.setProperty("user.dir", name);
	}
	
	/**
	 * Create a directory
	 * 
	 * @param dir
	 */
	private void mkdir(String dir) {

		try {
			boolean success = (new File(dir)).mkdir();
			if (success) {
				System.out.println("Folder " + dir + " created");
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
}
