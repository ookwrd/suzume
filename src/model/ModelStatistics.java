
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jfree.chart.JFreeChart;

import tools.Pair;
import tools.Statistics;

import model.ChartPanel.ChartType;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	public enum PlotType {TIMESERIES, DENSITY}
	
	private boolean trim = true;
	private int trimStart = 2000;
	private int trimEnd = Integer.MAX_VALUE;
	
    private JFrame frame;
    private JScrollPane scrollPane;
    private JButton saveButton;
    
    private ArrayList<ChartPanel> chartPanels;
	private TextArea textArea;
	
	public ModelStatistics(String title) {
		
		//Gets rid of all the fancy graph settings.
		//ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		frame = new JFrame();
		frame.setTitle(title);
		
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		scrollPane = new JScrollPane(this);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		
		frame.add(scrollPane);
		
		this.chartPanels = new ArrayList<ChartPanel>();
		addConsolePanel("");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		saveButton = new JButton("Save all graphs");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveAllCharts();
			}
		});
		buttonPanel.add(saveButton);
		
		frame.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void display() {
		frame.pack();
		frame.setVisible(true);
		saveAllChartsThumbnail();
	}
	
	public void plotDensity(ArrayList<Pair<Double, Double>>[] table, String title,
			 String xLabel, String experiment) {

		ArrayList<Pair<Double, Double>>[] densityData = Statistics.calculateDensity(Statistics.aggregateArrayLists(table));
		ChartPanel chartPanel = new ChartPanel(densityData, ChartType.SCATTER_PLOT, title + "(Density)", "Occurences", xLabel, experiment);
		addChartPanel(chartPanel);

		//Do we also trim?
		if(trim){
			int length = table[0].size();
			int trimStart = this.trimStart < length? this.trimStart : 0;
			int trimEnd = this.trimEnd < length? this.trimEnd : length;

			ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
			densityData = Statistics.calculateDensity(Statistics.aggregateArrayLists(trimmedDataArrayList));
			chartPanel.addAdditionalChart(densityData, ChartType.SCATTER_PLOT, title + " (Density Trimmed " + trimStart + "-" + trimEnd+")", "Occurences", xLabel);
		}
		
	}
	
	public void plotTimeSeries(ArrayList<Pair<Double, Double>>[] table, String title,
			String label, String experiment){

		ChartPanel chartPanel = new ChartPanel(table, ChartType.LINE_CHART, title, label, "Generations", experiment);
		addChartPanel(chartPanel);
		
		//Do we also trim?
		if(trim){
			int length = table[0].size();
			int trimStart = this.trimStart < length? this.trimStart : 0;
			int trimEnd = this.trimEnd < length? this.trimEnd : length;
			
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
		textArea.setSize(this.getSize());
		consolePanel.add(textArea);
		frame.add(consolePanel, BorderLayout.AFTER_LAST_LINE);
		
		validate();
		frame.validate();
	}
	
	public void updateConsoleText(String text) {
		textArea.setText(text);
	}
	
	/**
	 * Save all charts displayed in window. The smaller images are replaced by larger ones.
	 */
	public void saveAllCharts() {
		for (ChartPanel panel : chartPanels) {
			panel.saveFullSizeChart();
		}
	}
	
	public void saveAllChartsThumbnail() {
		for (ChartPanel panel : chartPanels) {
			panel.saveThumbNailSizeChart();
		}
	}

}
