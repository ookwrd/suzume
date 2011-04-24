package model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ZoomPanel extends JLabel implements MouseMotionListener, MouseListener {

	BufferedImage small;
	BufferedImage large;
	
	ImageIcon icon;
	
	public ZoomPanel(BufferedImage small, BufferedImage large){
		super();
		
		this.small = small;
		this.large = large;
		
		icon = new ImageIcon(small);
		setIcon(icon);
	
		addMouseMotionListener(this);
		addMouseListener(this);
		
		}

	@Override
	public void mouseDragged(MouseEvent e) {	
		
		int x = e.getX();
		int y = e.getY();
		
		//Check still in area
		if(x < 0 || x >= small.getWidth()){
			return;
		}
		if(y < 0 || y >= small.getHeight()){
			return;
		}
		
		adjustViewport(x, y);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		resetViewport();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		adjustViewport(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		resetViewport();
	}

	private void adjustViewport(int x, int y){
		BufferedImage zoom = large.getSubimage(x, y, small.getWidth(), small.getHeight());//Works as the large panel is currently twice as large as the small
		icon.setImage(zoom);
		repaint();
	}
	
	private void resetViewport(){
		icon.setImage(small);
		repaint();
	}
	
}
