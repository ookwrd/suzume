package PopulationModel.graphs;

import java.util.ArrayList;

import nodes.Node;

import autoconfiguration.ConfigurationParameter;


public class CompleteGraph extends AbstractGraph implements Graph {

	protected static final String SELF_LINKS = "Include Self Links";
	
	public CompleteGraph(){
		setDefaultParameter(SELF_LINKS, new ConfigurationParameter(true));
	}

	@Override
	public ArrayList<Node> getInNodes(int index) {
		@SuppressWarnings("unchecked")
		ArrayList<Node> retVal = (ArrayList<Node>) getNodeSet().clone();
		if(!getBooleanParameter(SELF_LINKS)){
			retVal.remove(retVal.get(index));
		}
		return getNodeSet();
	}

}
