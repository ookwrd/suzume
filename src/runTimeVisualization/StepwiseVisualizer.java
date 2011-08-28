package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.PopulationModel;

import simulation.VisualizationConfiguration;

@SuppressWarnings("serial")
public class StepwiseVisualizer extends JPanel {
	
	private static final String RUN_COUNTER_PREFIX = "Run : ";
	private static final String GENERATION_COUNTER_PREFIX = "Generation : ";
	
	private VisualizationConfiguration config;
	private PopulationModel model;	
		
	private JFrame frame;
	
	//Layout Visualization
	private SingleStepVisualization singleStepPanel;
	
	//Timeseries Visualization
	private TimeSeriesVisualization timeSeriesPanel;
		
	//Top bar
	private JTextField pauseField;
	private JTextField intervalField;
	private JButton pausePlayButton;
	private JButton stepButton;
	
	//Bottom bar
	private JLabel generationCounter;
	private JLabel runCounter;
	
	private boolean pauseStatus;
	private int steps;
	
	public StepwiseVisualizer(String title, int generationCount, PopulationModel model, VisualizationConfiguration config){
		
		this.model = model;
		this.config = config;
		
		setLayout(new BorderLayout());
		
		//Layout visualization
		singleStepPanel = new SingleStepVisualization(model);
		add(singleStepPanel, BorderLayout.WEST);
		
		//TimeSeries visualization
		timeSeriesPanel = new TimeSeriesVisualization(model, generationCount);
		add(timeSeriesPanel, BorderLayout.CENTER);
		
		frame = new JFrame();
		frame.setTitle(title);
		frame.setLayout(new BorderLayout());
		
		configureTopBar();
		configureBottomBar();
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private void configureTopBar(){
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		//Pause Field
		JLabel pauseLabel = new JLabel("Pause duration:");
		pauseField = new JTextField(""+config.getParameter(VisualizationConfiguration.PAUSE_AFTER_VISUALIZATION).getInteger(), 6);
		pauseField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config.setParameter(VisualizationConfiguration.PAUSE_AFTER_VISUALIZATION, new ConfigurationParameter(Integer.parseInt(pauseField.getText().trim())));;
			}
		});
		buttonPanel.add(pauseLabel);
		buttonPanel.add(pauseField);
		
		//Interval Field
		JLabel intervalLabel = new JLabel("Display every ");
		intervalField = new JTextField(""+config.getParameter(VisualizationConfiguration.VISUALIZATION_INTERVAL).getInteger(), 6);
		intervalField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config.setParameter(VisualizationConfiguration.VISUALIZATION_INTERVAL,new ConfigurationParameter(Integer.parseInt(intervalField.getText().trim())));	
			}
		});
		JLabel intervalUnit = new JLabel(" generations");
		buttonPanel.add(intervalLabel);
		buttonPanel.add(intervalField);
		buttonPanel.add(intervalUnit);
		
		//Pause/Play button
		pausePlayButton = new JButton("Pause");
		pausePlayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pausePlayButton.setText(pauseStatus?"Pause":"Play");
				pauseStatus = !pauseStatus;
				steps = 0;
				
			}
		});
		buttonPanel.add(pausePlayButton);
		
		//Step
		stepButton = new JButton(">>");
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				steps += 1;
			}
		});
		buttonPanel.add(stepButton);
		
		frame.add(buttonPanel, BorderLayout.NORTH);
	}
	
	private void configureBottomBar(){
		
		JPanel counterPanel = new JPanel();
		counterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton printCurrentGenerationButton = new JButton("Print current generation");
		printCurrentGenerationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.print();
			}
		});
		counterPanel.add(printCurrentGenerationButton);

		runCounter = new JLabel(RUN_COUNTER_PREFIX + "0");
		counterPanel.add(runCounter);
		
		generationCounter = new JLabel(GENERATION_COUNTER_PREFIX + "0");
		counterPanel.add(generationCounter);
		
		frame.add(counterPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Update the model that we are producing visualizations of.
	 * 
	 * @param model
	 */
	public void updateModel(PopulationModel model){
		this.model = model;
		singleStepPanel.updateModel(model);
		timeSeriesPanel.updateModel(model);
	}
	
	public void update(int run,int generation){
		
		while(true){
			
			if(steps > 0){
				steps--;
				break;
			} else if ( !pauseStatus){
				break;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		if( generation % config.getParameter(VisualizationConfiguration.VISUALIZATION_INTERVAL).getInteger() != 0){
			return;
		}
		
		updateCounter(run, generation);
		
		updateImage(run);
				
		if(config.getParameter(VisualizationConfiguration.PAUSE_AFTER_VISUALIZATION).getInteger() > 0){
			try {
				Thread.sleep(config.getParameter(VisualizationConfiguration.PAUSE_AFTER_VISUALIZATION).getInteger());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void updateCounter(int run, int generation){
		runCounter.setText(RUN_COUNTER_PREFIX + run);
		generationCounter.setText(GENERATION_COUNTER_PREFIX + generation);
	}
	
	private void updateImage(int run){
		//Current Status visualization
		singleStepPanel.updateImage();
		timeSeriesPanel.updateImage(run);
	}

}
