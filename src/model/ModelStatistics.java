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

public class ModelStatistics extends Panel {
	
	private static int id = 0;
	private static int WIDTH = 500;
	private static int LENGTH = 300;
	private static int POSITION_Y = 0;
	private static int POSITION_X = 0;
	
	private BufferedImage image;
	public static String title;
	XYSeries series;
	
	public static void plot(ArrayList<Double> data, String title) {
		ModelStatistics mp = new ModelStatistics(title, data);
	}
	
	public ModelStatistics(String title, ArrayList<Double> data) {
		this.title = title;
		plot(data);
	}

	public void plot(ArrayList<Double> data) {
		
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
		switch (id++) {
		case 0:
			POSITION_Y = 0;
			POSITION_X = 0;
			break;
		case 1:
			POSITION_Y = 0;
			POSITION_X = WIDTH;
			break;
			
		case 2:
			POSITION_Y = LENGTH;
			POSITION_X = 0;
			break;
		
		case 3:
			POSITION_Y = LENGTH;
			POSITION_X = WIDTH;
			break;
		
		case 4:
			POSITION_Y = LENGTH*2;
			POSITION_X = 0;
			break;
		case 5:
			POSITION_Y = LENGTH*2;
			POSITION_X = WIDTH;
			break;
		case 6:
			POSITION_Y = 0;
			POSITION_X = WIDTH*2;
			break;
		case 7:
			POSITION_Y = LENGTH;
			POSITION_X = WIDTH*2;
			break;
		case 8:
			POSITION_Y = LENGTH*2;
			POSITION_X = WIDTH*2;
			break;
			
		default:
			POSITION_Y = 0;
			POSITION_X = 0;
			break;
		}
		frame.setLocation(POSITION_X, POSITION_Y);
	    frame.getContentPane().add(this);
	    frame.setSize(WIDTH, LENGTH);
	    frame.setVisible(true);
	}
	
	private static JFreeChart createChart(XYSeries series) {
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

	private BufferedImage createImage(XYSeries series) {
		JFreeChart chart = createChart(series);
		image = chart.createBufferedImage(WIDTH, LENGTH);

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
