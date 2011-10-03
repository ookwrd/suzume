package PopulationModel;

import java.util.ArrayList;

import AutoConfiguration.ConfigurationParameter;

import simulation.RandomGenerator;

public class Grid extends AbstractGraph {

	protected static final String SELF_LINKS = "Include Self Links";
	
	protected int size;
	protected int columns;
	
	public Grid(){
		setDefaultParameter(SELF_LINKS, new ConfigurationParameter(true));
	}
	
	public void init(ArrayList<Node> subNodes, GraphConfiguration config, RandomGenerator randomGenerator){
		super.init(subNodes, config, randomGenerator);
		
		size = subNodes.size();
		columns = edgeLength();
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
}
