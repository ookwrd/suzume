package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class RuntimeVisualizer extends JPanel {
	
	public interface Stoppable{
		public void stopRequest();
	}
	
	private static final String RUN_COUNTER_PREFIX = "Run : ";
	private static final String GENERATION_COUNTER_PREFIX = "Generation : ";
		
	private JFrame frame;
	
	private GeographicVisualization singleStepPanel;
	private TimeSeriesVisualization timeSeriesPanel;
	
	//Bottom bar
	private JButton pausePlayButton;
	private JButton stepButton;
	private JLabel generationCounter;
	private JLabel runCounter;
	private JButton printSelectedButton;
	
	private boolean pauseStatus;
	private int steps;
	
	/**
	 * @param title
	 * @param generationCount
	 * @param model
	 * @param stoppable
	 */
	public RuntimeVisualizer(String title, int generationCount, Visualizable model, final Stoppable stoppable){
		setLayout(new BorderLayout());		
		frame = new JFrame();
		frame.setTitle(title);
		frame.setLayout(new BorderLayout());
		
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		if(stoppable != null){
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e){
					stoppable.stopRequest();
				}
			});
		}
		
		configureBottomBar();
		
		//Geographic visualization
		singleStepPanel = new GeographicVisualization(model, printSelectedButton);
		add(singleStepPanel, BorderLayout.WEST);
		
		//TimeSeries visualization
		timeSeriesPanel = new TimeSeriesVisualization(model, generationCount, printSelectedButton);
		add(timeSeriesPanel, BorderLayout.CENTER);
		
		frame.add(this, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void configureBottomBar(){
		
		JPanel bottomBar = new JPanel();
		bottomBar.setLayout(new BorderLayout());
		
		//Pause/Play button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
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
		
		bottomBar.add(buttonPanel,BorderLayout.WEST);
		
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
	
	public void update(int run,int generation, Visualizable model){
		
		while(true){//TODO a better implementation of pause.	
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
		
		updateImage(run, model);
	}
	
	
	private void updateCounter(int run, int generation){
		runCounter.setText(RUN_COUNTER_PREFIX + run);
		generationCounter.setText(GENERATION_COUNTER_PREFIX + generation);
	}
	
	private void updateImage(int run, Visualizable model){
		//Current Status visualization
		singleStepPanel.updateImage(model);
		timeSeriesPanel.updateImage(run, model);
	}
	
	@Override
	public void finalize() throws Throwable{
		System.out.println("Runtime Visualizer Being finalized");
		super.finalize();
	}

}
