package runTimeVisualization;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class GeographicVisualization extends JScrollPane {
	
	private ArrayList<GeographicPanel> series = new ArrayList<GeographicPanel>();
	
	public GeographicVisualization(Visualizable model, final JButton printButton){
		super();
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getVerticalScrollBar().setUnitIncrement(16);
		
		JPanel inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
		
		for(Object key : model.getVisualizationKeys()){
			JPanel overlay = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));	
			JLabel test = new JLabel(key + ":");
			overlay.add(test);
			inner.add(overlay);
			
			GeographicPanel panel = new GeographicPanel(model, printButton, key);
			series.add(panel);
			inner.add(panel);
		}
		
		getViewport().setView(inner);
	}
	
	public BufferedImage getSelected(){
		for(GeographicPanel panel : series){
			if(panel.isSelected()){
				return panel.getImage();
			}
		}
		return null;
	}
	
	public void updateImage(Visualizable model){
		for(GeographicPanel panel : series){
			panel.updateImage(model);
		}
	}

	@Override
	public void finalize() throws Throwable{
		System.out.println("Geographic Visualization Being finalized");
		super.finalize();
	}
}
