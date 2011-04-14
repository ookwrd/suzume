
package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tools.Pair;

import model.ChartPanel.ChartType;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	public enum PlotType {TIMESERIES, DENSITY}
	
    private JFrame frame;
    private JScrollPane scrollPane;
    private JButton saveButton;
    
    private ArrayList<ChartPanel> chartPanels;
	
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
			chartPanel = new ChartPanel(table, ChartType.SCATTER_PLOT, title, yLabel, xLabel, experiment);
			break;

		case TIMESERIES:
		default:
			chartPanel = new ChartPanel(table, ChartType.LINE_CHART, title, yLabel, xLabel, experiment);
			break;
		}
		
		addChartPanel(chartPanel);
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
