
package model;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import model.ChartPanel.PrintSize;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import tools.Pair;
import tools.ScreenImage;

@SuppressWarnings("serial")
public class ModelStatistics extends JPanel {

	public static final String DEFAULT_SAVE_LOCATION = "/suzume-charts"; //TODO refactor to a configuration class
	
	public enum PlotType {TIMESERIES, DENSITY}
	
	private boolean trim = true;
	//public static Integer[][] TRIM_INTERVALS = {{2000,Integer.MAX_VALUE},{0,1000},{1000,2000},{2000,3000},{3000,4000},{4000,5000},{9000,11000},{15000,16000},{19000,21000}};
	public static Integer[][] TRIM_INTERVALS = { { 2000, Integer.MAX_VALUE },
			{ 0, 1000 }, { 1000, 2000 }, { 2000, 3000 }, { 3000, 4000 },
			{ 4000, 5000 }, { 5000, 6000 }, { 6000, 7000 }, { 7000, 8000 },
			{ 8000, 9000 }, { 9000, 10000 }, { 10000, 11000 },
			{ 11000, 12000 }, { 12000, 13000 }, { 13000, 14000 },
			{ 14000, 15000 }, { 15000, 16000 }, { 16000, 17000 },
			{ 17000, 18000 }, { 18000, 19000 }, { 19000, 20000 },
			{ 20000, 21000 }, { 21000, 22000 }, { 22000, 23000 },
			{ 23000, 24000 }, { 24000, 25000 }, { 25000, 26000 },
			{ 26000, 27000 }, { 27000, 28000 }, { 28000, 29000 },
			{ 29000, 30000 }, { 30000, 31000 }, { 31000, 32000 },
			{ 32000, 33000 }, { 33000, 34000 }, { 34000, 35000 },
			{ 35000, 36000 }, { 36000, 37000 }, { 37000, 38000 },
			{ 38000, 39000 }, { 39000, 40000 }, { 40000, 41000 },
			{ 41000, 42000 }, { 42000, 43000 }, { 43000, 44000 },
			{ 44000, 45000 }, { 45000, 46000 }, { 46000, 47000 },
			{ 47000, 48000 }, { 48000, 49000 }, { 49000, 50000 },
			{ 50000, 51000 }, { 51000, 52000 }, { 52000, 53000 },
			{ 53000, 54000 }, { 54000, 55000 }, { 55000, 56000 },
			{ 56000, 57000 }, { 57000, 58000 }, { 58000, 59000 },
			{ 59000, 60000 }, { 60000, 61000 }, { 61000, 62000 },
			{ 62000, 63000 }, { 63000, 64000 }, { 64000, 65000 },
			{ 65000, 66000 }, { 66000, 67000 }, { 67000, 68000 },
			{ 68000, 69000 }, { 69000, 70000 }, { 70000, 71000 },
			{ 71000, 72000 }, { 72000, 73000 }, { 73000, 74000 },
			{ 74000, 75000 }, { 75000, 76000 }, { 76000, 77000 },
			{ 77000, 78000 }, { 78000, 79000 }, { 79000, 80000 },
			{ 80000, 81000 }, { 81000, 82000 }, { 82000, 83000 },
			{ 83000, 84000 }, { 84000, 85000 }, { 85000, 86000 },
			{ 86000, 87000 }, { 87000, 88000 }, { 88000, 89000 },
			{ 89000, 90000 }, { 90000, 91000 }, { 91000, 92000 },
			{ 92000, 93000 }, { 93000, 94000 }, { 94000, 95000 },
			{ 95000, 96000 }, { 96000, 97000 }, { 97000, 98000 },
			{ 98000, 99000 }, { 99000, 100000 }, { 100000, 101000 } }; // Genotypes
																		// vs
																		// time
	public static final boolean PRINT_THUMBNAIL = true;

	public ArrayList<DataPanel> panels = new ArrayList<DataPanel>();
	
    private JFrame frame;
    private JScrollPane scrollPane;
    
    private ArrayList<DataPanel> dataPanels;
    
	private TextArea textArea;
	private JPanel clusteringPanel;
	private JPanel graphPanel;
	
	public ModelStatistics(String title) {
		
		//Gets rid of all the fancy graph settings.
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		
		frame = new JFrame();
		frame.setTitle(title);
		
		setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		scrollPane = new JScrollPane(this);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		
		frame.add(scrollPane);
		
		this.dataPanels = new ArrayList<DataPanel>();
		
		initializeTopBar();

		initializeBottomBar();
		
		clusteringPanel = new JPanel();
		clusteringPanel.setLayout(new BorderLayout());
		add(clusteringPanel);
		
	}
	
