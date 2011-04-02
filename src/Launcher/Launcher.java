package Launcher;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.ModelConfiguration;
import model.ModelController;
import model.RandomGenerator;

@SuppressWarnings("serial")
public class Launcher extends JPanel {

	private JFrame window;
	
	private RandomConfigurationPanel randomOptions;
	private ModelConfigurationPanel modelOptions;
	
	private JPanel menuBar;
	private JButton createButton;
	
	public Launcher(){
		
		window = new JFrame();
		window.setTitle("Simulation Launcher");
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		randomOptions = new RandomConfigurationPanel();
		add(randomOptions);
		
		modelOptions = new ModelConfigurationPanel();
		add(modelOptions);
		
		
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
		
		window.add(this);
		window.pack();
		window.setVisible(true);
	}
	
	private void createSimulation(){
		
		ModelConfiguration configuration = modelOptions.getConfiguration();
		
		RandomGenerator random = randomOptions.getGenerator();
	
		ModelController controller = new ModelController(configuration, random);
		
		Thread thread = new Thread(controller);
		
		thread.start();
		
	}
	
	private void createMultipleSimulation(){
	
		ModelConfiguration configuration = modelOptions.getConfiguration();
		
		RandomGenerator random = randomOptions.getGenerator();
	
		ModelController controller = new ModelController(configuration, random);
		
		Thread thread = new Thread(controller);
		
		thread.start();
		
	}
	
	
	public static void main(String[] args){
		
		new Launcher();
		
	}
}
