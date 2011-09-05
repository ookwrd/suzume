package Launcher;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import AutoConfiguration.BasicConfigurationPanel;

import simulation.ModelController;
import simulation.RandomGenerator;
import simulation.VisualizationConfiguration;

@SuppressWarnings("serial")
public class Launcher extends JPanel {

	private JFrame window;
	
	private BasicConfigurationPanel randomOptions;
	private BasicConfigurationPanel modelOptions;
	private BasicConfigurationPanel visualOptions;
	
	private JPanel menuBar;
	private JButton createButton;
	
	public Launcher(){
		
		window = new JFrame();
		window.setTitle("Simulation Launcher");
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		modelOptions = new ModelController().getConfigurationPanel();
		add(modelOptions);

		randomOptions = new RandomGenerator().getConfigurationPanel();
		add(randomOptions);
		
		visualOptions = new VisualizationConfiguration().getConfigurationPanel();
		add(visualOptions);
			
		menuBar = new JPanel();
		menuBar.setLayout(new FlowLayout());
		
		createButton = new JButton("Run Simulation");
		createButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				createSimulation();
			}
		});
		menuBar.add(createButton);
		
		add(menuBar);
		
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);//Faster scrolling.
		
		window.add(scrollPane);
		window.pack();
		window.setVisible(true);
	}
	
	/**
	 * Creates a simulation instance based on the current parameter settings and sets it running in a new thread.
	 */
	private void createSimulation(){
		VisualizationConfiguration visualizationConfiguration = new VisualizationConfiguration(visualOptions.getConfiguration());
		RandomGenerator random = new RandomGenerator(randomOptions.getConfiguration());
	
		ModelController controller = new ModelController(modelOptions.getConfiguration(), visualizationConfiguration, random);
		
		Thread thread = new Thread(controller);
		thread.start();
	}
	
	public static void main(String[] args){
		new Launcher();
	}
}
