package PopulationModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import auto_configuration.ConfigurationParameter;


public class CyclicGraph extends AbstractGraph {

	protected static final String SELF_LINKS = "Include Self Links";
	protected static final String LINK_DISTANCE = "Link Distance";
	
	public CyclicGraph(){
		setDefaultParameter(SELF_LINKS, new ConfigurationParameter(true));
		setDefaultParameter(LINK_DISTANCE, new ConfigurationParameter(1));
	}

	@Override
	public ArrayList<Node> getInNodes(int location) {
		ArrayList<Node> retValAgents = new ArrayList<Node>();
		
		int distance = getIntegerParameter(LINK_DISTANCE);

		if(getBooleanParameter(SELF_LINKS)){
			retValAgents.add(subNodes.get(location));
		}

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around ends of arrays
			if (neighbour1 < 0) {
				neighbour1 = subNodes.size() + neighbour1;
			}
			if (neighbour2 >= subNodes.size()) {
				neighbour2 = neighbour2 - subNodes.size();
			}

			retValAgents.add(subNodes.get(neighbour1));
			retValAgents.add(subNodes.get(neighbour2));
		}

		return retValAgents;	
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension,
			VisualizationStyle type) {
		int size = subNodes.size();
		Dimension agentDimension = subNodes.get(0).getDimension(baseDimension, type);
		
		int agentsPerEdge = (size/4)+(size%4!=0?1:0) + 1;
		
		return new Dimension(agentsPerEdge*agentDimension.width, agentsPerEdge*agentDimension.height);
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object key, Graphics g){
		
		int size = subNodes.size();
		Dimension thisDimension = getDimension(baseDimension, type);
		Dimension agentDimension = subNodes.get(0).getDimension(baseDimension, type);

		int agentsPerSection = (size/4)+(size%4!=0?1:0);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, thisDimension.width, thisDimension.height);
		
		//Top edge
		int i;
		for(i = 0; i < agentsPerSection; i++){
			subNodes.get(i).draw(baseDimension,type,key,g);
			g.translate(agentDimension.width, 0);
		}
		
		//right edge
		for(; i < 2*agentsPerSection; i++){
			subNodes.get(i).draw(baseDimension,type,key,g);
			g.translate(0, agentDimension.height);
		}
		
		//Bottom edge
		for(; i < 3*agentsPerSection; i++){
			subNodes.get(i).draw(baseDimension,type,key,g);
			g.translate(-agentDimension.width, 0);
		}
		
		//Left edge
		for(; i < size; i++){
			subNodes.get(i).draw(baseDimension,type,key,g);
			g.translate(0, -agentDimension.height);
		}
	}
		
	
}
