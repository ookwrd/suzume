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
import PopulationModel.PopulationModel;


@SuppressWarnings("serial")
public class RuntimeVisualizer extends JPanel {
	
	private static final String RUN_COUNTER_PREFIX = "Run : ";
	private static final String GENERATION_COUNTER_PREFIX = "Generation : ";
		
	private JFrame frame;
	
	//Layout Visualization
	private SingleStepVisualization singleStepPanel;
	
	//Timeseries Visualization
	private TimeSeriesVisualization timeSeriesPanel;
		
	//Top bar
	private JButton pausePlayButton;
	private JButton stepButton;
	
	//Bottom bar
	private JLabel generationCounter;
	private JLabel runCounter;
	JButton printSelectedButton;
	
	private boolean pauseStatus;
	private int steps;
	
	public RuntimeVisualizer(String title, int generationCount, PopulationModel model){
		
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
		
		JPanel bottomBar = new JPanel();
		bottomBar.setLayout(new BorderLayout());
		
		JPanel printPanel = new JPanel();
		printPanel.setLayout(new FlowLayout());
		
		JPanel counterPanel = new JPanel();
		counterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		printSelectedButton = new JButton("Export Selected Chart");
		printSelectedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSelected();
			}
		});
		
		printPanel.add(printSelectedButton);
		bottomBar.add(printPanel, BorderLayout.CENTER);

		runCounter = new JLabel(RUN_COUNTER_PREFIX + "0");
		counterPanel.add(runCounter);
		
		generationCounter = new JLabel(GENERATION_COUNTER_PREFIX + "0");
		counterPanel.add(generationCounter);
		
		bottomBar.add(counterPanel, BorderLayout.EAST);
		
		frame.add(bottomBar, BorderLayout.SOUTH);
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
		
	}
	
	/**
	 * Update the model that we are producing visualizations of.
	 * 
	 * @param model
	 */
	public void updateModel(PopulationModel model){
		singleStepPanel.updateModel(model);
		timeSeriesPanel.updateModel(model);
	}
	
	public void update(int run,int generation){
		
		while(true){
			
			if(steps > 0){
				steps--;
				break;
			} else if (!pauseStatus){
				break;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		updateCounter(run, generation);
		
		updateImage(run);
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
