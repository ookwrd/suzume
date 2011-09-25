package populationNodes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import AutoConfiguration.ConfigurationPanel;
import AutoConfiguration.ConfigurationParameter;

import populationNodes.AbstractNode.NodeType;

@SuppressWarnings("serial")
public class NodeTypeConfigurationPanel extends JPanel {

	public static final String NODE_TYPE = "Node type";
	
	private JComboBox agentTypesBox;
	
	private ConfigurationPanel subPanel;
	
	public NodeTypeConfigurationPanel(NodeConfiguration initialValue){
		//TODO initial value
		
		System.out.println("NodeTypeConfigurationPanel" + initialValue.getClass());
		
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
		
		//agentTypesBox.setSelectedItem(initialValue.NODE_TYPE);
		
		subPanel = initialValue.getConfigurationPanel();
		add(subPanel,BorderLayout.SOUTH);
		
		//reconfigureSubPanel();
	}
	
	private JPanel reconfigureSubPanel(){
		
		remove(subPanel);
		
		subPanel = NodeFactory.constructUninitializedNode((NodeType)agentTypesBox.getSelectedItem()).getConfigurationPanel();	
		add(subPanel,BorderLayout.SOUTH);
		
		revalidate();
		return subPanel;
	}
	
	public NodeConfiguration getConfiguration(){
		NodeConfiguration config = new NodeConfiguration(subPanel.getConfiguration());
		config.setParameter(NODE_TYPE, new ConfigurationParameter(AbstractNode.NodeType.values(), new Object[]{agentTypesBox.getSelectedItem()}));
		return config;
	}
	
}
