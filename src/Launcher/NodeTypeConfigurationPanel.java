package Launcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import Agents.NodeConfiguration;
import Agents.NodeConfiguration.NodeType;
import Agents.NodeConfigurationPanel;
import Agents.NodeFactory;


@SuppressWarnings("serial")
public class NodeTypeConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	
	private NodeConfigurationPanel subPanel;
	
	public NodeTypeConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setBorder(new EtchedBorder());
		
		JPanel topPanel = new JPanel();
		ConfigurationDisplayTools.configurePanel(topPanel);
		agentTypesBox = ConfigurationDisplayTools.addComboBox("", NodeConfiguration.NodeType.values(),topPanel);
		agentTypesBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		ConfigurationDisplayTools.makeGrid(topPanel);
		
		add(topPanel);
		
		reconfigureSubPanel();
		
	}
	
	private JPanel reconfigureSubPanel(){
		
		if(subPanel != null ) {
			remove(subPanel);
		}
		
		subPanel = NodeFactory.getConfigurationPanel((NodeType)agentTypesBox.getSelectedItem());
			
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
