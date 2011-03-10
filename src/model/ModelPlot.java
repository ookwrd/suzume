//TODO where did this come from.

package model;
import java.awt.Graphics;
import java.awt.PageAttributes.OrientationRequestedType;
import java.awt.Panel;
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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ModelPlot extends Panel {
	
	private BufferedImage image;
	public static String title;
	XYSeries series;
	
	public static void plot(ArrayList<Integer> data, String title) {
		ModelPlot mp = new ModelPlot(title, data);
	}
	
	public ModelPlot(String title, ArrayList<Integer> data) {
		this.title = title;
		plot(data);
	}

	public void plot(ArrayList<Integer> data) {
		
		series = new XYSeries(title);
		
		for (int i = 0; i < data.size(); i++) {
			series.add(new Double(
					
					i // x
					), new Double(
							
							data.get(i) // y 
							)); 
		}
		createImage(series);
		
		JFrame frame = new JFrame(title);
	    frame.getContentPane().add(this);
	    frame.setSize(500, 500);
	    frame.setVisible(true);
	    
	}
	
	private static JFreeChart createChart(XYSeries series) {
		XYDataset xyDataset = new XYSeriesCollection(series);

		JFreeChart chart = ChartFactory.createXYLineChart(
				title, // Title
				title, // X-Axis label
				"Generation", // Y-Axis label
				xyDataset, // Dataset
				PlotOrientation.VERTICAL, // Plot orientation 
				true, // Show legend
				false, // Tooltips 
				false); // URL
		return chart;
	}

	private BufferedImage createImage(XYSeries series) {
		JFreeChart chart = createChart(series);
		image = chart.createBufferedImage(500, 300);

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

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	public static void main() {
		ModelController.main(null);
	}

}
