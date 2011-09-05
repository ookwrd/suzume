package runTimeVisualization;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class DrawingLabel extends JLabel {
	
	private BufferedImage image;
	private Graphics graphics;
	
	public DrawingLabel(Dimension size){
		super();
		
		image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);//TODO better constructor?
		graphics = image.getGraphics();

		ImageIcon icon = new ImageIcon(image);
		
		super.setIcon(icon);
	}
	
	@Override
	public Graphics getGraphics(){
		return graphics;
	}

	public BufferedImage getImage() {
		return image;
	}

}
