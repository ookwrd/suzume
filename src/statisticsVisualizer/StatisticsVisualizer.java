
package statisticsVisualizer;

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


import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;

import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import statisticsVisualizer.ChartPanel.PrintSize;
import tools.Pair;
import tools.ScreenImage;

@SuppressWarnings("serial")
public class StatisticsVisualizer extends JPanel {

	public static final String DEFAULT_SAVE_LOCATION = "/suzume-charts"; //TODO refactor to a configuration class
	
	public static final boolean PRINT_THUMBNAIL = true;

	public ArrayList<DataPanel> panels = new ArrayList<DataPanel>();
	
    private JFrame frame;
    private JScrollPane scrollPane;
    
    private ArrayList<DataPanel> dataPanels;
    
	private TextArea textArea;
	private JPanel clusteringPanel;
	private JPanel graphPanel;
	
	public StatisticsVisualizer(String title) {
		
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
		final JTextField trimStart = new JTextField(""+0,5);
		bottomBar.add(new JLabel("Trim from:"));
		bottomBar.add(trimStart);
		
		final JTextField trimEnd = new JTextField(""+5000,5);
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
				this,
				title, 
				label, 
				"Generations", 
				experiment, 
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
