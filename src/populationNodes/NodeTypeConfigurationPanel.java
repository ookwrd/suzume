package populationNodes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import AutoConfiguration.BasicConfigurationPanel;

import populationNodes.AbstractNode.NodeType;


@SuppressWarnings("serial")
public class NodeTypeConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	
	private BasicConfigurationPanel subPanel;
	
	public NodeTypeConfigurationPanel(NodeConfiguration initialValue){
		//TODO initial value
		
		setLayout(new BorderLayout());
		setBorder(new EtchedBorder());
		
		agentTypesBox = new JComboBox(AbstractNode.NodeType.values());
		agentTypesBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
		add(agentTypesBox,BorderLayout.NORTH);
		
		reconfigureSubPanel();
	}
	
	private JPanel reconfigureSubPanel(){
		
		if(subPanel != null ) {
			remove(subPanel);
		}
		
		subPanel = NodeFactory.constructUninitializedNode((NodeType)agentTypesBox.getSelectedItem()).getConfigurationPanel();//new NodeConfigurationPanel((NodeType)agentTypesBox.getSelectedItem());
			
		add(subPanel,BorderLayout.SOUTH);
		
		revalidate();
		
		//I really hate swing, revalidate won't update window size, so do it manually.
		JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
		if(frame != null) {
			frame.pack();
		}
		
		return subPanel;
	}
	
	public NodeConfiguration getConfiguration(){
		return new NodeConfiguration(subPanel.getConfiguration());
	}
	
}
