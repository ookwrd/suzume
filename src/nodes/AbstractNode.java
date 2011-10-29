package nodes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import autoconfiguration.BasicConfigurable;
import autoconfiguration.Configurable;

import simulation.RandomGenerator;

public abstract class AbstractNode extends BasicConfigurable implements Node{
	
	public enum NodeType {SimpleConfigurableModel, AdvancedConfigurableModel, YamauchiHashimoto2010Agent, BiasAgent, ExtendedYamauchiHashimotoAgent, ProbabilityAgent, SynonymAgent}
	public static final String NODE_TYPE = "Node type";
	
	private int id;
	protected RandomGenerator randomGenerator;

	@Override
	public void initialize(Configurable config, int id, RandomGenerator randomGenerator){
		initialize(config);
		this.id = id;
		this.randomGenerator = randomGenerator;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getName(){
		return getParameter(AbstractNode.NODE_TYPE).toString();
	}
	
	@Override 
	public BasicConfigurable getConfiguration(){
		return this;
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension, VisualizationStyle type){
		return baseDimension;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		System.out.println(AbstractNode.class.getName() + ": Method draw() shouldnt be reached.");
		g.setColor(Color.green);
		g.drawRect(0, 0, baseDimension.width, baseDimension.height);
	}
	
	@Override
	public StatisticsAggregator getStatisticsAggregator(Object statisticsKey){
		System.err.println(AbstractNode.class.getName() + ": Unknown statistics AggregatorKey.");
		return null;
	}
	
}
