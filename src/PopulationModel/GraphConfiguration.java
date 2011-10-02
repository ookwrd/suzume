package PopulationModel;

import AutoConfiguration.BasicConfigurable;
import PopulationModel.Graph.GraphType;

public class GraphConfiguration extends BasicConfigurable {

	public GraphConfiguration(){}
	
	public GraphConfiguration(BasicConfigurable baseConfig){
		super(baseConfig);
	}
	
	public GraphType getType(){
		return (GraphType)getParameter(GraphTypeConfigurationPanel.GRAPH_TYPE).getSelectedValue();
	}
}
