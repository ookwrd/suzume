package Launcher;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import Agents.NodeConfiguration;
import Agents.NodeConfiguration.NodeType;
import Agents.AgentConfigurationPanel;
import Agents.AgentFactory;


@SuppressWarnings("serial")
public class AgentTypeConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	
	private AgentConfigurationPanel subPanel;
	
	public AgentTypeConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel();
		ConfigurationPanelTools.configurePanel(topPanel);
		agentTypesBox = ConfigurationPanelTools.addComboBox("Agent type:", NodeConfiguration.NodeType.values(),topPanel);
		agentTypesBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		ConfigurationPanelTools.makeGrid(topPanel);
		
		add(topPanel);
		
		
		reconfigureSubPanel();
		
	}
	
	private JPanel reconfigureSubPanel(){
		
		if(subPanel != null ) {
			remove(subPanel);
		}
		
		subPanel = AgentFactory.getConfigurationPanel((NodeType)agentTypesBox.getSelectedItem());
			
		add(subPanel);
		
		revalidate();
		
		//I really hate swing, revalidate won't update window size, so do it manually.
		JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
		if(frame != null) {
			frame.pack();
		}
		
		return subPanel;
	}
	
	public NodeConfiguration getConfiguration(){
		return subPanel.getConfiguration();
	}
	
}
