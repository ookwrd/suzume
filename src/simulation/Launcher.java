package simulation;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import autoconfiguration.ConfigurationPanel;



@SuppressWarnings("serial")
public class Launcher extends JPanel {

	private JFrame window;
	
	private ConfigurationPanel randomOptions;
	private ConfigurationPanel modelOptions;
	
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
		RandomGenerator random = new RandomGenerator(randomOptions.getConfiguration());
	
		ModelController controller = new ModelController(modelOptions.getConfiguration(), random);
		
		Thread thread = new Thread(controller);
		thread.start();
	}
	
	public static void main(String[] args){
		new Launcher();
	}
}
