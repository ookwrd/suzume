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

import runTimeVisualization.Visualizable.VisualizationStyle;

@SuppressWarnings("serial")
public class TimeSeriesPanel extends JPanel {

	private static final Dimension verticalBaseDimension = new Dimension(1,1);
	
	private final Object key;
	private DrawingLabel label;
	private boolean isSelected = false;
	
	public TimeSeriesPanel(Visualizable model, int generationCount, final JButton printButton, Object visualizationKey){
		
		key = visualizationKey;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 0, 0));
		
		Dimension singleSize = model.getDimension(verticalBaseDimension, VisualizationStyle.vertical);
		Dimension panelSize = new Dimension(singleSize.width*generationCount, singleSize.height);
		label = new DrawingLabel(panelSize);
		
		add(label, BorderLayout.NORTH);
		
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
				if(e.getOppositeComponent() != TimeSeriesPanel.this){
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
	
	private void setSelected(boolean focused){
		if(focused){
			label.setBorder(new LineBorder(Color.orange, 2));
			isSelected = true;
		}else{
			label.setBorder(new EmptyBorder(2, 2, 2, 2));
			isSelected = false;
		}
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public void updateImage(Visualizable model){
		model.draw(verticalBaseDimension, VisualizationStyle.vertical, key, label.getGraphics().create());
		label.getGraphics().translate(verticalBaseDimension.height, 0);
		label.repaint();
	}

	public BufferedImage getImage() {
		return label.getImage();
	}
	
}
