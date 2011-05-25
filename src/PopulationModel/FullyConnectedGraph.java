package PopulationModel;

import java.util.ArrayList;

public class FullyConnectedGraph extends AbstractGraph implements Graph {

	@Override
	public ArrayList<Node> getOutNodes(Node node) {
		return getNodeSet();
	}

	@Override
	public ArrayList<Node> getInNodes(Node node) {
		return getNodeSet();
	}

}
