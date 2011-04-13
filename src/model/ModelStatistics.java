
package model;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.ModelController.Pair;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	public int saveSize = 200;
	public int smallSize = 100;
	public int[] imageRatio = {5, 3};
	
	private final static boolean SAVE_WHILE_PROCESSING = false;
	
	private static enum chartType {LINE_CHART, BAR_CHART, SCATTER_PLOT};

	private String experiment = "default";
	private String yLabel = "";
	private String xLabel = "";
	
	private chartType type;//TODO olaf can this be removed?

    private JFrame frame;
    JScrollPane scrollPane;
    private JButton saveButton;
    
 // for the current graph
	private String title = "The current chart";
	private BufferedImage currentImage;
	private JFreeChart curChart;
	private String curFilename;

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
	}

	public void plot(ArrayList<Double>[] dataSets, String title, String yLabel,
			String xLabel, String experiment) {
		this.experiment = experiment;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.type = chartType.LINE_CHART;
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

		add(new JLabel(new ImageIcon(createImage(newSeries))));
		validate();

		saveCurChart();
	}

	public void plot(ArrayList<Pair<Double, Integer>> table, String title,
			String yLabel, String xLabel, String experiment) {
		this.experiment = experiment;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.type = chartType.SCATTER_PLOT;//TODO
		
		XYSeries[] newSeries = new XYSeries[1];
		newSeries[0] = new XYSeries(yLabel);

		for(Pair<Double, Integer> pair : table){
			newSeries[0].add(pair.first, pair.second);
		}

		add(new JLabel(new ImageIcon(createImage(newSeries))));
		validate();
		frame.validate();
		saveCurChart();
	}

	private JFreeChart createChart(XYSeries[] series, chartType type) {

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

	private BufferedImage createImage(XYSeries[] series) {
		return createImage(series, SAVE_WHILE_PROCESSING, type);
	}

	/**
	 * 
	 * @param currentSeries
	 * @param title
	 * @param printToFile
	 *            : if true, the corresponding file is created
	 * @return
	 */
	private BufferedImage createImage(XYSeries[] series,
			boolean printToFile, chartType type) {
		curChart = createChart(series, type);
		currentImage = curChart.createBufferedImage(imageRatio[0]
		    * smallSize, imageRatio[1] * smallSize);
		curFilename = title.replaceAll(" ", "") + "-" + experiment + ".jpg";
		
		charts.add(curChart);
		images.add(currentImage);
		filenames.add(curFilename);
		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(currentImage));

		return currentImage;
	}

	/**
	 * Create a jpg file for the currentImage of the curChart
	 * 
	 * @param curChart
	 * @param currentImage
	 */
	private void createFile(JFreeChart chart, BufferedImage image, String filename, int printSize) {

		cd("/");
		mkdir("/suzume-charts");
		try {

			ChartUtilities.saveChartAsJPEG(
					new File("/suzume-charts/" + filename), chart,
					imageRatio[0] * printSize, imageRatio[1] * printSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a directory
	 * 
	 * @param name
	 */
	private void cd(String name) {
		System.setProperty("user.dir", name);
		System.out.println(System.getProperty("user.dir"));
	}

	/**
	 * This only prints the current curChart to file without showing it in a window
	 */
	private void saveCurChart() {
		
		createFile(curChart, currentImage, curFilename, smallSize);

	}
	
	/**
	 * Save all charts displayed in window. The smaller images are replaced by larger ones.
	 */
	public void saveAllCharts() {
		for (int i = 0; i < images.size(); i++) {
			createFile(charts.get(i), images.get(i), filenames.get(i), saveSize);
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

	private void displayChart(BufferedImage image) {//TODO olaf can this be removed?
		try {
			System.out.println("Enter currentImage name\n");
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					System.in));
			String imageName = bf.readLine();
			File input = new File(imageName);
			image = ImageIO.read(input);
		} catch (IOException ie) {
			System.out.println("Error:" + ie.getMessage());
		}
	}

}
