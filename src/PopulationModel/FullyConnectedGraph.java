package PopulationModel;

import java.util.ArrayList;

public class FullyConnectedGraph extends AbstractGraph implements PopulationGraph {

	@Override
	public ArrayList<PopulationNode> getOutNodes(PopulationNode node) {
		return getNodeSet();
	}

	@Override
	public ArrayList<PopulationNode> getInNodes(PopulationNode node) {
		return getNodeSet();
	}

}
