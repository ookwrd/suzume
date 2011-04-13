
package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tools.Pair;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	private static final Dimension SAVE_DIMENSION = new Dimension(1000, 600);
	private static final Dimension THUMBNAIL_DIMENSION = new Dimension(500,300);
	
	private static enum ChartType {LINE_CHART, BAR_CHART, SCATTER_PLOT};

    private JFrame frame;
    private JScrollPane scrollPane;
    private JButton saveButton;
    
	private ArrayList<BufferedImage> images;
	private ArrayList<JFreeChart> charts;
	private ArrayList<String> filenames;
	
	public ModelStatistics(String title) {
		
		//Gets rid of all the fancy graph settings.
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		frame = new JFrame();
		frame.setTitle(title);
		frame.setSize(new Dimension(1000, 500));
		
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		scrollPane = new JScrollPane(this);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		
		frame.add(scrollPane);
		
		this.images = new ArrayList<BufferedImage>();
		this.charts = new ArrayList<JFreeChart>();
		this.filenames = new ArrayList<String>();
		
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
		frame.setVisible(true);
		saveAllCharts();
	}
	
	public void plot(ArrayList<Double>[] dataSets, String title, String yLabel,
			String xLabel, String experiment) {

		ChartType type = ChartType.LINE_CHART;
		XYSeries[] newSeries = new XYSeries[dataSets.length];

		for (int j = 0; j < dataSets.length; j++) {

			newSeries[j] = new XYSeries(yLabel + j);

			ArrayList<Double> data = dataSets[j];

			for (int i = 0; i < data.size(); i++) {
				newSeries[j].add(new Double(

				i // x
						), new Double(

						data.get(i) // y
						));
			}

		}

		JFreeChart chart = createChart(newSeries, type, title, xLabel, yLabel);
		
		add(new JLabel(new ImageIcon(createImage(chart, title, experiment))));
		validate();

	}

	public void plot(ArrayList<Pair<Double, Double>> table, String title,
			String yLabel, String xLabel, String experiment) {
		ChartType type = ChartType.SCATTER_PLOT;//TODO
		
		XYSeries[] newSeries = new XYSeries[1];
		newSeries[0] = new XYSeries(yLabel);

		for(Pair<Double, Double> pair : table){
			newSeries[0].add(pair.first, pair.second);
		}

		JFreeChart chart = createChart(newSeries, type, title, xLabel, yLabel);
		
		add(new JLabel(new ImageIcon(createImage(chart, title, experiment))));
		validate();
		frame.validate();
	}

	private JFreeChart createChart(ArrayList<Pair<Double, Double>>[] series, ChartType type){
		
		return null;
	}

	
	private JFreeChart createChart(XYSeries[] series, ChartType type, String title, String xLabel, String yLabel) {

		XYSeriesCollection dataset = new XYSeriesCollection();

		for (int i = 0; i < series.length; i++) {
			dataset.addSeries(series[i]);
		}

		JFreeChart chart;
		switch (type) {

		case BAR_CHART:
			chart = ChartFactory.createHistogram(title, // Title
					xLabel, // X-Axis label
					yLabel, // Y-Axis label
					dataset, // Dataset
					PlotOrientation.VERTICAL, // Plot orientation
					false, // Show legend
					false, // Tooltips
					false); // URL
			XYPlot catPlot = chart.getXYPlot();
			((XYBarRenderer)catPlot.getRenderer()).setShadowVisible(false);
			break;
			
		case SCATTER_PLOT:
			
			chart = ChartFactory.createScatterPlot(title, 
					xLabel, 
					yLabel, 
					dataset, 
					PlotOrientation.VERTICAL, 
					false, 
					false, 
					false);
			XYPlot plot = (XYPlot)chart.getPlot();
			XYDotRenderer renderer = new XYDotRenderer();
			renderer.setDotWidth(2);
			renderer.setDotHeight(2);
			plot.setRenderer(renderer);
			break;

		case LINE_CHART:
		default:
			chart = ChartFactory.createXYLineChart(title, // Title
					xLabel, // X-Axis label
					yLabel, // Y-Axis label
					dataset, // Dataset
					PlotOrientation.VERTICAL, // Plot orientation
					false, // Show legend
					false, // Tooltips
					false); // URL;
		}

		return chart;
	}

	/**
	 * 
	 * @param currentSeries
	 * @param title
	 * @param printToFile
	 *            : if true, the corresponding file is created
	 * @return
	 */
	private BufferedImage createImage(JFreeChart chart, String title, String experiment) {
		
		BufferedImage image = chart.createBufferedImage(
				THUMBNAIL_DIMENSION.width, 
				THUMBNAIL_DIMENSION.height
				);
		String filename = title.replaceAll(" ", "") + "-" + experiment + ".jpg";
		
		charts.add(chart);
		images.add(image);
		filenames.add(filename);
		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));

		return image;
	}

	/**
	 * Create a jpg file for the currentImage of the curChart
	 * 
	 * @param curChart
	 * @param currentImage
	 */
	private void createFile(JFreeChart chart, BufferedImage image, String filename, Dimension printSize) {

		cd("/");
		mkdir("/suzume-charts");
		try {

			ChartUtilities.saveChartAsJPEG(
					new File("/suzume-charts/" + filename), 
					chart,
					printSize.width, 
					printSize.height
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save all charts displayed in window. The smaller images are replaced by larger ones.
	 */
	public void saveAllCharts() {
		for (int i = 0; i < images.size(); i++) {
			createFile(charts.get(i), images.get(i), filenames.get(i), SAVE_DIMENSION);
		}
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
	
	/**
	 * Change to a directory
	 * 
	 * @param name
	 */
	private void cd(String name) {
		System.setProperty("user.dir", name);
	}

}
