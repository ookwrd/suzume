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

public class TimeSeries extends JPanel {

	private Dimension verticalBaseDimension = new Dimension(1,1);
	
	private PopulationModel model;
	
	private BufferedImage verticalImage;
	private Graphics verticalGraphics;
	private JLabel verticalImageLabel;
	
	public TimeSeries(PopulationModel model, int generationCount){
		
		setLayout(new BorderLayout());
		
		this.model = model;

		setBorder(new EmptyBorder(5, 0, 5, 0));
		
		Dimension drawSize = model.getDimension(verticalBaseDimension, VisualizationType.vertical);
		verticalImage = new BufferedImage(drawSize.width*generationCount, drawSize.height, BufferedImage.TYPE_INT_RGB);
		verticalGraphics = verticalImage.getGraphics();
		model.draw(verticalBaseDimension, VisualizationType.vertical,verticalImage.getGraphics());
		
		ImageIcon icon = new ImageIcon(verticalImage);
		verticalImageLabel = new JLabel(icon);
		
		add(verticalImageLabel, BorderLayout.CENTER);
		
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
			verticalImageLabel.setBorder(new LineBorder(Color.orange, 2));
		}else{
			verticalImageLabel.setBorder(new EmptyBorder(2, 2, 2, 2));
		}
	}
	
	public void updateImage(){

		//Timeseries visualization
		model.draw(verticalBaseDimension, VisualizationType.vertical, verticalGraphics.create());
		verticalGraphics.translate(verticalBaseDimension.height, 0);
		verticalImageLabel.repaint();
		
	}
	
}
