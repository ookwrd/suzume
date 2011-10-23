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

	private JComboBox agentTypesBox;
	private Object currentlySelected;
	
	private ConfigurationPanel subPanel;
	
	public NodeConfigurationPanel(Configurable initialValue){	
		setLayout(new BorderLayout());
		
		agentTypesBox = new JComboBox(initialValue.getParameter(AbstractNode.NODE_TYPE).getList());
		agentTypesBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
		add(agentTypesBox,BorderLayout.NORTH);
		
		subPanel = initialValue.getConfigurationPanel();
		add(subPanel,BorderLayout.SOUTH);
		
		currentlySelected = initialValue.getParameter(AbstractNode.NODE_TYPE).getSelectedValue();
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
	
	public Configurable getConfiguration(){
		Configurable config = subPanel.getConfiguration();
		config.setParameter(AbstractNode.NODE_TYPE, new ConfigurationParameter(AbstractNode.NodeType.values(), new Object[]{agentTypesBox.getSelectedItem()}));
		return config;
	}
	
}
