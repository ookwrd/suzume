package PopulationModel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import simulation.RandomGenerator;

public abstract class AbstractGraph extends GraphConfiguration implements Graph {

	protected ArrayList<Node> subNodes;
	
	@Override
	public void init(ArrayList<Node> subNodes, GraphConfiguration config, RandomGenerator randomGenerator){
		super.initialize(config);
		this.subNodes = subNodes; 
	}
	
	@Override
	public void resetSubNodes(ArrayList<Node> subNodes){
		this.subNodes = subNodes;
	}
	
	@Override
	public ArrayList<Node> getNodeSet(){
		return subNodes;
	}

	@Override
	public GraphConfiguration getConfiguration() {
		return this;
	}
	
	/**
	 * Override this implementation to create a directed graph.
	 */
	@Override
	public ArrayList<Node> getOutNodes(int index){
		return getInNodes(index);
	}
	

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return new ArrayList<Object>();
	}

	/**
	 * By default visualize the subNodes arranged in a compact rectangle.
	 * 
	 */
	@Override
	public Dimension getDimension(Dimension baseDimension,
			VisualizationStyle type) {
		return new Dimension(
				subNodes.get(0).getDimension(baseDimension, type).width*edgeLength(),
				subNodes.get(0).getDimension(baseDimension, type).height*edgeLength()
				);
	}
	
	private int edgeLength(){
		int subNodeCount = subNodes.size();
		double sqrt = Math.sqrt(subNodeCount);
		int edgeLength;
		if(((int)sqrt) == sqrt){
			edgeLength = (int)sqrt;
		}else{
			edgeLength = ((int)sqrt) + 1;
		}
		return edgeLength;
	}

	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type,
			Object visualiztionKey, Graphics g) {
	
		int currentColumn = 0;
		int columns = edgeLength();
		
		Dimension subNodeDimension = subNodes.get(0).getDimension(baseDimension, type);
		
		for(Node node : subNodes){
			node.draw(baseDimension, type, visualiztionKey, g);
			g.translate(subNodeDimension.width, 0);
			currentColumn++;
			if(currentColumn >= columns){
				g.translate(-subNodeDimension.width*columns, subNodeDimension.height);
				currentColumn = 0;
			}
		}
	}
}
