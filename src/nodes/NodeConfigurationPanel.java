package nodes;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import nodes.AbstractNode.NodeType;

import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationPanel;
import autoconfiguration.ConfigurationParameter;



@SuppressWarnings("serial")
public class NodeConfigurationPanel extends JPanel {

	public static final String NODE_TYPE = "Node type";//TODO move
	
	private JComboBox agentTypesBox;
	private Object currentlySelected;
	
	private ConfigurationPanel subPanel;
	
	public NodeConfigurationPanel(Configurable initialValue){	
		setLayout(new BorderLayout());
		
		agentTypesBox = new JComboBox(AbstractNode.NodeType.values());
		agentTypesBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
		add(agentTypesBox,BorderLayout.NORTH);
		
		subPanel = initialValue.getConfigurationPanel();
		add(subPanel,BorderLayout.SOUTH);
		
		currentlySelected = initialValue.getParameter(NODE_TYPE).getSelectedValue();
		agentTypesBox.setSelectedItem(currentlySelected);
	}
	
	private void reconfigureSubPanel(){
		
		NodeType selected = (NodeType)agentTypesBox.getSelectedItem();
		if(currentlySelected == selected){
			return;
		}
		currentlySelected = selected;
		
		remove(subPanel);
		
		subPanel = NodeFactory.constructUninitializedNode(selected).getConfigurationPanel();	
		add(subPanel,BorderLayout.SOUTH);
		
		revalidate();
	}
	
	public NodeConfiguration getConfiguration(){
		NodeConfiguration config = new NodeConfiguration(subPanel.getConfiguration());
		config.setParameter(NODE_TYPE, new ConfigurationParameter(AbstractNode.NodeType.values(), new Object[]{agentTypesBox.getSelectedItem()}));
		return config;
	}
	
}
