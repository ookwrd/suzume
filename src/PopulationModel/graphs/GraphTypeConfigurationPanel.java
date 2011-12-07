package PopulationModel.graphs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import PopulationModel.graphs.Graph.GraphType;

import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationPanel;
import autoconfiguration.ConfigurationParameter;

@SuppressWarnings("serial")
public class GraphTypeConfigurationPanel extends JPanel {

	public static final String GRAPH_TYPE = "Graph Type";
	
	private JComboBox graphTypesBox;
	private Object currentlySelected;
	
	private ConfigurationPanel subPanel;
	
	public GraphTypeConfigurationPanel(Configurable initialValue){	
		setLayout(new BorderLayout());
	
		graphTypesBox = new JComboBox(initialValue.getParameter(GRAPH_TYPE).getList());
		graphTypesBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				reconfigureSubPanel();
			}
		});
		
		add(graphTypesBox,BorderLayout.NORTH);
		
		subPanel = new ConfigurationPanel(initialValue);
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
		
		subPanel = new ConfigurationPanel(GraphFactory.constructGraph(selected));	
		add(subPanel,BorderLayout.SOUTH);
		
		revalidate();
	}
	
	public Configurable getConfiguration(){
		Configurable retVal = subPanel.getConfiguration();
		retVal.setFixedParameter(GRAPH_TYPE, new ConfigurationParameter(Graph.GraphType.values(), new Object[]{graphTypesBox.getSelectedItem()}));
		return retVal;
	}
}
