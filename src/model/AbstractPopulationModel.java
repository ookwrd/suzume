package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public abstract class AbstractPopulationModel implements PopulationModel {

	@Override
	public Dimension getDimension(){
		return new Dimension(1,1);
	}
	
	@Override
	public void draw(Graphics g){
		g.setColor(Color.green);
		g.drawRect(0, 0, 1, 1);
	}
	
}
