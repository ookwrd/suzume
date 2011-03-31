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
  
	private static boolean SAVE_IMAGE_TO_FILE = true;
	private String experimentId = "default";
	private String chartName = "A chart";  
	JPanel innerPane = new JPanel();
	JScrollPane scroller = new JScrollPane(innerPane);
	
	public ModelStatistics(String title) {
		innerPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		scroller.setVerticalScrollBar(new JScrollBar());
		
		this.add(scroller, BorderLayout.CENTER);
		this.setTitle(title);
		this.setSize(new Dimension(1000,500));
	}
	
	public void display(){
		setVisible(true);
	}

	public void plot(ArrayList<Double> data, String chartName, String experimentId) {
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
	}
	
	private JFreeChart createChart(XYSeries series) {
		XYDataset xyDataset = new XYSeriesCollection(series);

		JFreeChart chart = ChartFactory.createXYLineChart(
				chartName, // Title
				"Generation", // X-Axis label
				chartName, // Y-Axis label
				xyDataset, // Dataset
				PlotOrientation.VERTICAL, // Plot orientation 
				true, // Show legend
				false, // Tooltips 
				false); // URL
		return chart;
	}

	private BufferedImage createImage(XYSeries series) {
		return createImage(series, chartName, SAVE_IMAGE_TO_FILE);
	}
	
	/**
	 * 
	 * @param series
	 * @param title 
	 * @param print : if true, the corresponded file is created
	 * @return
	 */
	private BufferedImage createImage(XYSeries series, String title, boolean print) {
		JFreeChart chart = createChart(series);
		BufferedImage image = chart.createBufferedImage(500, 300);

		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));

		// Creating the corresponding file
		if (print) {
			BufferedImage imagePrint = chart.createBufferedImage(2000, 1200);
			createFile(chart, imagePrint);
		}
		
		return image;
	}
	
	private void createFile(JFreeChart chart, BufferedImage image) {
		
		try {
			ChartUtilities.saveChartAsJPEG(new File(chartName.replaceAll(" ", "_")+"-"+experimentId+".jpg"), chart, 2000, 1200);
		} catch (IOException e) {
			e.printStackTrace();
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
