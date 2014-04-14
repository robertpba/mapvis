package mapwiki.common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class TestFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	
	public TestFrame(String title, BufferedImage img) {
		super(title);
		this.img = img;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(img.getWidth() + 10, img.getHeight() + 25));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(img, this.getInsets().left, this.getInsets().top, null);
	}
}
