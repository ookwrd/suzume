package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import nodes.AbstractNode;
import nodes.Node;
import nodes.NodeConfiguration;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;

import autoconfiguration.ConfigurationParameter;


import simulation.RandomGenerator;

public abstract class AbstractAgent extends AbstractNode implements Agent {
	
	private enum VisualizationTypes {FITNESS, ALIVE}
	
	protected static final String VISUALIZATION_TYPE = "Visualization Type";
	protected static final String FITNESS_STATISTICS = "Fitness";
	protected static final String BASE_FITNESS = "Base fitness value:";
	protected static final String MIN_FITNESS = "Minimum fitness:";

	private double fitness;
	private boolean isAlive = true;
	
	public AbstractAgent(){	
		setDefaultParameter(BASE_FITNESS, new ConfigurationParameter(1));
		setDefaultParameter(MIN_FITNESS, new ConfigurationParameter(1));
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), false));
	}
	
	@Override
	public void initialize(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		super.initialize(config, id, randomGenerator);

		setFitness(getIntegerParameter(BASE_FITNESS));;
	}
	
	@Override
	public double getFitness() {
		return fitness;
	}
	
	@Override
	public void setFitness(double fitness){
		this.fitness = fitness;
		int min = getIntegerParameter(MIN_FITNESS);
		if(this.fitness < min){
			this.fitness = min;
		}
	}
	
	@Override
	public boolean canStillLearn(){
		return true;
	}
	
	@Override
	public void invent(){}
	
	@Override
	public void killPhase(){
		killAgent();
	}
	
	@Override
	public boolean isAlive(){
		return isAlive;
	}
	
	protected void killAgent(){
		isAlive = false;
	}
	
	@Override
	public void finalizeFitnessValue(){}
	
	@Override
	public ArrayList<Agent> getBaseAgents(){
		ArrayList<Agent> retAgents = new ArrayList<Agent>();
		retAgents.add(this);
		return retAgents;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		if(!(visualizationKey instanceof VisualizationTypes)){
			super.draw(baseDimension, type, visualizationKey, g);
			return;
		}

		Color c;
		switch ((VisualizationTypes)visualizationKey) {
		case FITNESS:
			int fitness = new Double(getFitness()).intValue();
			c = new Color(fitness*8, fitness*8, 0);
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;
			
		case ALIVE:
			if(isAlive){
				c = Color.GREEN;
			}else{
				c = Color.RED;
			}
			g.setColor(c);
			g.fillRect(0, 0, baseDimension.width, baseDimension.height);
			break;

		default:
			System.err.println("Unrecognized Visualization type in AbstractAgent:draw.");
		}
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		ArrayList<StatisticsAggregator> retVal = new ArrayList<StatisticsAggregator>();
		
		retVal.add(new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication,"Fitness") {
			@Override
			public double getValue(Node agent) {	
				return ((Agent)agent).getFitness();
			}
		});
		
		/*retVal.add(new AbstractMinMaxAggregator(AbstractMinMaxAggregator.Type.Max, StatisticsCollectionPoint.PostCommunication, "Max Fitness") {
			@Override
			protected double statValue(Node agent) {
				return ((Agent)agent).getFitness();
			}
		});
		
		retVal.add(new AbstractMinMaxAggregator(AbstractMinMaxAggregator.Type.Min, StatisticsCollectionPoint.PostCommunication, "Min Fitness") {
			@Override
			protected double statValue(Node agent) {
				return ((Agent)agent).getFitness();
			}
		});*/
		
		return retVal;
	}

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return new ArrayList<Object>(Arrays.asList(getParameter(VISUALIZATION_TYPE).getSelectedValues()));
	}	
	
}
