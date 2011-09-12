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

	private ArrayList<TimeSeries> series = new ArrayList<TimeSeries>();
	
	public TimeSeriesSet(Visualizable model, int generationCount, int run, final JButton printButton){

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new TitledBorder("Run " + run));
		
		for(Object key : model.getVisualizationKeys()){	
			JPanel overlay = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));	
			JLabel test = new JLabel(key + ":");
			overlay.add(test);
			add(overlay);
			
			TimeSeries ts = new TimeSeries(model, generationCount, printButton, key);
			series.add(ts);
			add(ts);
		}
	}
	
	public void updateImage(){
		for(TimeSeries ts : series){
			ts.updateImage();
		}
	}
	
	public BufferedImage getSelected(){
		for(TimeSeries tss : series){
			if(tss.isSelected()){
				return tss.getImage();
			}
		}
		return null;
	}
	
}
