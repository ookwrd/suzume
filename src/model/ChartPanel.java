package model;

import java.awt.Color;
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
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import tools.Pair;

@SuppressWarnings("serial")
public class ChartPanel extends JPanel {

	private static final Dimension SAVE_DIMENSION = new Dimension(1000, 600);
	private static final Dimension PAPER_DIMENSION = new Dimension(750,450);
	private static final Dimension THUMBNAIL_DIMENSION = new Dimension(500,300);
	
	private static final int NUMBER_OF_BINS = 1000;
	
	public static enum ChartType {LINE_CHART, HISTOGRAM, SCATTER_PLOT, AREA_CHART};
	
	private JFreeChart chart;
	private ArrayList<JFreeChart> extraCharts = new ArrayList<JFreeChart>();
	private String filename;
	
	public ChartPanel(ArrayList<Pair<Double, Double>> data, ChartType type, String title,
			String yLabel, String xLabel, String printName){
		this(wrapArrayList(data), type, title, yLabel, xLabel, printName);			
	}
	
	public ChartPanel(ArrayList<Pair<Double, Double>>[] data, ChartType type, String title,
			String yLabel, String xLabel, String printName){
		super();
		
		this.filename = title.replaceAll(" ", "") + "-" + printName;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.chart = createChart(data, type, title, xLabel, yLabel);
		add(createImageJLabel(chart));
		
	}
	
	//TODO factor creation of trimmed datasets into this class.
	public void addAdditionalChart(ArrayList<Pair<Double, Double>>[] data, ChartType type, String title,
			String yLabel, String xLabel){
		JFreeChart chart = createChart(data, type, title, xLabel, yLabel);
		extraCharts.add(chart);
		add(createImageJLabel(chart));
	}
	
	private JLabel createImageJLabel(JFreeChart chart) {
		
		BufferedImage image = chart.createBufferedImage(
				THUMBNAIL_DIMENSION.width, 
				THUMBNAIL_DIMENSION.height
				);

		JLabel chartLabel = new JLabel();
		ImageIcon icon = new ImageIcon(image);
		chartLabel.setIcon(icon);
		
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
			chart = ChartFactory.createHistogram(title, 
					xLabel,
					yLabel, 
					createHistogramDataset(series), 
					PlotOrientation.VERTICAL,
					false, 
					false, 
					false
					);
			XYPlot catPlot = chart.getXYPlot();
            ((XYBarRenderer)catPlot.getRenderer()).setShadowVisible(false);

			chart.getPlot().setBackgroundPaint(Color.WHITE);
			chart.setBackgroundPaint(Color.WHITE);
			break;
		
		case AREA_CHART:
			chart = ChartFactory.createXYAreaChart(title, // Title
					xLabel, // X-Axis label
					yLabel, // Y-Axis label
					createXyDataset(series), // Dataset
					PlotOrientation.VERTICAL, // Plot orientation
					false, // Show legend
					false, // Tooltips
					false// URL
					);

			chart.getPlot().setBackgroundPaint(Color.WHITE);
			chart.setBackgroundPaint(Color.WHITE);
			break;
			
		case SCATTER_PLOT:
			
			chart = ChartFactory.createScatterPlot(title, 
					xLabel, 
					yLabel, 
					createXyDataset(series), 
					PlotOrientation.VERTICAL, 
					false, 
					false, 
					false
					);
			XYPlot plot = (XYPlot)chart.getPlot();
			XYDotRenderer renderer = new XYDotRenderer();
			renderer.setDotWidth(2);
			renderer.setDotHeight(2);
			plot.setRenderer(renderer);
			chart.getPlot().setBackgroundPaint(Color.WHITE);
			chart.setBackgroundPaint(Color.WHITE);
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
			chart.getPlot().setBackgroundPaint(Color.WHITE);
			chart.setBackgroundPaint(Color.WHITE);
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
	
	private HistogramDataset createHistogramDataset(ArrayList<Pair<Double, Double>>[] series){
		
	
		HistogramDataset dataSet = new HistogramDataset();
		
		dataSet.addSeries(new Double(1), stripIndexValues(series), NUMBER_OF_BINS);
		
		return dataSet;
	}
	
	//TODO factor this into Statistics class
	private double[] stripIndexValues(ArrayList<Pair<Double, Double>>[] series){
		
		ArrayList<Double> dataset = new ArrayList<Double>();
		
		for(ArrayList<Pair<Double, Double>> list : series){
			for(Pair<Double, Double> pair : list){
				dataset.add(pair.second);
			}
		}
		
		//Convert to doubles
		double[] retVal = new double[dataset.size()];
		for(int i =0; i < dataset.size(); i++){
			retVal[i] = dataset.get(i);
		}
		
		return retVal;
	}
	
	public void saveThumbNailSizeChart(){
		saveChartToFile(THUMBNAIL_DIMENSION, ModelStatistics.DEFAULT_SAVE_LOCATION);
	}
	
	public void saveFullSizeChart(String location){
		saveChartToFile(SAVE_DIMENSION, location);//TODO change back
	}

	public void saveChartToFile(Dimension printSize, String location) {

		saveChartToFile(chart, printSize, location, filename);
		
		for(JFreeChart extraChart : extraCharts){
			saveChartToFile(extraChart, printSize, location, filename+"-"+(extraCharts.indexOf(extraChart)+1));
		}
		
	}
	
	private void saveChartToFile(JFreeChart chart, Dimension printSize, String location, String filename){
		cd("/");
		mkdir(location);
		try {

			ChartUtilities.saveChartAsJPEG(
					new File(location + "/" + filename + ".jpg"), 
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
