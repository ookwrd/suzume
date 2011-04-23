package model;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ZoomPanel extends JLabel {

	public ZoomPanel(BufferedImage small, BufferedImage large){
		super();
		
		ImageIcon icon = new ImageIcon(small);
		setIcon(icon);
	}
	
}
