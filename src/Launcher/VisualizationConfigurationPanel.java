package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.VisualizationConfiguration;

@SuppressWarnings("serial")
public class VisualizationConfigurationPanel extends JPanel {
	
	JCheckBox printSliceGenerationBox;
	JTextField printSliceGenerationField;
	
	public VisualizationConfigurationPanel(){
		super();
		
		ConfigurationPanelTools.configurePanel("Visualization Configuration", this);
		
		printSliceGenerationBox = ConfigurationPanelTools.addCheckBox("Print slice generation?", VisualizationConfiguration.DEFAULT_PRINT_SLICE_GENERATION, this);
		printSliceGenerationField = ConfigurationPanelTools.addField("Slice generation:", ""+VisualizationConfiguration.DEFAULT_SLICE_GENERATION, this);
		
		
		printSliceGenerationBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				printSliceGenerationField.setEnabled(printSliceGenerationBox.isSelected());
			}
		});
		printSliceGenerationField.setEnabled(VisualizationConfiguration.DEFAULT_PRINT_SLICE_GENERATION);
		
		
		ConfigurationPanelTools.makeGrid(this);
		
	}
	
	public VisualizationConfiguration getConfiguration(){
		
		return new VisualizationConfiguration(
				printSliceGenerationBox.isSelected(), 
				Integer.parseInt(printSliceGenerationField.getText().trim())
				);
		
	}

}
