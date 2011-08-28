package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import PopulationModel.PopulationModel;

import runTimeVisualization.Visualizable.VisualizationType;


@SuppressWarnings("serial")
public class SingleStepVisualization extends JPanel {
	
	private static final Dimension layoutBaseDimension = new Dimension(5,5);
	
	private PopulationModel model;
	
	private Graphics layoutGraphics;

	public SingleStepVisualization(PopulationModel model){
		
		this.model = model;
		setLayout(new BorderLayout());
		
		Dimension drawSize = model.getDimension(layoutBaseDimension,VisualizationType.layout);
		BufferedImage layoutImage = new BufferedImage(drawSize.width, drawSize.height, BufferedImage.TYPE_INT_RGB);
		layoutGraphics = layoutImage.getGraphics();
		model.draw(layoutBaseDimension,VisualizationType.layout,layoutImage.getGraphics());
		
		ImageIcon icon = new ImageIcon(layoutImage);
		add(new JLabel(icon), BorderLayout.CENTER);
	}
	
	public void updateImage(){
		model.draw(layoutBaseDimension, VisualizationType.layout, layoutGraphics.create());
		repaint();
	}

	public void updateModel(PopulationModel model) {
		this.model = model;
	}
	
}
