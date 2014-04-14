package mapwiki.layout.hexagon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import mapwiki.common.TestFrame;

class TestHexagon {
	private static Random rand = new Random();
	
	public static void main(String[] args) {
		Point p1 = new Point(250, 250);
		//Point p2 = new Point(150, 250);
		Point p2 = new Point(rand.nextInt(500), rand.nextInt(500));
		
		Dimension imgSize = new Dimension(500, 500);
		AffineTransform at = new AffineTransform();
		
		BufferedImage img = new BufferedImage(imgSize.width, imgSize.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		try {
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, img.getWidth(), img.getHeight());
			
			g2.setColor(Color.RED);
			g2.drawLine(p1.x, p1.y, p2.x, p2.y);
			
			String s = "ABCDjoin1234";
			Font font = new Font("Arial", Font.BOLD, 24);
			g2.setColor(Color.BLACK);
			g2.fillArc(245, 245, 10, 10, 0, 360);
			g2.fillArc(300, 300, 10, 10, 0, 360);
			
			double radius;
			if (p2.x == p1.x) {
				radius = -Math.PI / 2;
			} else if (p2.y == p1.y) {
				radius = 0;
			} else {
				radius = Math.atan(((double)p2.y - p1.y) / (p2.x - p1.x));
			}
			//radius = Math.PI / -2;
			
			g2.setColor(Color.GREEN);
			at.setToRotation(radius, 250, 250);
			g2.setTransform(at);
			g2.setFont(font);
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(s, 250, 250 + fm.getAscent());
			
//			g2.setColor(Color.BLUE);
//			s = "Second time!";
//			at.setToRotation(Math.toRadians(45), 250, 250);
//			g2.setTransform(at);
//			g2.drawString(s, 250, 250 + fm.getAscent());
			
			
			System.out.println("Degree=" + degree(p1.x, p1.y, p2.x, p2.y));
			
			JFrame frame = new TestFrame("Testing Hexagon", img);
			frame.setVisible(true);
		} finally {
			g2.dispose();
		}
	}
	
	private static double degree(int cx, int cy, int px, int py) {
		if (px == cx) {
			if (py > cy)
				return 90;
			else if (py < cy)
				return 270;
			else
				return 0;
		}
		
		double d = Math.toDegrees(Math.atan(((double)py - cy) / (px - cx)));
		// Deal with degree > 180.
		if (px < cx)
			return 180 + d;
		else if (d < 0)
			return 360 + d;
		else
			return d;
	}
}
