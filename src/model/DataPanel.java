package model;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.ModelStatistics.PrintSize;

import tools.Pair;
import tools.Statistics;

@SuppressWarnings("serial")
public class DataPanel extends JPanel {
	
	private ModelStatistics parent;

	public static final int DEFAULT_TRIM_START = 0;
	public static final int DEFAULT_TRIM_END = 5000;
	
	private ArrayList<ChartPanel> chartPanels = new ArrayList<ChartPanel>();
	
	private boolean average;
	private boolean density;
	
	private ArrayList<Pair<Double, Double>>[] data;
	private String title;
	private String yLabel;
	private String xLabel;
	
	private JPanel chartPanel;
	
	public DataPanel(ArrayList<Pair<Double, Double>>[] data,  String title,
			String yLabel, String xLabel, String configName, ModelStatistics parent, boolean average, boolean density){
		super();
		
		this.setLayout(new BorderLayout());
		
		this.data = data;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.average = average;
		this.density = density;
		
		this.parent= parent;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		final JTextField trimStartField = new JTextField(""+DEFAULT_TRIM_START,5);
		final JTextField trimEndField = new JTextField(""+DEFAULT_TRIM_END,5);
		
		JButton trimButton = new JButton("Trim");
		trimButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int trimStart = Integer.parseInt(trimStartField.getText());
				int trimEnd = Integer.parseInt(trimEndField.getText());
				addChart(trimStart, trimEnd);
			}
		});
		
		
		JButton printButton = new JButton("Print");
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			//	printToFile(size, )TODO
			}
		});
		
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeThisPanel();
			}
		});
		
		buttonPanel.add(new JLabel("Trim from:"));
		buttonPanel.add(trimStartField);
		buttonPanel.add(new JLabel("to:"));
		buttonPanel.add(trimEndField);
		buttonPanel.add(trimButton);
		buttonPanel.add(printButton);
		buttonPanel.add(removeButton);
		
		add(buttonPanel, BorderLayout.NORTH);
		

		chartPanel = new JPanel();
		chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
		
		addChart(0, Integer.MAX_VALUE);
		
		add(chartPanel);
	}
	
	public void printToFile(PrintSize size, String location) {
		
		for(ChartPanel panel : chartPanels){
			panel.printToFile(size, location);
		}
		
	}
	
	private void removeThisPanel(){
		parent.removeDataPanel(this);
	}
	
	public void addChart(int trimStart, int trimEnd){
		
		int length = data[0].size();
		
		if(length <= trimStart){
			//TODO error message
			return;
		}
		
		int trimStartAdjusted = trimStart;//TODO
		int trimEndAdjusted =  trimEnd < length ? trimEnd : length;

		ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(data, trimStartAdjusted, trimEndAdjusted);

		ChartPanel chart = new ChartPanel(trimmedDataArrayList, 
				title, 
				average, 
				density, 
				xLabel, 
				yLabel, 
				"XXX");
		
		chartPanels.add(chart);
		chartPanel.add(chart);
		
		revalidate();
	}
	

	
}
