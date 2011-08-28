package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import PopulationModel.PopulationModel;

import runTimeVisualization.Visualizable.VisualizationType;


@SuppressWarnings("serial")
public class SingleStepVisualization extends JPanel {
	
	private static final Dimension layoutBaseDimension = new Dimension(5,5);
	
	private PopulationModel model;
	
	private Graphics layoutGraphics;
	private JLabel iconLabel;

	public SingleStepVisualization(PopulationModel model){
		
		this.model = model;
		setLayout(new BorderLayout());
		
		Dimension drawSize = model.getDimension(layoutBaseDimension,VisualizationType.layout);
		BufferedImage layoutImage = new BufferedImage(drawSize.width, drawSize.height, BufferedImage.TYPE_INT_RGB);
		layoutGraphics = layoutImage.getGraphics();
		model.draw(layoutBaseDimension,VisualizationType.layout,layoutImage.getGraphics());
		
		ImageIcon icon = new ImageIcon(layoutImage);
		iconLabel = new JLabel(icon);
		add(iconLabel, BorderLayout.CENTER);
		
		setFocusable(true);
		setFocused(false);
		
		addFocusListener(new FocusListener() {
			@Override public void focusLost(FocusEvent arg0) {
				setFocused(false);
			}
			@Override public void focusGained(FocusEvent arg0) {
				setFocused(true);
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				requestFocusInWindow();
			}
		});
	}
	
	private void setFocused(boolean focused){
		if(focused){
			iconLabel.setBorder(new LineBorder(Color.orange, 2));
		}else{
			iconLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		}
	}
	
	public void updateImage(){
		model.draw(layoutBaseDimension, VisualizationType.layout, layoutGraphics.create());
		repaint();
	}

	public void updateModel(PopulationModel model) {
		this.model = model;
	}
	
}
