package PopulationModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import model.AbstractPopulationModel;
import model.PopulationModel;

import org.jfree.ui.Size2D;

import Agents.Agent;
import Agents.YamauchiHashimoto2010;

/**
 * Population model corresponding to a cyclical distribution of agents as seen in:
 * Relaxation of selection, Niche Construction, and the Baldwin Eﬀect in Language 
 * Evolution - Hajime Yamauchi & Takashi Hashimoto
 * 
 * @author Luke McCrohon
 *
 */
public class OriginalPopulationModel extends AbstractPopulationModel implements PopulationModel {

	private PopulationGraph learningGraph;
	private PopulationGraph communicationGraph;
	private PopulationGraph reproductionGraph;
	
	private ArrayList<Agent> previousGeneration = new ArrayList<Agent>();
	private ArrayList<Agent> currentGeneration = new ArrayList<Agent>();
	
	/**
	 * Create new population with the specified agents as the currentGeneration and previousGeneration.
	 * 
	 * @param agents
	 */
	public OriginalPopulationModel(ArrayList<Agent> currentGeneration, ArrayList<Agent> previousGeneration) {
		this.currentGeneration = currentGeneration;
		this.previousGeneration = previousGeneration;
	}

	@Override
	public void switchGenerations(ArrayList<Agent> newGeneration) {

		previousGeneration = currentGeneration;
		currentGeneration = newGeneration;

	}

	/**
	 * 
	 * size of returnValue = distance*2 
	 * 
	 */
	@Override
	public ArrayList<Agent> getPossibleCommunicators(Agent agent) {

		int distance = 1;
		
		int location = currentGeneration.indexOf(agent);

		ArrayList<Agent> retValAgents = new ArrayList<Agent>();

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around ends of arrays
			if (neighbour1 < 0) {
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			if (neighbour2 >= currentGeneration.size()) {
				neighbour2 = neighbour2 - currentGeneration.size();
			}

			retValAgents.add(currentGeneration.get(neighbour1));
			retValAgents.add(currentGeneration.get(neighbour2));
		}

		return retValAgents;
	}

	/**
	 * 
	 * size of return = agents*2 + 1
	 */
	@Override
	public ArrayList<Agent> getPossibleTeachers(Agent agent) {

		int distance = 2;
		
		int location = currentGeneration.indexOf(agent);

		ArrayList<Agent> retValAgents = new ArrayList<Agent>();

		//add the ancestor at the same point as the specified agent
		retValAgents.add(previousGeneration.get(location));

		for (int i = 1; i <= distance; i++) {
			//Add pair of agents distance i from the central agent
			
			int neighbour1 = location - i;
			int neighbour2 = location + i;

			//wrap around the end of arrays
			if (neighbour1 < 0) {
				neighbour1 = currentGeneration.size() + neighbour1;
			}
			if (neighbour2 >= currentGeneration.size()) {
				neighbour2 = neighbour2 - currentGeneration.size();
			}

			retValAgents.add(previousGeneration.get(neighbour1));
			retValAgents.add(previousGeneration.get(neighbour2));

		}

		return retValAgents;
	}

	@Override
	public ArrayList<Agent> getAncestorGeneration() {
		return previousGeneration;
	}

	@Override
	public ArrayList<Agent> getCurrentGeneration() {
		return currentGeneration;
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension, VisualizationType type){
		
		if(currentGeneration.size() == 0){
			System.out.print("Trying to get Dimension of empty population model");
			return null;
		}
	
		switch(type){
		case layout:
			return getDimensionLayout(baseDimension, type);
			
		case vertical:
			return getDimensionVertical(baseDimension, type);
			
		default:
			System.out.println("Unsupported Visualization type");
			return null;
		}
	
	}
	
	private Dimension getDimensionLayout(Dimension baseDimension, VisualizationType type){
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);
		
		int agentsPerEdge = (size/4)+(size%4!=0?1:0) + 1;
		
		return new Dimension(agentsPerEdge*agentDimension.width, agentsPerEdge*agentDimension.height);
	}
	
	private Dimension getDimensionVertical(Dimension baseDimension, VisualizationType type){
		int size = currentGeneration.size();
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);
		
		return new Dimension(agentDimension.width, agentDimension.height*size);
	}
		
	
	@Override
	public void draw(Dimension baseDimension, VisualizationType type, Graphics g){
		switch(type){
		case layout:
			drawLayout(baseDimension, type, g);
			break;
			
		case vertical:
			drawVertical(baseDimension, type, g);
			break;
			
		default:
			System.out.println("Unrecognized Visualization type");
			return;
		}
	}
	
	private void drawLayout(Dimension baseDimension, VisualizationType type, Graphics g){
		int size = currentGeneration.size();
		Dimension thisDimension = getDimension(baseDimension, type);
		Dimension agentDimension = currentGeneration.get(0).getDimension(baseDimension, type);

		int agentsPerSection = (size/4)+(size%4!=0?1:0);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, thisDimension.width, thisDimension.height);
		
		//Top edge
		int i;
		for(i = 0; i < agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(agentDimension.width, 0);
		}
		
		//right edge
		for(; i < 2*agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(0, agentDimension.height);
		}
		
		//Bottom edge
		for(; i < 3*agentsPerSection; i++){
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(-agentDimension.width, 0);
		}
		
		//Left edge
		for(; i < size; i++){
			//TODO reset the translate point.
			previousGeneration.get(i).draw(baseDimension,type,g);
			g.translate(0, -agentDimension.height);
		}
	}
	

	private void drawVertical(Dimension baseDimension, VisualizationType type, Graphics g){
		int size = previousGeneration.size();
		Dimension agentDimension = previousGeneration.get(0).getDimension(baseDimension, type);

		for(int i = 0; i < size; i++){
			previousGeneration.get(i).draw(baseDimension, type, g);
			g.translate(0, agentDimension.height);
		}
	}
	
	/**
	 * Print a generations worth of agents. 
	 */
	@Override
	public void print(){
		System.out.println("Printing Previous Generation");	
		for(Agent agent : getAncestorGeneration()){  
			agent.printAgent();
			System.out.println();
		}
	}

	@Override
	public ArrayList<Agent> getPossibleParents(Agent agent) {
		return previousGeneration;
	}//TODO w�switch out for a graphbased implemenation

}