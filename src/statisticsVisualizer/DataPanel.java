package statisticsVisualizer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;



import statisticsVisualizer.ChartPanel.PrintSize;
import tools.Pair;
import tools.Statistics;

@SuppressWarnings("serial")
public class DataPanel extends JPanel {
	
	private StatisticsVisualizer parent;

	public static final int DEFAULT_TRIM_START = 0; //TODO remove into config
	public static final int DEFAULT_TRIM_END = 5000;
	
	private ArrayList<ChartPanel> chartPanels = new ArrayList<ChartPanel>();
	
	private boolean density;//TODO remove
	private boolean average;
	
	private ArrayList<Pair<Double, Double>>[] data;
	
	private String title; //TODO remove into a default config.
	private String yLabel;
	private String xLabel;
	private String configName;
	
	private JPanel chartPanel;
	
	private ArrayList<DataSetChangedListener> dataSetChangedListeners = new ArrayList<DataSetChangedListener>();
	
	public DataPanel(ArrayList<Pair<Double, Double>>[] data,  
			String title,
			String yLabel, 
			String xLabel, 
			String configName, 
			StatisticsVisualizer parent, 
			boolean average, 
			boolean density){
		
		super();
		
		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder());
		
		this.data = data;
		this.title = title;
		this.yLabel = yLabel;
		this.xLabel = xLabel;
		this.configName = configName;
		this.average = average;
		this.density = density;
		
		this.parent= parent;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		final JTextField trimStartField = new JTextField(""+DEFAULT_TRIM_START,5);
		final JTextField trimEndField = new JTextField(""+DEFAULT_TRIM_END,5);
		final JCheckBox averageCheckBox = new JCheckBox("average");
		
		JButton createButton = new JButton("Create Chart");
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int trimStart = Integer.parseInt(trimStartField.getText());
				int trimEnd = Integer.parseInt(trimEndField.getText());
				boolean average = averageCheckBox.isSelected();
				addChart(trimStart, trimEnd, average);
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
		buttonPanel.add(averageCheckBox);
		buttonPanel.add(createButton);
		buttonPanel.add(printButton);
		buttonPanel.add(removeButton);
		
		add(buttonPanel, BorderLayout.NORTH);
		

		chartPanel = new JPanel();
		chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
		
		addChart(0, Integer.MAX_VALUE, false);
		
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
	
	public void removeChartPanel(ChartPanel panel){
		chartPanel.remove(panel);
		chartPanels.remove(panel);
		revalidate();
	}
	
	public void addChart(int trimStart, int trimEnd, boolean average){
		
		int length = data[0].size();
		
		if(length <= trimStart){
			//TODO error message
			return;
		}
		
		int trimStartAdjusted = trimStart;//TODO
		int trimEndAdjusted =  trimEnd < length ? trimEnd : length;

		ArrayList<Pair<Double, Double>>[] trimmedDataArrayList = Statistics.trimArrayLists(data, trimStartAdjusted, trimEndAdjusted);

		ChartPanel chart = new ChartPanel(trimmedDataArrayList, 
				new ChartConfiguration(title, xLabel, yLabel, configName, average, density),
				this);
		
		chartPanels.add(chart);
		chartPanel.add(chart);
		
		revalidate();
	}
	
	public void registerDatasetChangedListener(DataSetChangedListener listener){
		dataSetChangedListeners.add(listener);
	}
	
	//TOOD make useable
	private void notifyDataSetChangeListeners(){
		for(DataSetChangedListener listener : dataSetChangedListeners){
			listener.dataSetChangedListener(data);
		}
	}
	
}
