package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import AutoConfiguration.ConfigurationParameter;
import PopulationModel.PopulationModel;

import simulation.VisualizationConfiguration;

@SuppressWarnings("serial")
public class RuntimeVisualizer extends JPanel {
	
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
	JButton printSelectedButton;
	
	private boolean pauseStatus;
	private int steps;
	
	public RuntimeVisualizer(String title, int generationCount, PopulationModel model, VisualizationConfiguration config){
		
		this.model = model;
		this.config = config;
		
		setLayout(new BorderLayout());
		
		frame = new JFrame();
		frame.setTitle(title);
		frame.setLayout(new BorderLayout());
		
		configureTopBar();
		configureBottomBar();
		
		//Layout visualization
		singleStepPanel = new SingleStepVisualization(model, printSelectedButton);
		add(singleStepPanel, BorderLayout.WEST);
		
		//TimeSeries visualization
		timeSeriesPanel = new TimeSeriesVisualization(model, generationCount, printSelectedButton);
		add(timeSeriesPanel, BorderLayout.CENTER);
		
		
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
				if(pauseStatus){
					pausePlayButton.setText("Pause");
					stepButton.setEnabled(false);
				}else{
					pausePlayButton.setText("Play");
					stepButton.setEnabled(true);
				}
				pauseStatus = !pauseStatus;
				steps = 0;
				
			}
		});
		buttonPanel.add(pausePlayButton);
		
		//Step
		stepButton = new JButton("Step");
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				steps += 1;
			}
		});
		buttonPanel.add(stepButton);
		stepButton.setEnabled(false);
		
		frame.add(buttonPanel, BorderLayout.NORTH);
	}
	
	private void configureBottomBar(){
		
		JPanel counterPanel = new JPanel();
		counterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		printSelectedButton = new JButton("Export Selected Chart");
		printSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSelected();
			}
		});
		counterPanel.add(printSelectedButton);

		runCounter = new JLabel(RUN_COUNTER_PREFIX + "0");
		counterPanel.add(runCounter);
		
		generationCounter = new JLabel(GENERATION_COUNTER_PREFIX + "0");
		counterPanel.add(generationCounter);
		
		frame.add(counterPanel, BorderLayout.SOUTH);
	}
	
	private void saveSelected(){
		BufferedImage image = singleStepPanel.getSelected();
		
		if(image == null){
			image = timeSeriesPanel.getSelected();
		}
		
		if(image == null){
			System.out.println("No chart selected");
			return;
		}
		
		final JFileChooser fc = new JFileChooser();
		
		int ret = fc.showSaveDialog(this);
		
		switch(ret){
		case JFileChooser.APPROVE_OPTION:
			
			File file = fc.getSelectedFile();
		    try {
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    return;
		    
		default:
		}
		
		System.out.println("Is it null? "+ image);
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
