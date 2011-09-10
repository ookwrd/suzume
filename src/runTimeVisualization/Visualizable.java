package runTimeVisualization;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

public interface Visualizable {

	public enum VisualizationStyle {layout, vertical}
	
	public ArrayList<Object> getVisualizationKeys();
	
	public Dimension getDimension(Dimension baseDimension, VisualizationStyle type);
	public void draw(Dimension baseDimension, VisualizationStyle type, Graphics g);
	
}
