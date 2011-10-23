package runTimeVisualization;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class TimeSeriesVisualization extends JScrollPane {

	private ArrayList<TimeSeriesSet> runs = new ArrayList<TimeSeriesSet>();
	
	private int generationCount;
	
	private JButton printButton;
	
	private JPanel inner;
	
	private int runAtLastUpdate = 1;
	
	public TimeSeriesVisualization(Visualizable model, int generationCount, JButton printButton){
		super();
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getHorizontalScrollBar().setUnitIncrement(16);
		getVerticalScrollBar().setUnitIncrement(16);
		
		this.generationCount = generationCount;
		this.printButton = printButton;
		
		inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

		configureNewSetTimeseries(runAtLastUpdate, model);
		getViewport().setView(inner);
		
	}
	
	public void configureNewSetTimeseries(int runNum, Visualizable model){
		TimeSeriesSet run = new TimeSeriesSet(model, generationCount, runNum, printButton);
		runs.add(run);
		inner.add(run);
		
		//Scroll to the new panel at the bottom
		inner.revalidate();
		int height = (int)inner.getPreferredSize().getHeight();
        Rectangle rect = new Rectangle(0,height,10,10);
        inner.scrollRectToVisible(rect);     
	}
	
	public void updateImage(int run, Visualizable model){
		//Is the update the start of a new run visualization?
		if(this.runAtLastUpdate != run){
			configureNewSetTimeseries(run, model);
			runAtLastUpdate = run;
		}
		
		runs.get(runs.size()-1).updateImage(model);
	}
	
	public BufferedImage getSelected(){
		for(TimeSeriesSet tss : runs){
			BufferedImage image = tss.getSelected();
			if(image != null){
				return image;
			}
		}
		return null;
	}
}
