package Launcher;

import javax.swing.JLabel;
import javax.swing.JPanel;

import PopulationModel.GraphConfiguration;

@SuppressWarnings("serial")
public class GraphTypeConfigurationPanel extends JPanel {

	public GraphTypeConfigurationPanel(){
		add(new JLabel("PlaceHolder"));
	}
	
	public GraphConfiguration getConfiguration(){
		return null;//TODOsubPanel.getConfiguration();
	}
}
