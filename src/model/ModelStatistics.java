//TODO where did this come from.

package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class ModelStatistics extends JFrame {

	public int printSize = 200;
	public int viewSize = 100;
	public int[] imageRatio = { 5, 3 };

	private final static boolean SAVE_WHILE_PROCESSING = false;
	private static final int LINE_CHART = 1;
	private static final int BAR_CHART = 2;

	private String experimentId = "default";
	private String yLabel = "";
	private String xLabel = "";
	private String title = "A chart";
	
	private int chartType = LINE_CHART;

	JPanel innerPane = new JPanel();
	JScrollPane scroller = new JScrollPane(innerPane);

	public ModelStatistics(String title) {
		innerPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		scroller.setVerticalScrollBar(new JScrollBar());
		this.add(scroller, BorderLayout.CENTER);
		this.setTitle(title);
		this.setSize(new Dimension(1000, 500));
	}

	public void display() {
		setVisible(true);
	}

	public void plot(ArrayList<Double>[] dataSets, String title, String yLabel,
			String xLabel, String experimentId) {
		this.experimentId = experimentId;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.chartType = LINE_CHART;
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

		innerPane.add(new JLabel(new ImageIcon(createImage(newSeries))));
		innerPane.validate();

		saveChart(dataSets[0]);// TODO
	}

	public void plot(Hashtable<Double, Integer> table, String title,
			String yLabel, String xLabel, String experimentId) {
		this.experimentId = experimentId;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.chartType = BAR_CHART;
		XYSeries[] newSeries = new XYSeries[1];
		newSeries[0] = new XYSeries(yLabel);

		Enumeration<Double> e = table.keys();
		while (e.hasMoreElements()) {
			Double key = (Double) e.nextElement();
			newSeries[0].add(new Double(key // x
					), new Double(table.get(key) // y
					));
		}

		innerPane.add(new JLabel(new ImageIcon(createImage(newSeries))));
		innerPane.validate();

		// saveChart(table);
	}

	private JFreeChart createChart(XYSeries[] series, int type) {

		XYSeriesCollection dataset = new XYSeriesCollection();

		for (int i = 0; i < series.length; i++) {
			dataset.addSeries(series[i]);
		}

		JFreeChart chart;
		switch (type) {
		case LINE_CHART:
			chart = ChartFactory.createXYLineChart(title, // Title
					xLabel, // X-Axis label
					yLabel, // Y-Axis label
					dataset, // Dataset
					PlotOrientation.VERTICAL, // Plot orientation
					false, // Show legend
					false, // Tooltips
					false); // URL;
			break;

		case BAR_CHART:
			chart = ChartFactory.createBarChart(title, // Title
					xLabel, // X-Axis label
					yLabel, // Y-Axis label
					(CategoryDataset) dataset, // Dataset
					PlotOrientation.VERTICAL, // Plot orientation
					false, // Show legend
					false, // Tooltips
					false); // URL
			break;

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
		return createImage(series, yLabel, SAVE_WHILE_PROCESSING);
	}

	/**
	 * 
	 * @param series
	 * @param title
	 * @param printToFile
	 *            : if true, the corresponding file is created
	 * @return
	 */
	private BufferedImage createImage(XYSeries[] series, String title,
			boolean printToFile) {
		JFreeChart chart = createChart(series, LINE_CHART);
		BufferedImage image = chart.createBufferedImage(imageRatio[0]
				* viewSize, imageRatio[1] * viewSize);

		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));

		// Creating the corresponding file
		if (printToFile) {
			BufferedImage imagePrint = chart.createBufferedImage(imageRatio[0]
					* printSize, imageRatio[1] * printSize);
			createFile(chart, imagePrint);
		}

		return image;
	}

	/**
	 * Create a jpg file for the image of the chart
	 * 
	 * @param chart
	 * @param image
	 */
	private void createFile(JFreeChart chart, BufferedImage image) {

		cd("/");
		mkdir("/suzume-charts");
		try {

			ChartUtilities.saveChartAsJPEG(
					new File("/suzume-charts/" + yLabel.replaceAll(" ", "_")
							+ "-" + experimentId + ".jpg"), chart,
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
	 * This only prints charts to file without showing a window
	 */
	private void saveChart(ArrayList<Double> data) {

		XYSeries[] series = new XYSeries[1];
		series[0] = new XYSeries(yLabel);

		for (int i = 0; i < data.size(); i++) {
			series[0].add(new Double(

			i // x
					), new Double(

					data.get(i) // y
					));
		}

		JFreeChart chart = createChart(series, LINE_CHART);
		BufferedImage imagePrint = chart.createBufferedImage(imageRatio[0]
				* printSize, imageRatio[1] * printSize);
		createFile(chart, imagePrint);

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

	private void displayChart(BufferedImage image) {
		try {
			System.out.println("Enter image name\n");
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
