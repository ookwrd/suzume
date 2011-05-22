package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import runTimeVisualization.Visualizable.VisualizationType;
import simulation.PopulationModel;
import simulation.VisualizationConfiguration;

@SuppressWarnings("serial")
public class StepwiseVisualizer extends JPanel {
	
	private static final String RUN_COUNTER_PREFIX = "Run : ";
	private static final String GENERATION_COUNTER_PREFIX = "Generation : ";
	
	private VisualizationConfiguration config;
	private PopulationModel model;	
	
	private Dimension layoutBaseDimension = new Dimension(5,5);
	private Dimension verticalBaseDimension = new Dimension(1,1);
	
	private JFrame frame;
	
	//Main Visualization
	private Graphics layoutGraphics;
	private JLabel layoutImageLabel;
	
	//Timeseries Visualization
	private JPanel timeSeriesPanel;
	
	private BufferedImage verticalImage;
	private Graphics verticalGraphics;
	private JLabel verticalImageLabel;
	
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
	private int generationCount;
	
	private int runAtLastUpdate = 0;
	
	//private VisualizationType visualizationType = VisualizationType.vertical;
	
	public StepwiseVisualizer(String title, int generationCount, PopulationModel model, VisualizationConfiguration config){
		
		this.model = model;
		this.config = config;
		
		frame = new JFrame();
		frame.setTitle(title);
		frame.setLayout(new BorderLayout());
		
		this.generationCount = generationCount;
		
		//Layout visualization
		Dimension drawSize = model.getDimension(layoutBaseDimension,VisualizationType.layout);
		BufferedImage layoutImage = new BufferedImage(drawSize.width, drawSize.height, BufferedImage.TYPE_INT_RGB);
		layoutGraphics = layoutImage.getGraphics();
		model.draw(layoutBaseDimension,VisualizationType.layout,layoutImage.getGraphics());
		
		ImageIcon icon = new ImageIcon(layoutImage);
		layoutImageLabel = new JLabel(icon);
		add(layoutImageLabel);
		
		//TimeSeries visualization
		timeSeriesPanel = new JPanel();
		timeSeriesPanel.setLayout(new BoxLayout(timeSeriesPanel, BoxLayout.Y_AXIS));
		configureNewTimeseries();
		add(timeSeriesPanel);
		
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		configureTopBar();
		
		configureBottomBar();
		
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private void configureTopBar(){
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		//Pause Field
		JLabel pauseLabel = new JLabel("Pause duration:");
		pauseField = new JTextField(""+config.visualizationPause, 6);
		pauseField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config.visualizationPause = Integer.parseInt(pauseField.getText().trim());
			}
		});
		buttonPanel.add(pauseLabel);
		buttonPanel.add(pauseField);
		
		//Interval Field
		JLabel intervalLabel = new JLabel("Display every ");
		intervalField = new JTextField(""+config.visualizationInterval, 6);
		intervalField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				config.visualizationInterval = Integer.parseInt(intervalField.getText().trim());	
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
	
	private void configureNewTimeseries(){

		Dimension drawSize = model.getDimension(verticalBaseDimension, VisualizationType.vertical);
		verticalImage = new BufferedImage(drawSize.width*generationCount, drawSize.height, BufferedImage.TYPE_INT_RGB);
		verticalGraphics = verticalImage.getGraphics();
		model.draw(verticalBaseDimension, VisualizationType.vertical,verticalImage.getGraphics());
		
		ImageIcon icon = new ImageIcon(verticalImage);
		verticalImageLabel = new JLabel(icon);
		timeSeriesPanel.add(verticalImageLabel);
		
	}
	
	/**
	 * Update the model that we are producing visualizations of.
	 * 
	 * @param model
	 */
	public void updateModel(PopulationModel model){
		this.model = model;
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
		
		if( generation % config.visualizationInterval != 0){
			return;
		}
		
		updateCounter(run, generation);
		
		updateImage(run);
				
		if(config.visualizationPause > 0){
			try {
				Thread.sleep(config.visualizationPause);
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
		model.draw(layoutBaseDimension, VisualizationType.layout, layoutGraphics.create());
		layoutImageLabel.repaint();
		
		//Do we reset the visualization?
		if(this.runAtLastUpdate < run){
			configureNewTimeseries();
			runAtLastUpdate = run;
		}
		
		//Timeseries visualization
		model.draw(verticalBaseDimension, VisualizationType.vertical, verticalGraphics.create());
		verticalGraphics.translate(verticalBaseDimension.height, 0);
		verticalImageLabel.repaint();
		
	}

}
