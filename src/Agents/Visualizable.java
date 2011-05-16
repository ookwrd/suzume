package Agents;

import java.awt.Dimension;
import java.awt.Graphics;

public interface Visualizable {

	public enum VisualizationType {layout, vertical}
	
	public Dimension getDimension(Dimension baseDimension, VisualizationType type);
	public void draw(Dimension baseDimension, VisualizationType type, Graphics g);
	public void print();
	
}
