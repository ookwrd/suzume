
package model;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.JFreeChart;

import tools.Pair;
import tools.Statistics;

import model.ChartPanel.ChartType;


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
	
	public void plot(ArrayList<Pair<Double, Double>>[] table, String title,
			String yLabel, String xLabel, String experiment, PlotType plotType) {
		
		ChartPanel chartPanel;
		switch (plotType) {
		case DENSITY:
			ArrayList<Pair<Double, Double>>[] densityData = Statistics.calculateDensity(Statistics.aggregateArrayLists(table));
			chartPanel = new ChartPanel(densityData, ChartType.SCATTER_PLOT, title + "(Density)", yLabel, xLabel, experiment);
			addChartPanel(chartPanel);
			
			//Do we also trim?
			if(trim){
				int length = table[0].size();
				int trimStart = this.trimStart < length? this.trimStart : 0;
				int trimEnd = this.trimEnd < length? this.trimEnd : length;
				
				ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
				densityData = Statistics.calculateDensity(Statistics.aggregateArrayLists(trimmedDataArrayList));
				chartPanel.addAdditionalChart(densityData, ChartType.SCATTER_PLOT, title + " (Density Trimmed " + trimStart + "-" + trimEnd+")", yLabel, xLabel);
			}
			break;

		case TIMESERIES:
		default:
			chartPanel = new ChartPanel(table, ChartType.LINE_CHART, title, yLabel, xLabel, experiment);
			addChartPanel(chartPanel);
			
			//Do we also trim?
			if(trim){
				int length = table[0].size();
				int trimStart = this.trimStart < length? this.trimStart : 0;
				int trimEnd = this.trimEnd < length? this.trimEnd : length;
				
				ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(table, trimStart, trimEnd);
				chartPanel.addAdditionalChart(trimmedDataArrayList, ChartType.LINE_CHART, title + " (Trimmed " + trimStart + "-" + trimEnd+")", yLabel, xLabel);
			}
			break;
		}
		
	}
	
	public void addChartPanel(ChartPanel chartPanel){
		add(chartPanel);
		chartPanels.add(chartPanel);

		validate();
		frame.validate();
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
