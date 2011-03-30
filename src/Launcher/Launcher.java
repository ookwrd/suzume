package Launcher;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import model.ModelConfiguration;
import model.ModelController;
import model.ModelConfiguration.AgentType;
import model.ModelConfiguration.PopulationModelType;

@SuppressWarnings("serial")
public class Launcher extends JPanel {

	private JFrame window;
	
	private ModelConfigurationPanel modelOptions;
	
	private JPanel menuBar;
	private JButton createButton;
	
	public Launcher(){
		
		window = new JFrame();
		window.setTitle("Simulation Launcher");
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
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
		
		ModelController controller = new ModelController(configuration);
		
		controller.run();
		
	}
	
	
	public static void main(String[] args){
		
		Launcher launcher = new Launcher();
		
		
	}
}
