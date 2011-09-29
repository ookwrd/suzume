package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.ConfigurationParameter;

public class FullyConnectedGraph extends AbstractGraph implements Graph {

	protected static final String SELF_LINKS = "Include Self Links";
	
	public FullyConnectedGraph(){
		setDefaultParameter(SELF_LINKS, new ConfigurationParameter(true));
	}
	
	@Override
	public ArrayList<Node> getOutNodes(Node node) {
		return getNodeSet();
	}

	@Override
	public ArrayList<Node> getInNodes(Node node) {
		return getNodeSet();
	}

}
