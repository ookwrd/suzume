package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ModelStatistics.PrintSize;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import tools.Pair;
import tools.Statistics;

@SuppressWarnings("serial")
public class ChartPanel extends JPanel {
	
	public static enum ChartType {LINE_CHART, HISTOGRAM, SCATTER_PLOT, AREA_CHART};
	
	private static final Dimension LARGE_DIMENSION = new Dimension(1500, 900);
	private static final Dimension MEDIUM_DIMENSION = new Dimension(750,450);
	private static final Dimension SMALL_DIMENSION = new Dimension(500,300);

	public static double HISTOGRAM_X_MIN = 7;
	public static double HISTOGRAM_X_MAX = 12;
	public static double HISTOGRAM_Y_MIN = -1; // a negative value means no min value
	public static double HISTOGRAM_Y_MAX = -1; // a negative value means no max value
	
	private static final int NUMBER_OF_BINS = 200;
	
	private String filename;
	
	JFreeChart chart;
	
	public ChartPanel(ArrayList<Pair<Double, Double>>[] data, 
			String title, 
			boolean average, 
			boolean density, 
			String xLabel, 
			String yLabel, 
			String configName){
		
		super();
		
		if(average){
			data = Statistics.averageArrayLists(data);
			title = "Average " + title;
		}
		
		ChartType type = determineType(density);
		
		chart = createChart(data, type, title, xLabel, yLabel);
		add(createImageJLabel());
		
		this.filename = title.replaceAll(" ", "") + "-" + configName;
		
	}
	
	private ChartType determineType(boolean density){
		
		if(density){
			return ChartType.HISTOGRAM;
		}
		
		return ChartType.LINE_CHART;
	}
	
	private JFreeChart createChart(ArrayList<Pair<Double, Double>>[] series, 
			ChartType type, 
			String title, 
			String xLabel, 
			String yLabel){
		
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
			
			//TODO provisoire !
			if (HISTOGRAM_Y_MIN>=0.0) catPlot.getRangeAxis().setLowerBound(HISTOGRAM_Y_MIN);
			if (HISTOGRAM_Y_MAX>=0.0) catPlot.getRangeAxis().setUpperBound(HISTOGRAM_Y_MAX);
			
			if (HISTOGRAM_X_MIN>=0.0) catPlot.getDomainAxis().setLowerBound(HISTOGRAM_X_MIN);
			if (HISTOGRAM_X_MAX>=0.0) catPlot.getDomainAxis().setUpperBound(HISTOGRAM_X_MAX);
            
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
		
		dataSet.addSeries(new Double(1), Statistics.stripIndexValues(series), NUMBER_OF_BINS);
		
		return dataSet;
	}
	
	public JLabel createImageJLabel() {
		
		//TODO save these
		BufferedImage imageSmall = chart.createBufferedImage(
				SMALL_DIMENSION.width, 
				SMALL_DIMENSION.height
				);
		
		BufferedImage imageLarge = chart.createBufferedImage(
				LARGE_DIMENSION.width, 
				LARGE_DIMENSION.height
				);
		
		
		return new ZoomPanel(imageSmall, imageLarge);

		/*JLabel chartLabel = new JLabel();
		ImageIcon icon = new ImageIcon(image);
		chartLabel.setIcon(icon);
		
		return chartLabel;*/
	}
	
	public void printToFile(PrintSize size, String location) {
		
		Dimension dimension;
		
		switch(size){
		
		case SMALL:
			dimension = SMALL_DIMENSION;
			break;
			
		case MEDIUM:
			dimension = MEDIUM_DIMENSION;
			break;
		
		default:
		case LARGE:
			dimension = LARGE_DIMENSION;
			break;
		}
		
		printToFile(dimension, location, filename);
		
	}
	
	private void printToFile(Dimension printSize, String location, String filename){
		
		//Location
		cd("/");
		mkdir(location);
		
		try {

			ChartUtilities.saveChartAsJPEG(
					new File(location + "/" + filename + "-" + printSize.height + "x" + printSize.width + ".jpg"), 
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

		//TODO make this work recursively on deep directories.
		
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
