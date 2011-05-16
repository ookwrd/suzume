package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import simulation.VisualizationConfiguration;

@SuppressWarnings("serial")
public class VisualizationConfigurationPanel extends JPanel {
	
	private JCheckBox printGenerationsBox;
	private JTextField printGenerationsEachXField;
	
	private JCheckBox enableStepwiseVisualizationBox;
	private JTextField stepwiseVisualizationIntervalField;
	private JTextField stepwiseVisualizationPauseField;
	
	public VisualizationConfigurationPanel(){
		super();
		
		ConfigurationPanelTools.configurePanel("Visualization Configuration", this);
		
		printGenerationsBox = ConfigurationPanelTools.addCheckBox("Print generation count?", VisualizationConfiguration.DEFAULT_PRINT_GENERATIONS, this);
		printGenerationsEachXField = ConfigurationPanelTools.addField("Print each X generations", ""+VisualizationConfiguration.DEFAULT_PRINT_GENERATIONS_EACH_X, this);
		printGenerationsBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				printGenerationsEachXField.setEnabled(printGenerationsBox.isSelected());
			}
		});
		printGenerationsEachXField.setEnabled(VisualizationConfiguration.DEFAULT_PRINT_GENERATIONS);
		
		
		enableStepwiseVisualizationBox = ConfigurationPanelTools.addCheckBox("Enable continuous visualiaztion?", VisualizationConfiguration.DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION, this);
		stepwiseVisualizationIntervalField = ConfigurationPanelTools.addField("Visualization interval", ""+VisualizationConfiguration.DEFAULT_VISUALIZATION_INTERVAL, this);
		stepwiseVisualizationPauseField = ConfigurationPanelTools.addField("Pause after visualization", ""+VisualizationConfiguration.DEFAULT_VISUALIZATION_PAUSE, this);
		enableStepwiseVisualizationBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean enabled = enableStepwiseVisualizationBox.isSelected();
				stepwiseVisualizationIntervalField.setEnabled(enabled);
				stepwiseVisualizationPauseField.setEnabled(enabled);
			}
		});
		stepwiseVisualizationIntervalField.setEnabled(VisualizationConfiguration.DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION);
		stepwiseVisualizationPauseField.setEnabled(VisualizationConfiguration.DEFAULT_ENABLE_CONTINUOUS_VISUALIZATION);
		
		ConfigurationPanelTools.makeGrid(this);
		
	}
	
	public VisualizationConfiguration getConfiguration(){
		
		return new VisualizationConfiguration(
				printGenerationsBox.isSelected(),
				Integer.parseInt(printGenerationsEachXField.getText().trim()),
				enableStepwiseVisualizationBox.isSelected(),
				Integer.parseInt(stepwiseVisualizationIntervalField.getText().trim()),
				Integer.parseInt(stepwiseVisualizationPauseField.getText().trim())
				);
		
	}

}
