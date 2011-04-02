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
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class ModelStatistics extends JFrame {

	public int printSize = 200;
	public int viewSize = 100;
	public int[] imageRatio = { 5, 3 };

	private final static boolean SAVE_WHILE_PROCESSING = false;

	private String experimentId = "default";
	private String chartName = "A chart";

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

	public void plot(ArrayList<Double> data, String chartName,
			String experimentId) {
		this.experimentId = experimentId;
		this.chartName = chartName;
		XYSeries newSeries = new XYSeries(chartName);

		for (int i = 0; i < data.size(); i++) {
			newSeries.add(new Double(

			i // x
					), new Double(

					data.get(i) // y
					));
		}

		innerPane.add(new JLabel(new ImageIcon(createImage(newSeries))));
		innerPane.validate();

		saveChart(data);
	}

	public void plot(Hashtable<Double, Integer> table, String chartName,
			String experimentId) {
		this.experimentId = experimentId;
		this.chartName = chartName;
		XYSeries newSeries = new XYSeries(chartName);
		
		Enumeration e = table.keys();
		while (e.hasMoreElements()) {
			Double key = (Double) e.nextElement();
			newSeries.add(new Double(key// x
					), new Double(table.get(key) // y
					));
		}

		innerPane.add(new JLabel(new ImageIcon(createImage(newSeries))));
		innerPane.validate();

		//saveChart(table);
	}

	private JFreeChart createChart(XYSeries series) {
		XYDataset xyDataset = new XYSeriesCollection(series);

		JFreeChart chart = ChartFactory.createXYLineChart(chartName, // Title
				"Generation", // X-Axis label
				chartName, // Y-Axis label
				xyDataset, // Dataset
				PlotOrientation.VERTICAL, // Plot orientation
				false, // Show legend
				false, // Tooltips
				false); // URL

		return chart;
	}

	private BufferedImage createImage(XYSeries series) {
		return createImage(series, chartName, SAVE_WHILE_PROCESSING);
	}

	/**
	 * 
	 * @param series
	 * @param title
	 * @param printToFile
	 *            : if true, the corresponding file is created
	 * @return
	 */
	private BufferedImage createImage(XYSeries series, String title,
			boolean printToFile) {
		JFreeChart chart = createChart(series);
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

			ChartUtilities.saveChartAsJPEG(new File("/suzume-charts/"
					+ chartName.replaceAll(" ", "_") + "-" + experimentId
					+ ".jpg"), chart, imageRatio[0] * printSize, imageRatio[1]
					* printSize);
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

		XYSeries series = new XYSeries(chartName);

		for (int i = 0; i < data.size(); i++) {
			series.add(new Double(

			i // x
					), new Double(

					data.get(i) // y
					));
		}

		JFreeChart chart = createChart(series);
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
