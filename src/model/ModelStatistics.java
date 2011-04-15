
package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import tools.Pair;
import tools.Statistics;

import model.ChartPanel.ChartType;


@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	public static final String DEFAULT_SAVE_LOCATION = "/suzume-charts";
	
	public enum PlotType {TIMESERIES, DENSITY}
	
	private boolean trim = true;
	public static Integer[][] TRIM_INTERVALS = {{2000,Integer.MAX_VALUE},{1000,2000},{4000,5000},{7000,8000},{10000,11000}};
//	public static final int trimStart = 2000;
//	public static final int trimEnd = Integer.MAX_VALUE;
	
    private JFrame frame;
    private JScrollPane scrollPane;
    private JButton saveButton;
    
    private ArrayList<ChartPanel> chartPanels;
	private TextArea textArea = null;
	private BasicVisualizationServer<Integer, String> vv = null;
	private JPanel clusteringPanel;
	
	public ModelStatistics(String title) {
		
		//Gets rid of all the fancy graph settings.
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		frame = new JFrame();
		frame.setTitle(title);
		
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		scrollPane = new JScrollPane(this);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
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
		//ArrayList<Pair<Double, Double>>[] densityData = Statistics.calculateDensity(Statistics.aggregateArrayLists(table));
		ChartPanel chartPanel = new ChartPanel(table, type, title + "(Density)", "Occurences", xLabel, experiment);
		addChartPanel(chartPanel);

		//Do we also trim?
		if(trim){
			int length = table[0].size();
			int trimStart = TRIM_INTERVALS[0][0] < length ? TRIM_INTERVALS[0][0] : 0;
			int trimEnd = TRIM_INTERVALS[0][1] < length? TRIM_INTERVALS[0][1] : length;

			ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
			//densityData = Statistics.calculateDensity(Statistics.aggregateArrayLists(trimmedDataArrayList));
			chartPanel.addAdditionalChart(trimmedDataArrayList, type, title + " (Density Trimmed " + trimStart + "-" + trimEnd+")", "Occurences", xLabel);
		}
		
	}
	
	public void plotTimeSeries(ArrayList<Pair<Double, Double>>[] table, String title,
			String label, String experiment){

		ChartPanel chartPanel = new ChartPanel(table, ChartType.LINE_CHART, title, label, "Generations", experiment);
		addChartPanel(chartPanel);
		
		//Do we also trim?
		if(trim){
			int length = table[0].size();
			int trimStart = TRIM_INTERVALS[0][0] < length ? TRIM_INTERVALS[0][0] : 0;
			int trimEnd = TRIM_INTERVALS[0][1] < length? TRIM_INTERVALS[0][1] : length;
	
			ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
			chartPanel.addAdditionalChart(trimmedDataArrayList, ChartType.LINE_CHART, title + " (Trimmed " + trimStart + "-" + trimEnd+")", label, "Generations");
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
		consolePanel.add(textArea);
		clusteringPanel.add(consolePanel, BorderLayout.SOUTH);
		
		validate();
		frame.validate();
	}
	
	private void addGraphPanel(BasicVisualizationServer<Integer, String> vv) {
		JPanel graphPanel = new JPanel();
		clusteringPanel.add(graphPanel, BorderLayout.NORTH);
		graphPanel.add(vv);
		validate();
		frame.validate();
	}
	
	public void addGraph(BasicVisualizationServer<Integer, String> vv) {
		if(this.vv==null) addGraphPanel(vv); // lazy
		else this.vv = vv;
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

}
