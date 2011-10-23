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
public class GeographicPanel extends JPanel {
	
	private static final Dimension layoutBaseDimension = new Dimension(4,4);
	
	private final Object key;
	private DrawingLabel iconLabel;
	private boolean isSelected = false;
	
	private JPanel inner;

	public GeographicPanel(Visualizable model, final JButton printButton, Object key){
		
		this.key = key;
		
		inner = new JPanel();
		add(inner);
		
		inner.setLayout(new BorderLayout());
		
		Dimension drawSize = model.getDimension(layoutBaseDimension,VisualizationStyle.layout);
		iconLabel = new DrawingLabel(drawSize);
		
		inner.add(iconLabel, BorderLayout.CENTER);
		
		inner.setFocusable(true);
		setSelected(false);
		
		inner.addFocusListener(new FocusListener() {
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
				if(e.getOppositeComponent() != inner){
					setSelected(false);
				}
			}
		});
		
		inner.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				inner.requestFocusInWindow();
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
	
	public void updateImage(Visualizable model){
		model.draw(layoutBaseDimension, VisualizationStyle.layout, key, iconLabel.getGraphics().create());
		inner.repaint();
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public BufferedImage getImage(){
		return iconLabel.getImage();
	}
	
}
