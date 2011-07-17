package statisticsVisualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.OverlayLayout;


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

import runTimeVisualization.ZoomPanel;
import statisticsVisualizer.ChartConfiguration.ChartType;
import tools.Pair;
import tools.Statistics;

@SuppressWarnings("serial")
public class ChartPanel extends JPanel implements ConfigurationParameterChangedListener, DataSetChangedListener {
	
	public static enum PrintSize {SMALL, MEDIUM, LARGE, EXTRA_LARGE}
	
	protected static final Dimension EXTRA_LARGE_DIMENSION = new Dimension(1500,900);
	protected static final Dimension LARGE_DIMENSION = new Dimension(1000, 600);
	protected static final Dimension MEDIUM_DIMENSION = new Dimension(750,450);
	protected static final Dimension SMALL_DIMENSION = new Dimension(500,300);

	private BufferedImage extraLargeImage;
	private BufferedImage largeImage;
	private BufferedImage mediumImage;
	private BufferedImage smallImage;
	
	private static boolean iconsLoaded = false; 
	
	private JFreeChart chart;
	private ZoomPanel chartImagePanel;
	private JPanel buttonPanel;
	public JPanel editPanel;
	
	private DataPanel parent;
	private ChartConfiguration config;
	
	private boolean editing;
	private JTextArea editableTitle;
	private JButton confirmEdit;
	private ActionListener stopEditListener;
	
	
	public ChartPanel(
			ChartConfiguration config,
			DataPanel parent){
		
		super();
		loadIcons();
		
		this.config = config;
		this.parent = parent;
		this.editing = false;
		
		createChart();

		setLayout(new OverlayLayout(this));
		
		setupEditPanel();
		
		setupButtonPanel();
		
		addChartImage();
		
		config.registerParameterChangeListener(this);
		parent.registerDatasetChangedListener(this);
		
	}
	
