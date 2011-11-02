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
public class Launcher extends JFrame {
	
	private ConfigurationPanel modelOptions;
	
	public Launcher(){
		setTitle("Suzume: Simulation Launcher");
		
		JPanel internalPanel = new JPanel();
		internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
		
		modelOptions = new ModelController().getConfigurationPanel();
		internalPanel.add(modelOptions);
			
		JPanel menuBar = new JPanel();
		menuBar.setLayout(new FlowLayout());
		
		JButton createButton = new JButton("Launch Simulation");
		createButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				createSimulation();
			}
		});
		menuBar.add(createButton);
		internalPanel.add(menuBar);
		
		JScrollPane scrollPane = new JScrollPane(internalPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);//Faster scrolling.
		
		add(scrollPane);
		pack();
		setVisible(true);
	}
	
	/**
	 * Creates a simulation instance based on the current parameter settings and sets it running in a new thread.
	 */
	private void createSimulation(){
		ModelController controller = new ModelController(modelOptions.getConfiguration());	
		Thread thread = new Thread(controller);
		thread.start();
	}
	
	public static void main(String[] args){
		new Launcher();
	}
}
