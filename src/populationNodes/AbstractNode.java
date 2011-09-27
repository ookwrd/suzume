package populationNodes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

import simulation.RandomGenerator;
import PopulationModel.Node;

public abstract class AbstractNode extends NodeConfiguration implements Node{
	
	public enum NodeType { YamauchiHashimoto2010, BiasAgent, AlteredAgent, FixedProbabilityAgent, ProbabilityAgent, ConfigurablePopulation }
	
	private int id;
	protected RandomGenerator randomGenerator;

	@Override
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
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
		return getParameter(NodeTypeConfigurationPanel.NODE_TYPE).toString();
	}
	
	@Override 
	public NodeConfiguration getConfiguration(){
		return this;
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension, VisualizationStyle type){
		return baseDimension;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationStyle type, Object visualizationKey, Graphics g){
		System.out.println("Abstract Node: Method draw() shouldnt be reached.");
		g.setColor(Color.green);
		g.drawRect(0, 0, baseDimension.width, baseDimension.height);
	}
	
	@Override
	public ArrayList<StatisticsAggregator> getStatisticsAggregators(){
		return new ArrayList<Node.StatisticsAggregator>();
	}
	
}
