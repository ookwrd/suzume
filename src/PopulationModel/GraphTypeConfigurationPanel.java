package PopulationModel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import populationNodes.NodeFactory;
import populationNodes.AbstractNode.NodeType;

import AutoConfiguration.Configurable;
import AutoConfiguration.ConfigurationPanel;
import AutoConfiguration.ConfigurationParameter;
import PopulationModel.Graph.GraphType;

@SuppressWarnings("serial")
public class GraphTypeConfigurationPanel extends JPanel {

	public static final String GRAPH_TYPE = "Graph Type";
	
	private JComboBox graphTypesBox;
	private Object currentlySelected;
	
	private ConfigurationPanel subPanel;
	
	public GraphTypeConfigurationPanel(Configurable initialValue){	
		setLayout(new BorderLayout());
	
		graphTypesBox = new JComboBox(GraphType.values());
		graphTypesBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
		add(graphTypesBox,BorderLayout.NORTH);
		
		subPanel = initialValue.getConfigurationPanel();
		add(subPanel,BorderLayout.SOUTH);
		
		currentlySelected = initialValue.getParameter(GRAPH_TYPE).getSelectedValue();
		graphTypesBox.setSelectedItem(currentlySelected);
	}	
	

	private void reconfigureSubPanel(){
	
		GraphType selected = (GraphType)graphTypesBox.getSelectedItem();
		if(currentlySelected == selected){
			return;
		}
		currentlySelected = selected;
		
		remove(subPanel);
		
		subPanel = GraphFactory.constructGraph(selected).getConfigurationPanel();	
		add(subPanel,BorderLayout.SOUTH);
		
		revalidate();
	}
	
	public GraphConfiguration getConfiguration(){
		GraphConfiguration retVal = new GraphConfiguration(subPanel.getConfiguration());
		retVal.setFixedParameter(GRAPH_TYPE, new ConfigurationParameter(Graph.GraphType.values(), new Object[]{graphTypesBox.getSelectedItem()}));
		return retVal;
	}
}
