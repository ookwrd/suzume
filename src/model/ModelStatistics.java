
package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import tools.Pair;
import tools.ScreenImage;
import tools.Statistics;

import model.ChartPanel.ChartType;


@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	public static final String DEFAULT_SAVE_LOCATION = "/suzume-charts";
	
	public enum PlotType {TIMESERIES, DENSITY}
	
	private boolean trim = true;
	//public static Integer[][] TRIM_INTERVALS = {{2000,Integer.MAX_VALUE},{0,1000},{1000,2000},{2000,3000},{3000,4000},{4000,5000},{9000,11000},{15000,16000},{19000,21000}};
	public static Integer[][] TRIM_INTERVALS = { { 2000, Integer.MAX_VALUE },
			{ 0, 1000 }, { 1000, 2000 }, { 2000, 3000 }, { 3000, 4000 },
			{ 4000, 5000 }, { 5000, 6000 }, { 6000, 7000 }, { 7000, 8000 },
			{ 8000, 9000 }, { 9000, 10000 }, { 10000, 11000 },
			{ 11000, 12000 }, { 12000, 13000 }, { 13000, 14000 },
			{ 14000, 15000 }, { 15000, 16000 }, { 16000, 17000 },
			{ 17000, 18000 }, { 18000, 19000 }, { 19000, 20000 },
			{ 20000, 21000 }, { 21000, 22000 }, { 22000, 23000 },
			{ 23000, 24000 }, { 24000, 25000 }, { 25000, 26000 },
			{ 26000, 27000 }, { 27000, 28000 }, { 28000, 29000 },
			{ 29000, 30000 }, { 30000, 31000 }, { 31000, 32000 },
			{ 32000, 33000 }, { 33000, 34000 }, { 34000, 35000 },
			{ 35000, 36000 }, { 36000, 37000 }, { 37000, 38000 },
			{ 38000, 39000 }, { 39000, 40000 }, { 40000, 41000 },
			{ 41000, 42000 }, { 42000, 43000 }, { 43000, 44000 },
			{ 44000, 45000 }, { 45000, 46000 }, { 46000, 47000 },
			{ 47000, 48000 }, { 48000, 49000 }, { 49000, 50000 },
			{ 50000, 51000 }, { 51000, 52000 }, { 52000, 53000 },
			{ 53000, 54000 }, { 54000, 55000 }, { 55000, 56000 },
			{ 56000, 57000 }, { 57000, 58000 }, { 58000, 59000 },
			{ 59000, 60000 }, { 60000, 61000 }, { 61000, 62000 },
			{ 62000, 63000 }, { 63000, 64000 }, { 64000, 65000 },
			{ 65000, 66000 }, { 66000, 67000 }, { 67000, 68000 },
			{ 68000, 69000 }, { 69000, 70000 }, { 70000, 71000 },
			{ 71000, 72000 }, { 72000, 73000 }, { 73000, 74000 },
			{ 74000, 75000 }, { 75000, 76000 }, { 76000, 77000 },
			{ 77000, 78000 }, { 78000, 79000 }, { 79000, 80000 },
			{ 80000, 81000 }, { 81000, 82000 }, { 82000, 83000 },
			{ 83000, 84000 }, { 84000, 85000 }, { 85000, 86000 },
			{ 86000, 87000 }, { 87000, 88000 }, { 88000, 89000 },
			{ 89000, 90000 }, { 90000, 91000 }, { 91000, 92000 },
			{ 92000, 93000 }, { 93000, 94000 }, { 94000, 95000 },
			{ 95000, 96000 }, { 96000, 97000 }, { 97000, 98000 },
			{ 98000, 99000 }, { 99000, 100000 }, { 100000, 101000 } }; // Genotypes
																		// vs
																		// time

    private JFrame frame;
    private JScrollPane scrollPane;
    private JButton saveButton;
    
    private ArrayList<ChartPanel> chartPanels;
	private TextArea textArea = null;
	private BasicVisualizationServer<Integer, String> vv;
	private JPanel clusteringPanel;
	private JPanel graphPanel;
	
	public ModelStatistics(String title) {
		
		//Gets rid of all the fancy graph settings.
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		frame = new JFrame();
		frame.setTitle(title);
		
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		scrollPane = new JScrollPane(this);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		
		frame.add(scrollPane);
		
		this.chartPanels = new ArrayList<ChartPanel>();
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		final JTextField saveDestination = new JTextField(DEFAULT_SAVE_LOCATION);
		buttonPanel.add(new JLabel("Save images to:"));
		buttonPanel.add(saveDestination);
		
		saveButton = new JButton("Save all graphs");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveAllCharts(saveDestination.getText());
			}
		});
		buttonPanel.add(saveButton);
		

		clusteringPanel = new JPanel();
		clusteringPanel.setLayout(new BorderLayout());
		add(clusteringPanel);
		
		frame.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void display() {
		frame.pack();
		frame.setVisible(true);
		saveAllChartsThumbnail();
	}
	
	public void plotDensity(ArrayList<Pair<Double, Double>>[] table, String title,
			 String xLabel, String experiment) {

		ChartType type = ChartType.HISTOGRAM;
		ChartPanel chartPanel = new ChartPanel(table, type, title + " (Generations " + 0 + "-" + table[0].size() + ")"
, "Occurences", xLabel, experiment);
		addChartPanel(chartPanel);

		//Do we also trim?
		if(trim){

			int length = table[0].size();
			
			for(int i = 0; i < TRIM_INTERVALS.length; i++){
				
				if(length <= TRIM_INTERVALS[i][0]){
					continue;
				}
				
				int trimStart = TRIM_INTERVALS[i][0];// <= length ? TRIM_INTERVALS[i][0] : 0;
				int trimEnd =  TRIM_INTERVALS[i][1] < length ? TRIM_INTERVALS[i][1] : length;
		
				ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
				chartPanel.addAdditionalChart(trimmedDataArrayList, type, title + " (Generations " + trimStart + "-" + trimEnd+")", "Occurences", xLabel);
			}
		}
		
	}
	
	public void plotTimeSeries(ArrayList<Pair<Double, Double>>[] table, String title,
			String label, String experiment){

		ChartPanel chartPanel = new ChartPanel(table, ChartType.LINE_CHART, title, label, "Generations", experiment);
		addChartPanel(chartPanel);
		
		//Do we also trim?
		if(trim){
			int length = table[0].size();
			
			for(int i = 0; i < TRIM_INTERVALS.length; i++){
				
				if(length <= TRIM_INTERVALS[i][0]){
					continue;
				}
				
				int trimStart = TRIM_INTERVALS[i][0];
				int trimEnd =  TRIM_INTERVALS[i][1] < length ? TRIM_INTERVALS[i][1] : length;
		
				ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
				
				chartPanel.addAdditionalChart(trimmedDataArrayList, ChartType.LINE_CHART, title, label, "Generations");
			
			}
		}
	}
	
	public void addChartPanel(ChartPanel chartPanel){
		add(chartPanel);
		chartPanels.add(chartPanel);
		validate();
		frame.validate();
	}
	
	private void addConsolePanel(String text) {
		JPanel consolePanel = new JPanel();
		textArea = new TextArea(text);
		textArea.setEditable(false);
		consolePanel.add(textArea);
		clusteringPanel.add(consolePanel, BorderLayout.SOUTH);
		validate();
		frame.validate();
	}
	
	private void addGraphPanel(BasicVisualizationServer<Integer, String> vv, String title) {
		graphPanel = new JPanel();
		graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));
		clusteringPanel.add(graphPanel, BorderLayout.NORTH);
		JTextArea t = new JTextArea(title);
		t.setEditable(false);
		graphPanel.add(t);
		graphPanel.add(vv);
		validate();
		frame.validate();
	}
	
	public void addGraph(BasicVisualizationServer<Integer, String> vv, String title) {
		if(this.graphPanel==null) addGraphPanel(vv, title); // lazy
		else {
			JTextArea t = new JTextArea(title);
			t.setEditable(false);
			graphPanel.add(t);
			graphPanel.add(vv);
			validate();
			frame.validate();
		}
	}
	
	public void updateConsoleText(String text) {
		if(textArea==null) addConsolePanel(text); // lazy
		else textArea.setText(text);
	}
	
	/**
	 * Save all charts displayed in window. The smaller images are replaced by larger ones.
	 */
	public void saveAllCharts(String location) {
		for (ChartPanel panel : chartPanels) {
			panel.saveFullSizeChart(location);
		}
	}
	
	public void saveAllChartsThumbnail() {
		for (ChartPanel panel : chartPanels) {
			panel.saveThumbNailSizeChart();
		}
	}
	
	public void saveGraphs(String imageFileName) {

		   BufferedImage bufImage = ScreenImage.createImage((JComponent) graphPanel);
		   try {
		       File outFile = new File(DEFAULT_SAVE_LOCATION+"/"+imageFileName+".png");
		       ImageIO.write(bufImage, "png", outFile);
		       System.out.println("wrote image to " +DEFAULT_SAVE_LOCATION+"/"+ imageFileName +".png");
		   } catch (Exception e) {
		       System.out.println("writeToImageFile(): " + e.getMessage());
		   }
	}

}
