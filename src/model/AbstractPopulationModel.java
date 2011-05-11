package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public abstract class AbstractPopulationModel implements PopulationModel {

	@Override
	public Dimension getDimension(Dimension baseDimension){
		return baseDimension;
	}
	
	@Override
	public void draw(Dimension baseDimension, Graphics g){
		g.setColor(Color.green);
		g.drawRect(0, 0, baseDimension.width, baseDimension.height);
	}
	
}
