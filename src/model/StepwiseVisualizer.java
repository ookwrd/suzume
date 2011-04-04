package model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class StepwiseVisualizer extends JPanel {
	
	private static final String COUNTER_PREFIX = "Generation : ";
	
	private VisualizationConfiguration config;
	
	private JFrame frame;
	private BufferedImage image;
	private JLabel imageLabel;
	
	private JTextField pauseField;
	private JTextField intervalField;
	
	private JLabel generationCounter;
	
	private PopulationModel model;
	
	
	public StepwiseVisualizer(String title, PopulationModel model, VisualizationConfiguration config){
		
		this.model = model;
		this.config = config;
		
		frame = new JFrame();
		frame.setTitle(title);
		frame.setLayout(new BorderLayout());
		
		Dimension drawSize = model.getDimension();
		image = new BufferedImage(drawSize.width, drawSize.height, BufferedImage.TYPE_INT_RGB);
		model.draw(image.getGraphics());
		
		ImageIcon icon = new ImageIcon(image);
		imageLabel = new JLabel(icon);
		add(imageLabel);
		
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		if(config.interactiveMode){
			configureInteractiveMode();
		}
		configureGenerationCounter();
		
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	private void configureInteractiveMode(){
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
		JLabel pauseLabel = new JLabel("Pause duration:");
		pauseField = new JTextField(""+config.visualizationPause, 6);
		pauseField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(pauseField.getText());
			}
		});
		
		buttonPanel.add(pauseLabel);
		buttonPanel.add(pauseField);
		

		JLabel intervalLabel = new JLabel("Display every ");
		intervalField = new JTextField(""+config.visualizationInterval, 6);
		JLabel intervalUnit = new JLabel(" generations");
		
		buttonPanel.add(intervalLabel);
		buttonPanel.add(intervalField);
		buttonPanel.add(intervalUnit);
		
		frame.add(buttonPanel, BorderLayout.NORTH);
	}
	
	private void configureGenerationCounter(){
		
		JPanel counterPanel = new JPanel();
		counterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		generationCounter = new JLabel(COUNTER_PREFIX + "0");
		counterPanel.add(generationCounter);
		
		frame.add(counterPanel, BorderLayout.SOUTH);
	}
	
	public void update(int generation){
		
		updateCounter(generation);
		
		updateImage();
	}
	
	private void updateCounter(int generation){
		generationCounter.setText(COUNTER_PREFIX+ generation);
	}
	
	private void updateImage(){
		
		model.draw(image.getGraphics());
		imageLabel.repaint();
		
		if(config.visualizationPause > 0){
			try {
				Thread.sleep(config.visualizationPause);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
