package runTimeVisualization;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import PopulationModel.PopulationModel;


@SuppressWarnings("serial")
public class TimeSeriesVisualization extends JScrollPane {

	//private ArrayList<TimeSeries> series = new ArrayList<TimeSeries>();
	
	private ArrayList<TimeSeriesSet> runs = new ArrayList<TimeSeriesSet>();
	
	private Visualizable model;
	private int generationCount;
	
	private JButton printButton;
	
	private JPanel inner;
	
	private int runAtLastUpdate = 0;
	
	public TimeSeriesVisualization(Visualizable model, int generationCount, JButton printButton){
		super();
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getHorizontalScrollBar().setUnitIncrement(16);
		getVerticalScrollBar().setUnitIncrement(16);
		
		this.model = model;
		this.generationCount = generationCount;
		this.printButton = printButton;
		
		inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

		configureNewSetTimeseries(0);
		getViewport().setView(inner);
		
	}
	
	public void configureNewSetTimeseries(int runNum){
		TimeSeriesSet run = new TimeSeriesSet(model, generationCount, runNum, printButton);
		runs.add(run);
		inner.add(run);
		
		//Scroll to the new panel at the bottom
		inner.revalidate();
		int height = (int)inner.getPreferredSize().getHeight();
        Rectangle rect = new Rectangle(0,height,10,10);
        inner.scrollRectToVisible(rect);     
	}
	
	public void updateImage(int run){
		//Is the update the start of a new run visualization?
		if(this.runAtLastUpdate != run){
			configureNewSetTimeseries(run);
			runAtLastUpdate = run;
		}
		
		runs.get(runs.size()-1).updateImage();
	}

	public void updateModel(Visualizable model) {//TODO is this needed
		this.model = model;
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
