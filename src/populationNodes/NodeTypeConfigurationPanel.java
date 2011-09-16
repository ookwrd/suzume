package populationNodes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import AutoConfiguration.ConfigurationPanel;

import populationNodes.AbstractNode.NodeType;


@SuppressWarnings("serial")
public class NodeTypeConfigurationPanel extends JPanel {

	private JComboBox agentTypesBox;
	
	private ConfigurationPanel subPanel;
	
	public NodeTypeConfigurationPanel(NodeConfiguration initialValue){
		//TODO initial value
		
		System.out.println("NodeTypeConfigurationPanel" + initialValue);
		
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
		
		subPanel = NodeFactory.constructUninitializedNode((NodeType)agentTypesBox.getSelectedItem()).getConfigurationPanel();	
		add(subPanel,BorderLayout.SOUTH);
		
		revalidate();
		
		return subPanel;
	}
	
	public NodeConfiguration getConfiguration(){
		return new NodeConfiguration(subPanel.getConfiguration());
	}
	
}