	private void setupEditPanel() {
		
		//Setup panel
		editPanel = new JPanel();
		editPanel.setOpaque(false);
		editPanel.setLayout(new FlowLayout(FlowLayout.TRAILING,0,0));
		add(editPanel);
		
		//Setup edit controls
		
		//Title
		editableTitle = new JTextArea(config.getTitle());
		editableTitle.setBackground(Color.WHITE);
		//editableTitle.setLineWrap(true);
		//editableTitle.setWrapStyleWord(true);
		editableTitle.setEditable(true);
		editableTitle.setVisible(false);
		editPanel.add(editableTitle);
		
		//Axis
		/*editableAxis = new JTextArea(config.getTitle());
		editableAxis.setBackground(Color.WHITE);
		editableAxis.setEditable(true);
		editableAxis.setVisible(false);
		editPanel.add(editableAxis);*/
		
		//Confirm
		confirmEdit = new JButton("OK");
		confirmEdit.setActionCommand("Stop editing");
		stopEditListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEditMode(false);
			}
		};
		confirmEdit.addActionListener(stopEditListener);
		confirmEdit.setVisible(false);
		editPanel.add(confirmEdit);
		
	}
	
	private void setEditMode(boolean edit) {
		
		//Set edit visible
		editableTitle.setVisible(edit);
		confirmEdit.setVisible(edit);
		
		//Update once done editing
		if(!edit)
			config.setTitle(editableTitle.getText());
	}

	private void setupButtonPanel(){
		
		//Setup the Panel
		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.TRAILING,0,0));
		add(buttonPanel);
		
		//Setup the buttons
		JButton configureChartButton = new HighlightButton(configureIcon);
		configureChartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setEditMode(true);
				//System.out.println("editing");
			}
		});
		buttonPanel.add(configureChartButton);

		JButton printChartButton = new HighlightButton(printIcon);
		printChartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("print");//TODO
				
			}
		});
		buttonPanel.add(printChartButton);
		
		JButton removeChartButton = new HighlightButton(removeIcon);
		removeChartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeThisChart();
			}
		});
		buttonPanel.add(removeChartButton);
		
	}
	
	private JFreeChart createChart(ArrayList<Pair<Double, Double>>[] series, ChartType type){
		
		JFreeChart chart;
		switch (type) {

		case HISTOGRAM:
			chart = ChartFactory.createHistogram(config.getTitle(), 
					config.getxLabel(),
					config.getyLabel(), 
					createHistogramDataset(series), 
					PlotOrientation.VERTICAL,
					false, 
					false, 
					false
					);
			
			XYPlot xyPlot = chart.getXYPlot();
			
			if (config.getHistogramYMin() >= 0) {
				xyPlot.getRangeAxis().setLowerBound(config.getHistogramYMin());
			}
			if (config.getHistogramYMax() >= 0) {
				xyPlot.getRangeAxis().setUpperBound(config.getHistogramYMax());
			}
			
			if (config.getHistogramXMin() >= 0) {
				xyPlot.getDomainAxis().setLowerBound(config.getHistogramXMax());
			}
			if (config.getHistogramXMax() >= 0) {
				xyPlot.getDomainAxis().setUpperBound(config.getHistogramXMax());
			}
            
			((XYBarRenderer)xyPlot.getRenderer()).setShadowVisible(false);
            
			chart.getPlot().setBackgroundPaint(Color.WHITE);
			chart.setBackgroundPaint(Color.WHITE);
			break;
		
		case AREA_CHART:
			chart = ChartFactory.createXYAreaChart(config.getTitle(), // Title
					config.getxLabel(), // X-Axis label
					config.getyLabel(), // Y-Axis label
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
			
			chart = ChartFactory.createScatterPlot(config.getTitle(), 
					config.getxLabel(), 
					config.getyLabel(), 
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
			chart = ChartFactory.createXYLineChart(config.getTitle(),
					config.getxLabel(),
					config.getyLabel(),
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
		
		dataSet.addSeries(new Double(1), Statistics.stripIndexValues(series), config.getNumberOfHistogramBins());
		
		return dataSet;
	}
	
	public void addChartImage() {
		
		chartImagePanel = new ZoomPanel(getSmallImage(), getExtraLargeImage());
		
		add(chartImagePanel);
		
	}
	
	protected BufferedImage getExtraLargeImage(){
		if(extraLargeImage == null){
			extraLargeImage = chart.createBufferedImage(
					EXTRA_LARGE_DIMENSION.width, 
					EXTRA_LARGE_DIMENSION.height
					);
		}
		return extraLargeImage;
	}
	
	protected BufferedImage getLargeImage(){
		if(largeImage == null){
			largeImage = chart.createBufferedImage(
					LARGE_DIMENSION.width, 
					LARGE_DIMENSION.height
					);
		}
		return largeImage;
	}
	
	protected BufferedImage getMediumImage(){
		if(mediumImage == null){
			mediumImage = chart.createBufferedImage(
					MEDIUM_DIMENSION.width, 
					MEDIUM_DIMENSION.height
					);
		}
		return mediumImage;
	}
	
	protected BufferedImage getSmallImage(){
		if(smallImage == null){
			smallImage = chart.createBufferedImage(
					SMALL_DIMENSION.width,
					SMALL_DIMENSION.height
					);
		}
		return smallImage;
	}
	
	
	protected BufferedImage getImage(PrintSize size){
		
		switch(size){
		
		case SMALL:
			return getSmallImage();
			
		case MEDIUM:
			return getMediumImage();
		
		default:
		case LARGE:
			return getLargeImage();
			
		case EXTRA_LARGE:
			return getExtraLargeImage();
		}
		
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
			System.out.println("Defaulting to large");
		case LARGE:
			dimension = LARGE_DIMENSION;
			break;
			
		case EXTRA_LARGE:
			dimension = EXTRA_LARGE_DIMENSION;
			break;
		}
		
		printToFile(dimension, location);
		
	}
	
	private void printToFile(Dimension printSize, String location){
		
		//Location
		cd("/");
		mkdir(location);
		
		try {

			ChartUtilities.saveChartAsJPEG(
					new File(location + "/" + config.getTitle().replaceAll(" ", "") + "-" + config.getConfigName() + "-" + printSize.height + "x" + printSize.width + ".jpg"), 
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
	
	private void removeThisChart(){
		parent.removeChartPanel(this);
	}

	private static final String REMOVE_ICON_LOCATION = "../graphics/icon_remove.png";
	private static final String PRINT_ICON_LOCATION = "../graphics/icon_print.png";
	private static final String CONFIGURE_ICON_LOCATION = "../graphics/icon_configure.png";
	
	private static ImageIcon removeIcon;
	private static ImageIcon printIcon;
	private static ImageIcon configureIcon;
	
	protected static synchronized void loadIcons() {
		if (iconsLoaded == true) {
			return;
		}
		
		URL image_url;
		image_url = ChartPanel.class.getResource(REMOVE_ICON_LOCATION);
		assert image_url != null;
		removeIcon = new ImageIcon(image_url, "Remove");
		
		image_url = ChartPanel.class.getResource(PRINT_ICON_LOCATION);
		assert image_url != null;
		printIcon = new ImageIcon(image_url, "Print");
		
		image_url = ChartPanel.class.getResource(CONFIGURE_ICON_LOCATION);
		assert image_url != null;
		configureIcon = new ImageIcon(image_url, "Configure");
		
		iconsLoaded = true;
		return;
	}

	@Override
	public void dataSetChangedListener(ArrayList<Pair<Double, Double>>[] dataSet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configurationParameterChanged() {
		
		if(chart == null){System.out.println("Chart is null");}

		if(config == null){System.out.println("Config is null");}
		
		//Reset cached image 
		smallImage = null;
		mediumImage = null;
		largeImage = null;
		extraLargeImage = null;
		
		//rebuild the underlying chart.
		createChart();
		
		//Replace currently displayed chart
		remove(chartImagePanel);
		
		addChartImage();
		revalidate();
		
	}
	
	private void createChart(){
		
		//Trim generations
		int length = parent.data[0].size();
		int trimStartAdjusted = config.getGenerationTrimStart();//TODO
		int trimEndAdjusted =  config.getGenerationTrimEnd() < length ? config.getGenerationTrimEnd() : length;
		ArrayList<Pair<Double, Double>>[] localData = Statistics.trimArrayLists(parent.data, trimStartAdjusted, trimEndAdjusted);
		
		if(config.isAverage()){
			localData = Statistics.averageArrayLists(localData);
			config.setTitle("Average " + config.getTitle());
		}
		
		ChartType type = determineType(config.isDensity());
		
		chart = createChart(localData, type);
	}
	
	
	private ChartType determineType(boolean density){
		
		if(density){
			return ChartType.HISTOGRAM;
		}
		return ChartType.LINE_CHART;
		
	}
}
