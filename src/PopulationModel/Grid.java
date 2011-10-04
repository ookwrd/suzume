package PopulationModel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import auto_configuration.ConfigurationParameter;


import simulation.RandomGenerator;

public class Grid extends AbstractGraph {

	protected static final String SELF_LINKS = "Include Self Links";
	protected static final String ROW_NUMBERS = "Number of rows:";
	protected static final String AUTO_LAYOUT = "Automatic layout:";
	
	protected int size;
	protected int columns;
	
	public Grid(){
		setDefaultParameter(SELF_LINKS, new ConfigurationParameter(false));
		setDefaultParameter(AUTO_LAYOUT, new ConfigurationParameter(true));
		setDefaultParameter(ROW_NUMBERS, new ConfigurationParameter(30));
	}
	
	public void init(ArrayList<Node> subNodes, GraphConfiguration config, RandomGenerator randomGenerator){
		super.init(subNodes, config, randomGenerator);
		
		size = subNodes.size();
		
		if(!getBooleanParameter(AUTO_LAYOUT)){
			columns = getIntegerParameter(ROW_NUMBERS);
		}else{
			columns = edgeLength();
		}
	}
	
	@Override
	public ArrayList<Node> getInNodes(int index) {
		ArrayList<Node> retVal = new ArrayList<Node>();
		
		if(hasNorth(index)){
			retVal.add(getNorth(index));
		}
		
		if(hasSouth(index)){
			retVal.add(getSouth(index));
		}
		
		if(hasEast(index)){
			retVal.add(getEast(index));
		}
		
		if(hasWest(index)){
			retVal.add(getWest(index));
		}
		
		if(getBooleanParameter(SELF_LINKS)){
			retVal.add(subNodes.get(index));
		}
		
		return retVal;
	}
	
	private boolean hasNorth(int index){
		return (index - columns) >= 0 && index < size;
	}
	
	private Node getNorth(int index){
		return subNodes.get(index-columns);
	}

	private boolean hasSouth(int index){
		return (index + columns) < size;
	}
	
	private Node getSouth(int index){
		return subNodes.get(index+columns);
	}
	
	private boolean hasEast(int index){
		return ((index + 1) % columns) != 0  && (index + 1) < size;
	}
	
	private Node getEast(int index){
		return subNodes.get(index+1);
	}
	
	private boolean hasWest(int index){
		return (index % columns) != 0 && columns < size;
	}
	
	private Node getWest(int index){
		return subNodes.get(index-1);
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension,
			VisualizationStyle type) {
		return new Dimension(
				subNodes.get(0).getDimension(baseDimension, type).width*columns,
				subNodes.get(0).getDimension(baseDimension, type).height*((size%columns)==0?size/columns:size/columns+1)
				);
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type,
			Object visualiztionKey, Graphics g) {
	
		int currentColumn = 0;
		
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
