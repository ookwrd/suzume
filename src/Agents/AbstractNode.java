package Agents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import simulation.RandomGenerator;
import AutoConfiguration.BasicConfigurable;
import PopulationModel.Node;

public abstract class AbstractNode extends BasicConfigurable implements Node{
	
	private int id;
	protected RandomGenerator randomGenerator;
	protected NodeConfiguration config;//TODO i think this is now redundent as abstract node extends basic configurable
	
	@Override
	public void initializeAgent(NodeConfiguration config, int id, RandomGenerator randomGenerator){
		this.config = config;
		this.id = id;
		this.randomGenerator = randomGenerator;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String getName(){
		return config.type.toString();
	}
	
	@Override
	public String getDescription(){
		return "Undescribed";
	}
	
	@Override
	public NodeConfiguration getConfiguration(){
		return config;
	}
	
	@Override
	public Dimension getDimension(Dimension baseDimension, VisualizationType type){
		return baseDimension;
	}
	
	@Override
	public void draw(Dimension baseDimension, VisualizationType type, Graphics g){
		g.setColor(Color.green);
		g.drawRect(0, 0, baseDimension.width, baseDimension.height);
	}
		
	@Override
	public void print(){
		System.out.println(this.toString());
	}
	
}
