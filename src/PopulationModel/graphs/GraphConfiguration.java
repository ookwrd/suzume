package PopulationModel.graphs;

import autoconfiguration.BasicConfigurable;
import PopulationModel.graphs.Graph.GraphType;

public class GraphConfiguration extends BasicConfigurable {

	public GraphConfiguration(){}
	
	public GraphConfiguration(BasicConfigurable baseConfig){
		super(baseConfig);
	}
	
	public GraphType getType(){
		return (GraphType)getParameter(GraphTypeConfigurationPanel.GRAPH_TYPE).getSelectedValue();
	}
}