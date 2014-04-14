package mapwiki.spectrum;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import mapwiki.common.TestFrame;

final class ColourTest {
	public static void main(String[] args) {
		int colourCount = CatInfoProviderImpl.CATEGORY_COLOURS.length;
		int width = 150;
		int height = 40 * (colourCount + 1);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = img.createGraphics();
		try {
			g.setFont(new Font("SansSerif", Font.BOLD, 16));
			int ascent = g.getFontMetrics().getAscent();
			int top = 20;
			for (String s: CatInfoProviderImpl.CATEGORY_COLOURS) {
				g.setColor(Color.decode(s));
				g.fillRect(20, top, 20, 20);
				g.drawString(s, 50, top + ascent);
				top += 40;
			}
		} finally {
			g.dispose();
		}
		
		TestFrame tf = new TestFrame("Colours", img);
		tf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tf.setVisible(true);
	}
}
