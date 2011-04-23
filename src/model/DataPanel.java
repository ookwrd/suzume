package model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
public class DataPanel extends JPanel {

	private static final Dimension LARGE_DIMENSION = new Dimension(1000, 600);
	private static final Dimension MEDIUM_DIMENSION = new Dimension(750,450);
	private static final Dimension SMALL_DIMENSION = new Dimension(500,300);
	
	private static final int NUMBER_OF_BINS = 200;
	
	public static double HISTOGRAM_X_MIN = 7;
	public static double HISTOGRAM_X_MAX = 12;
	public static double HISTOGRAM_Y_MIN = -1; // a negative value means no min value
	public static double HISTOGRAM_Y_MAX = -1; // a negative value means no max value
	
	public static enum ChartType {LINE_CHART, HISTOGRAM, SCATTER_PLOT, AREA_CHART};
	
	private ModelStatistics parent;

	public static final int DEFAULT_TRIM_START = 0;
	public static final int DEFAULT_TRIM_END = 5000;
	
	private JPanel buttonPanel;
	private JTextField trimStartField;
	private JTextField trimEndField;
	private JButton trimButton;
	
	private JButton printButton;
	private JButton removeButton;
	
	private JPanel chartPanel;
	
	private ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
	private String filename;
	
	
	private ArrayList<Pair<Double, Double>>[] data;
	private ChartType type;
	private String title;
	private String yLabel;
	private String xLabel;
	
	
	public DataPanel(ArrayList<Pair<Double, Double>> data, ChartType type, String title,
			String yLabel, String xLabel, String printName, ModelStatistics parent){
		this(wrapArrayList(data), type, title, yLabel, xLabel, printName, parent);			
	}
	
	public DataPanel(ArrayList<Pair<Double, Double>>[] data, ChartType type, String title,
			String yLabel, String xLabel, String printName, ModelStatistics parent){//TODO remove external printName generation
		super();
		
		this.setLayout(new BorderLayout());
		
		this.data = data;
		this.type = type;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		
		this.parent= parent;
		
		this.filename = title.replaceAll(" ", "") + "-" + printName;
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		trimStartField = new JTextField(""+DEFAULT_TRIM_START,5);
		trimEndField = new JTextField(""+DEFAULT_TRIM_END,5);
		
		trimButton = new JButton("Trim");
		trimButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int trimStart = Integer.parseInt(trimStartField.getText());
				int trimEnd = Integer.parseInt(trimEndField.getText());
				addTrimmedChart(trimStart, trimEnd);
			}
		});
		
		
		printButton = new JButton("Print");//TODO action Listener
		
		removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeThisPanel();
			}
		});
		
		buttonPanel.add(new JLabel("Trim from:"));
		buttonPanel.add(trimStartField);
		buttonPanel.add(new JLabel("to:"));
		buttonPanel.add(trimEndField);
		buttonPanel.add(trimButton);
		buttonPanel.add(printButton);
		buttonPanel.add(removeButton);
		
		add(buttonPanel, BorderLayout.NORTH);
		

		chartPanel = new JPanel();
		chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
		
		JFreeChart chart = createChart(data, type, title, xLabel, yLabel);
		chartPanel.add(createImageJLabel(chart));
		
		charts.add(chart);
		
		add(chartPanel);
	}
	
	private JLabel createImageJLabel(JFreeChart chart) {
		
		BufferedImage image = chart.createBufferedImage(
				SMALL_DIMENSION.width, 
				SMALL_DIMENSION.height
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
	

	
	
	public void saveChart(String location, PrintSize size){
		
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
		
		saveChartToFile(dimension, ModelStatistics.DEFAULT_SAVE_LOCATION);
	}

	public void saveChartToFile(Dimension printSize, String location) {
		for(JFreeChart extraChart : charts){
			saveChartToFile(extraChart, printSize, location, filename+"-"+(charts.indexOf(extraChart)+1));
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
			
			//TODO refator this out
			
			ChartUtilities.saveChartAsJPEG(
					new File(location + "/" + filename + "-small.jpg"), 
					chart,
					SMALL_DIMENSION.width, 
					SMALL_DIMENSION.height
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void removeThisPanel(){
		parent.removeDataPanel(this);
	}
	
	public void addTrimmedChart(int trimStart, int trimEnd){
		
		int length = data[0].size();
		
		if(length <= trimStart){
			//TODO error message
			return;
		}
		
		int trimStartAdjusted = trimStart;//TODO
		int trimEndAdjusted =  trimEnd < length ? trimEnd : length;

		ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(data, trimStartAdjusted, trimEndAdjusted);

		JFreeChart chart = createChart(trimmedDataArrayList, type, title+ " (Generations " + trimStartAdjusted + "-" + trimEndAdjusted+")",  "Occurences", xLabel);
		
		charts.add(chart);
		chartPanel.add(createImageJLabel(chart));
		
		revalidate();
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
