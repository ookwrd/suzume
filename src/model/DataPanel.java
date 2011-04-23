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
	
	private ArrayList<ChartPanel> chartPanels = new ArrayList<ChartPanel>();
	
	//private ArrayList<JFreeChart> charts = new ArrayList<JFreeChart>();
	
	
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
		
		
		printButton = new JButton("Print");
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			//	printToFile(size, )TODO
			}
		});
		
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
		
		ChartPanel chart = new ChartPanel(data, type, title, xLabel, yLabel, printName);
		chartPanel.add(chart);
		
		chartPanels.add(chart);
		
		add(chartPanel);
	}
	

	
	private static <E> ArrayList<E>[] wrapArrayList(ArrayList<E> input){
		@SuppressWarnings("unchecked")
		ArrayList<E>[] wrapper = new ArrayList[1];
		wrapper[0] = input;
		return wrapper;
	}

	
	public void printToFile(PrintSize size, String location) {
		
		for(ChartPanel panel : chartPanels){
			panel.printToFile(size, location);
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

		ChartPanel chart = new ChartPanel(trimmedDataArrayList, type, title, xLabel, yLabel, "XXX");//createChart(trimmedDataArrayList, type, title+ " (Generations " + trimStartAdjusted + "-" + trimEndAdjusted+")",  "Occurences", xLabel);
		
		chartPanels.add(chart);
		chartPanel.add(chart);
		
		revalidate();
	}
	

	
}