	private void initializeTopBar(){
		
		JPanel topBar = new JPanel();
		topBar.setLayout(new FlowLayout());
		
		JButton importButton = new JButton("Import Dataset");
		importButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				importDataset();
			}
		});
		
		topBar.add(importButton);
		
		frame.add(topBar, BorderLayout.NORTH);
	}
	
	private void initializeBottomBar(){
		
		JPanel bottomBar = new JPanel();
		bottomBar.setLayout(new FlowLayout());
		
		//Trim controls.
		final JTextField trimStart = new JTextField(""+DataPanel.DEFAULT_TRIM_START,5);
		bottomBar.add(new JLabel("Trim from:"));
		bottomBar.add(trimStart);
		
		final JTextField trimEnd = new JTextField(""+DataPanel.DEFAULT_TRIM_END,5);
		bottomBar.add(new JLabel("to:"));
		bottomBar.add(trimEnd);
		
		JButton trimButton = new JButton("Trim all");
		trimButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				trimAll(Integer.parseInt(trimStart.getText()), Integer.parseInt(trimEnd.getText()));
			}
		});
		bottomBar.add(trimButton);
		
		
		//Print controls
		final JTextField saveDestination = new JTextField(DEFAULT_SAVE_LOCATION);
		bottomBar.add(new JLabel("Output location:"));
		bottomBar.add(saveDestination);
		
		final JCheckBox smallCheck = new JCheckBox("small");
		final JCheckBox mediumCheckBox = new JCheckBox("medium");
		final JCheckBox largeCheckBox = new JCheckBox("large");
		final JCheckBox extraLargeCheckBox = new JCheckBox("ExtraLarge");
		
		largeCheckBox.setSelected(true);
		
		bottomBar.add(smallCheck);
		bottomBar.add(mediumCheckBox);
		bottomBar.add(largeCheckBox);
		bottomBar.add(extraLargeCheckBox);
		
		JButton saveButton = new JButton("Print all");
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(smallCheck.isSelected()){
					printAll(PrintSize.SMALL, saveDestination.getText());
				}
				if(mediumCheckBox.isSelected()){
					printAll(PrintSize.MEDIUM, saveDestination.getText());
				}
				if(largeCheckBox.isSelected()){
					printAll(PrintSize.LARGE, saveDestination.getText());
				}
				if(extraLargeCheckBox.isSelected()){
					System.out.println("Here");
					printAll(PrintSize.EXTRA_LARGE, saveDestination.getText());
				}
			}
		});
		bottomBar.add(saveButton);

		
		//Export controls
		JButton exportButton = new JButton("Export all");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exportAll(saveDestination.getText());
			}
		});
		bottomBar.add(exportButton);
		
		
		frame.add(bottomBar, BorderLayout.SOUTH);
		
	}

	public void display() {
		
		frame.pack();
		frame.setVisible(true);
		
		if(PRINT_THUMBNAIL){
			printAll(PrintSize.SMALL, DEFAULT_SAVE_LOCATION);
		}
	}
	
	public void addDataSeries(ArrayList<Pair<Double, Double>>[] table, 
			String title,
			String label, 
			String experiment, 
			boolean density){

		DataPanel dataPanel = new DataPanel(table, 
				title, 
				label, 
				"Generations", 
				experiment, 
				this,
				false,
				density);
		
		addDataPanel(dataPanel);

	}
	
	public void addDataPanel(DataPanel chartPanel){
		
		add(chartPanel);
		dataPanels.add(chartPanel);
		
		validate();
		frame.validate();

	}
	
	private void addConsolePanel(String text) {
		
		JPanel consolePanel = new JPanel();
		textArea = new TextArea(text);
		textArea.setEditable(false);
		consolePanel.add(textArea);
		clusteringPanel.add(consolePanel, BorderLayout.SOUTH);
		
		validate();
		frame.validate();
		
	}
	
	private void addGraphPanel(BasicVisualizationServer<Integer, String> vv, String title) {
		
		graphPanel = new JPanel();
		graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));
		clusteringPanel.add(graphPanel, BorderLayout.NORTH);
		JTextArea t = new JTextArea(title);
		t.setEditable(false);
		graphPanel.add(t);
		graphPanel.add(vv);
		validate();
		frame.validate();
	}
	
	public void removeDataPanel(DataPanel panel){
		
		remove(panel);
		dataPanels.remove(panel);
		
		revalidate();

	}
	
	public void addGraph(BasicVisualizationServer<Integer, String> vv, String title) {
		
		if (this.graphPanel==null) addGraphPanel(vv, title); // lazy
		else {
			JTextArea t = new JTextArea(title);
			t.setEditable(false);
			graphPanel.add(t);
			graphPanel.add(vv);
			validate();
			frame.validate();
		}
	}
	
	public void updateConsoleText(String text) {
		
		if (textArea==null) {// lazy
			addConsolePanel(text);
		} else {
			textArea.setText(text);
		}
	}

	public void saveGraphs(String imageFileName) {

		   BufferedImage bufImage = ScreenImage.createImage((JComponent) graphPanel);
		   try {
		       File outFile = new File(DEFAULT_SAVE_LOCATION+"/"+imageFileName+".png");
		       ImageIO.write(bufImage, "png", outFile);
		       System.out.println("wrote image to " +DEFAULT_SAVE_LOCATION+"/"+ imageFileName +".png");
		   } catch (Exception e) {
		       System.out.println("writeToImageFile(): " + e.getMessage());
		   }
	}
	
	private void trimAll(int start, int end){
		for(DataPanel panel : dataPanels){
			panel.addChart(start, end, false);
		}
	}
	
	private void printAll(PrintSize size, String location){
		for(DataPanel panel : dataPanels){
			panel.printToFile(size, location);
		}
	}
	
	private void exportAll(String location){
		for(DataPanel panel : dataPanels){
			//TODO
		}
	}
	
	private void importDataset(){
		//TODO
		System.out.println("Should be importing data set right now.");
	}

}
