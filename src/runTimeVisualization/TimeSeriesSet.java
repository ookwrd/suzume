package runTimeVisualization;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class TimeSeriesSet extends JPanel {

	private ArrayList<TimeSeriesPanel> series = new ArrayList<TimeSeriesPanel>();
	
	public TimeSeriesSet(Visualizable model, int generationCount, int run, final JButton printButton){

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Run " + run));
		
		for(Object key : model.getVisualizationKeys()){	
			JPanel overlay = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));	
			JLabel test = new JLabel(key + ":");
			overlay.add(test);
			add(overlay);
			
			TimeSeriesPanel ts = new TimeSeriesPanel(model, generationCount, printButton, key);
			series.add(ts);
			add(ts);
		}
	}
	
	public void updateImage(){
		for(TimeSeriesPanel ts : series){
			ts.updateImage();
		}
	}
	
	public BufferedImage getSelected(){
		for(TimeSeriesPanel tss : series){
			if(tss.isSelected()){
				return tss.getImage();
			}
		}
		return null;
	}
	
}
