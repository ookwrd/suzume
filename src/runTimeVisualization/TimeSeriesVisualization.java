package runTimeVisualization;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import PopulationModel.PopulationModel;

import runTimeVisualization.Visualizable.VisualizationType;

@SuppressWarnings("serial")
public class TimeSeriesVisualization extends JScrollPane {

	private Dimension verticalBaseDimension = new Dimension(1,1);
	
	private PopulationModel model;
	private int generationCount;
	
	private BufferedImage verticalImage;
	private Graphics verticalGraphics;
	private JLabel verticalImageLabel;
	
	private JPanel inner;
	
	private int runAtLastUpdate = 0;
	
	public TimeSeriesVisualization(PopulationModel model, int generationCount){
		super();
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		getHorizontalScrollBar().setUnitIncrement(16);
		getVerticalScrollBar().setUnitIncrement(16);
		
		this.model = model;
		this.generationCount = generationCount;
		
		inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
		getViewport().setView(inner);
		configureNewTimeseries();
		
	}
	
	public void configureNewTimeseries(){
		
		Dimension drawSize = model.getDimension(verticalBaseDimension, VisualizationType.vertical);
		verticalImage = new BufferedImage(drawSize.width*generationCount, drawSize.height, BufferedImage.TYPE_INT_RGB);
		verticalGraphics = verticalImage.getGraphics();
		model.draw(verticalBaseDimension, VisualizationType.vertical,verticalImage.getGraphics());
		
		ImageIcon icon = new ImageIcon(verticalImage);
		verticalImageLabel = new JLabel(icon);
		verticalImageLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
		inner.add(verticalImageLabel);
		
		//Scroll to the new panel at the bottom
		inner.revalidate();
		int height = (int)inner.getPreferredSize().getHeight();
        Rectangle rect = new Rectangle(0,height,10,10);
        inner.scrollRectToVisible(rect);
	}
	
	public void updateImage(int run){
		
		//Is the update the start of a new run visualization?
		if(this.runAtLastUpdate != run){
			configureNewTimeseries();
			runAtLastUpdate = run;
		}
		
		//Timeseries visualization
		model.draw(verticalBaseDimension, VisualizationType.vertical, verticalGraphics.create());
		verticalGraphics.translate(verticalBaseDimension.height, 0);
		verticalImageLabel.repaint();
		
	}

	public void updateModel(PopulationModel model) {
		this.model = model;
	}
	
}
