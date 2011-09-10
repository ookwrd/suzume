package runTimeVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import PopulationModel.PopulationModel;

import runTimeVisualization.Visualizable.VisualizationStyle;


@SuppressWarnings("serial")
public class SingleStepVisualization extends JPanel {
	
	private static final Dimension layoutBaseDimension = new Dimension(5,5);
	
	private PopulationModel model;
	
	private DrawingLabel iconLabel;
	
	private boolean isSelected = false;

	public SingleStepVisualization(PopulationModel model, final JButton printButton){
		
		this.model = model;
		setLayout(new BorderLayout());
		
		Dimension drawSize = model.getDimension(layoutBaseDimension,VisualizationStyle.layout);
		iconLabel = new DrawingLabel(drawSize);
		
		add(iconLabel, BorderLayout.CENTER);
		
		setFocusable(true);
		setSelected(false);
		
		addFocusListener(new FocusListener() {
			@Override public void focusLost(FocusEvent arg0) {
				if(arg0.getOppositeComponent() != printButton){
					setSelected(false);
				}	
			}
			@Override public void focusGained(FocusEvent arg0) {
				setSelected(true);
			}
		});
		
		printButton.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(e.getOppositeComponent() != SingleStepVisualization.this){
					setSelected(false);
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				requestFocusInWindow();
			}
		});
	}
	
	private void setSelected(boolean selected){
		if(selected){
			iconLabel.setBorder(new LineBorder(Color.orange, 2));
			isSelected = true;
		}else{
			iconLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
			isSelected = false;
		}
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public void updateImage(){
		model.draw(layoutBaseDimension, VisualizationStyle.layout, iconLabel.getGraphics().create());
		repaint();
	}

	public void updateModel(PopulationModel model) {
		this.model = model;
	}
	
	public BufferedImage getSelected(){
		if(isSelected){
			return iconLabel.getImage();
		}
		return null;
	}
	
}
