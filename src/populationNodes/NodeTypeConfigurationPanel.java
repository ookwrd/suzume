package populationNodes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import populationNodes.AbstractNode.NodeType;

import Launcher.ConfigurationPanel;


@SuppressWarnings("serial")
public class NodeTypeConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	
	private NodeConfigurationPanel subPanel;
	
	public NodeTypeConfigurationPanel(){
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setBorder(new EtchedBorder());
		
		ConfigurationPanel topPanel = new ConfigurationPanel();
		topPanel.configurePanel();
		agentTypesBox = topPanel.addComboBox("", AbstractNode.NodeType.values());
		agentTypesBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
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
