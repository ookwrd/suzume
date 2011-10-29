package nodes.Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import nodes.AbstractNode;
import nodes.Node;
import nodes.Agents.statisticaggregators.AbstractCountingAggregator;
import nodes.Agents.statisticaggregators.AbstractMinMaxAggregator;

import autoconfiguration.Configurable;
import autoconfiguration.ConfigurationParameter;


import simulation.RandomGenerator;

public abstract class AbstractAgent extends AbstractNode implements Agent {
	
	protected enum VisualizationTypes {FITNESS, ALIVE}
	protected enum StatisticsTypes {FITNESS, MAX_FITNESS, MIN_FITNESS}

	protected static final String VISUALIZATION_TYPE = "Visualization Types:";
	protected static final String BASE_FITNESS = "Base fitness value:";
	protected static final String MIN_FITNESS = "Minimum fitness:";

	private double fitness;
	private boolean isAlive = true;
	
	public AbstractAgent(){	
		setDefaultParameter(VISUALIZATION_TYPE, new ConfigurationParameter(VisualizationTypes.values(), new Object[]{}));
		setDefaultParameter(STATISTICS_TYPE, new ConfigurationParameter(StatisticsTypes.values(),new Object[]{StatisticsTypes.FITNESS}));
		
		setDefaultParameter(BASE_FITNESS, new ConfigurationParameter(1));
		setDefaultParameter(MIN_FITNESS, new ConfigurationParameter(1));
	}
	
	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator){
		super.initialize(config, id, randomGenerator);
	}
	
	@Override
	public double getFitness() {
		return fitness;
	}
	
	protected void setFitness(double fitness){
		this.fitness = fitness;
		int min = getIntegerParameter(MIN_FITNESS);
		if(this.fitness < min){
			this.fitness = min;
		}
	}
	
	@Override
	public void reset(){
		setFitness(getIntegerParameter(BASE_FITNESS));
	}
	
	@Override
	public boolean canStillLearn(int utteranceSeen){
		return utteranceSeen < 200;
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
			c = new Color(fitness*8,0,0);
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
			return new AbstractCountingAggregator(StatisticsCollectionPoint.PostFinalizeFitness,"Fitness") {
				@Override
				public double getValue(Node agent) {	
					return ((Agent)agent).getFitness();
				}
			};
			
		case MAX_FITNESS:
			return new AbstractMinMaxAggregator(AbstractMinMaxAggregator.Type.Max, StatisticsCollectionPoint.PostFinalizeFitness, "Max Fitness") {
				@Override
				protected double statValue(Node agent) {
					return ((Agent)agent).getFitness();
				}
			};
			
		case MIN_FITNESS:
			return new AbstractMinMaxAggregator(AbstractMinMaxAggregator.Type.Min, StatisticsCollectionPoint.PostFinalizeFitness, "Min Fitness") {
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
		return new ArrayList<Object>(Arrays.asList(getParameter(Node.STATISTICS_TYPE).getSelectedValues()));
	}
	
	protected Color mapValueToRainbow(double value, int range){
		if(value < 0 || value > range){
			return Color.BLACK;
		}
		
		//Scaling into 0-range
		int colorVal = (int)((value/range)*1023);
		
		if(colorVal < 256){//Red->Yellow
			return new Color(255, colorVal, 0);
		}else if(colorVal < 512){//Yellow-Green
			return new Color(511-colorVal,255,0);
		}else if(colorVal < 768){//Green->Cyan
			return new Color(0,255,colorVal - 512);
		}else{//Cyan->Blue
			return new Color(0,1023-colorVal,255);
		}
	}
	
	protected Color mapValueToYellowRed(double value, int range){
		return mapValueToColor(value, range, 255, 255, 0, 255, 0, 0);
	}
	
	protected Color mapValueToColor(
			double value, 
			int range, 
			int initialRed, 
			int initialGreen, 
			int initialBlue,
			int finalRed,
			int finalGreen, 
			int finalBlue){
		
		if(value < 0 || value > range){
			return Color.BLACK;
		}
		
		double proportion = value/range;
		double inverseProportion = 1-proportion;
		
		return new Color(
				(int)(initialRed*inverseProportion+finalRed*proportion),
				(int)(initialGreen*inverseProportion+finalGreen*proportion),
				(int)(initialBlue*inverseProportion+finalBlue*proportion)
				);
	}
}
