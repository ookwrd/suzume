package Agents;

import java.awt.Dimension;
import java.awt.Graphics;

public interface Visualizable {

	public Dimension getDimension(Dimension baseDimension);
	public void draw(Dimension baseDimension, Graphics g);
	
}
