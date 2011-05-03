package model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ZoomPanel extends JLabel implements MouseMotionListener, MouseListener {

	private BufferedImage small;
	private BufferedImage large;
	private ImageIcon icon;
	
	private int width;
	private int height;
	
	public ZoomPanel(BufferedImage small, BufferedImage large){
		
		super();
		
		this.small = small;
		this.large = large;
		
		width = small.getWidth();
		height = small.getHeight();
		
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
		
		//Calculate center in large image
		double largeX = ((double)x / width)*large.getWidth();
		double largeY = ((double)y / height)*large.getHeight();
		
		double startX = largeX - width/2;
		double startY = largeY - height/2;
		
		//Adjust if it goes over borders
		if(startX < 0){
			startX = 0;
		}
		if(startY < 0){
			startY = 0;
		}
		if(startX >= large.getWidth() - width){
			startX = large.getWidth() - width - 1;
		}
		if(startY >= large.getHeight() - height){
			startY = large.getHeight() - height -1;
		}
		
		//Set icon image as a view based on those settings.
		BufferedImage zoom = large.getSubimage((int)startX, (int)startY, width, height);
		icon.setImage(zoom);
		repaint();
	}
	
	private void resetViewport(){
		icon.setImage(small);
		repaint();
	}
	
}
