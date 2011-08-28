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
	
	private ArrayList<ChartPanel> chartPanels = new ArrayList<ChartPanel>();

	public ArrayList<Pair<Double, Double>>[] data;
	
	private ChartConfiguration defaultConfiguration = new ChartConfiguration();
	
	private JPanel chartPanel;
	
	private ArrayList<DataSetChangedListener> dataSetChangedListeners = new ArrayList<DataSetChangedListener>();
	
	
	/**
	 * Create a DataPanel with the default parameters
	 * 
	 * @param data
	 * @param parent
	 */
	public DataPanel(ArrayList<Pair<Double, Double>>[] data,
			StatisticsVisualizer parent){
	
			this(data, parent, new ChartConfiguration());
		
	}
	
	/**
	 * Create a DataPanel with the specified parameters.
	 * 
	 * @param data
	 * @param title
	 * @param yLabel
	 * @param xLabel
	 * @param configName
	 * @param parent
	 * @param average
	 * @param density
	 */
	public DataPanel(
			ArrayList<Pair<Double, Double>>[] data, 
			StatisticsVisualizer parent, 
			String title,
			String yLabel, 
			String xLabel, 
			String configName, 
			boolean average, 
			boolean density){
		
		this(data, parent, new ChartConfiguration(title, xLabel, yLabel, configName, average, density));
		
	}	
	
	/**
	 * Creates a Datapanel with parameters taken from the specified configuration.
	 * 
	 * @param data
	 * @param parent
	 * @param config
	 */
	public DataPanel(
			ArrayList<Pair<Double, Double>>[] data,
			StatisticsVisualizer parent, 
			ChartConfiguration config
			){
		
		super();
		
		this.setLayout(new BorderLayout());
		this.setBorder(new EtchedBorder());
		
		this.data = data;
		this.defaultConfiguration = config;
		
		this.parent= parent;
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		final JTextField trimStartField = new JTextField(""+defaultConfiguration.getGenerationTrimStart(),5);
		final JTextField trimEndField = new JTextField(""+defaultConfiguration.getGenerationTrimEnd(),5);
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
		
		/*JButton printButton = new JButton("Print"); // deprecated
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			//	printToFile(size, )TODO
			}
		});*/
		
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
		//buttonPanel.add(printButton); //deprecated
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
		

		ChartConfiguration config = defaultConfiguration.clone();
		
		config.setGenerationTrimStart(trimStart);
		config.setGenerationTrimEnd(trimEnd);
		config.setAverage(average);

		ChartPanel chart = new ChartPanel( 
				config,
				this);
		
		chartPanels.add(chart);
		chartPanel.add(chart);
		
		revalidate();
	}
	
	public void registerDatasetChangedListener(DataSetChangedListener listener){
		dataSetChangedListeners.add(listener);
	}
	
	//TODO make useable
	private void notifyDataSetChangeListeners(){
		for(DataSetChangedListener listener : dataSetChangedListeners){
			listener.dataSetChangedListener(data);
		}
	}
	
}
