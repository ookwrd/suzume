package model;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class StepwiseVisualizer extends JPanel {

	private JFrame frame;
	
	private PopulationModel model;
	
	private VisualizationConfiguration config;
	
	private BufferedImage image;
	private ImageIcon icon;
	private JLabel label;
	
	public StepwiseVisualizer(String title, PopulationModel model, VisualizationConfiguration config){
		
		this.model = model;
		this.config = config;
		
		frame = new JFrame();
		frame.setTitle(title);
		
		Dimension drawSize = model.getDimension();
		image = new BufferedImage(drawSize.width, drawSize.height, BufferedImage.TYPE_INT_RGB);
		model.draw(image.getGraphics());
		
		icon = new ImageIcon(image);
		label = new JLabel(icon);
		add(label);
		
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		frame.add(scrollPane);
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void updateImage(){
		
		model.draw(image.getGraphics());
		label.repaint();
	}

}
