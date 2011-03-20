//TODO where did this come from.

package model;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class ModelStatistics extends JFrame {

	JPanel innerPane = new JPanel();
	
	public ModelStatistics(String title) {
		innerPane.setLayout(new FlowLayout());
		this.add(innerPane);
		this.setTitle(title);
		this.setSize(new Dimension(1040,660));
	}
	
	public void display(){
		setVisible(true);
	}

	public void plot(ArrayList<Double> data, String title) {
	
		XYSeries newSeries = new XYSeries(title);
		
		for (int i = 0; i < data.size(); i++) {
			newSeries.add(new Double(
					
					i // x
					), new Double(
							
							data.get(i) // y 
							)); 
		}
		
		innerPane.add(new JLabel(new ImageIcon(createImage(newSeries, title))));
		innerPane.validate();
	}
	
	private static JFreeChart createChart(XYSeries series, String title) {
		XYDataset xyDataset = new XYSeriesCollection(series);

		JFreeChart chart = ChartFactory.createXYLineChart(
				title, // Title
				"Generation", // X-Axis label
				title, // Y-Axis label
				xyDataset, // Dataset
				PlotOrientation.VERTICAL, // Plot orientation 
				true, // Show legend
				false, // Tooltips 
				false); // URL
		return chart;
	}

	private BufferedImage createImage(XYSeries series, String title) {
		JFreeChart chart = createChart(series, title);
		BufferedImage image = chart.createBufferedImage(500, 300);

		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));

		// Creating the corresponding file
		
		// JFreeChart also includes a class named ChartUtilities that provides
		// several methods for saving charts to files or writing them out to
		// streams in JPEG or PNG format. For example, the following piece of
		// code can export a chart to a JPEG:
		// ChartUtilities.saveChartAsJPEG(new File("chart.jpg"), chart, 500,
		// 300);

		return image;
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
