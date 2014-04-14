package mapwiki.layout.hexagon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import mapwiki.common.TestFrame;

final class TestHexagonOutput {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		HexagonCanvas hc = new HexagonCanvas(5, 5, 40);
		HexagonCanvas hc = new HexagonCanvas(1, 1, 150);
		BufferedImage img = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		try {
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, img.getWidth(), img.getHeight());
			
			hc.draw(g);
			JFrame frame = new TestFrame("Test Hexagon Output", img);
			frame.setVisible(true);
		} finally {
			g.dispose();
		}
	}

}
