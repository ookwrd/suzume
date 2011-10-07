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
import nodes.Agents.statisticaggregators.AbstractMinMaxAggregator;

import autoconfiguration.ConfigurationParameter;


import simulation.RandomGenerator;

public abstract class AbstractAgent extends AbstractNode implements Agent {
	
	private enum VisualizationTypes {FITNESS, ALIVE}
	private enum StatisticsTypes {FITNESS, MAX_FITNESS, MIN_FITNESS}

	protected static final String VISUALIZATION_TYPE = "Visualization Types:";
	protected static final String BASE_FITNESS = "Base fitness value:";
	protected static final String MIN_FITNESS = "Minimum fitness:";

	private double fitness;
	private boolean isAlive = true;
	
	public AbstractAgent(){	
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), false));
		setDefaultParameter(STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),new Object[]{StatisticsTypes.FITNESS}));
		
		setDefaultParameter(BASE_FITNESS, new ConfigurationParameter(1));
		setDefaultParameter(MIN_FITNESS, new ConfigurationParameter(1));
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
			c = new Color(fitness,0,0);
			break;
			
		case ALIVE:
			if(isAlive){
				c = Color.GREEN;
			}else{
				c = Color.RED;
			}
			break;

		default:
			c = null;
			System.err.println("Unrecognized Visualization type in AbstractAgent:draw.");
		}
		
		g.setColor(c);
		g.fillRect(0, 0, baseDimension.width, baseDimension.height);
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		if(!(statisticsKey instanceof StatisticsTypes)){
			return super.getStatisticsAggregator(statisticsKey);
		}
		
		switch ((StatisticsTypes)statisticsKey) {
		case FITNESS:
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostCommunication,"Fitness") {
				@Override
				public double getValue(Node agent) {	
					return ((Agent)agent).getFitness();
				}
			};
			
		case MAX_FITNESS:
			return new AbstractMinMaxAggregator(AbstractMinMaxAggregator.Type.Max, StatisticsCollectionPoint.PostCommunication, "Max Fitness") {
				@Override
				protected double statValue(Node agent) {
					return ((Agent)agent).getFitness();
				}
			};
			
		case MIN_FITNESS:
			return new AbstractMinMaxAggregator(AbstractMinMaxAggregator.Type.Min, StatisticsCollectionPoint.PostCommunication, "Min Fitness") {
				@Override
				protected double statValue(Node agent) {
					return ((Agent)agent).getFitness();
				}
			};

		default:
			System.err.println(AbstractAgent.class.getName() + ": Unknown StatisticsType");
			return null;
		}
	}

	@Override
	public ArrayList<Object> getVisualizationKeys() {
		return new ArrayList<Object>(Arrays.asList(getParameter(VISUALIZATION_TYPE).getSelectedValues()));
	}	
	
	@Override
	public ArrayList<Object> getStatisticsKeys(){
		return new ArrayList<Object>(Arrays.asList(getParameter(STATISTICS_TYPE).getSelectedValues()));
	}
	
}
