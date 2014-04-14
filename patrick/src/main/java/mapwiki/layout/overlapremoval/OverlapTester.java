package mapwiki.layout.overlapremoval;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

public class OverlapTester {
	private static class Data {
		String name;
		Rectangle rect;
		
		@Override
		public String toString() {
			return "Data [" + name + "]";
		}
	}

	public static void main(String[] args) {
		BufferedImage img = new BufferedImage(1300, 750, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = img.createGraphics();
		g2.setBackground(Color.WHITE);
		g2.clearRect(0, 0, img.getWidth(), img.getHeight());
		g2.setColor(Color.BLACK);
		
		Rectangle[] predefined = new Rectangle[8];
		// TODO: Predefined.
//		predefined[0] = new Rectangle(250, 250, 6, 6);
//		predefined[1] = new Rectangle(248, 249, 5, 5);
//		predefined[2] = new Rectangle(291, 208, 5, 5);
//		predefined[3] = new Rectangle(163, 278, 5, 5);
//		predefined[4] = new Rectangle(259, 268, 5, 5);
//		predefined[5] = new Rectangle(323, 215, 67, 67);
//		predefined[6] = new Rectangle(226, 238, 5, 5);
//		predefined[7] = new Rectangle(331, 321, 5, 5);
//		predefined[8] = new Rectangle(263, 253, 5, 5);
//		predefined[9] = new Rectangle(298, 193, 5, 5);
//		predefined[10] = new Rectangle(244, 238, 5, 5);
//		predefined[11] = new Rectangle(233, 272, 5, 5);
//		predefined[12] = new Rectangle(259, 227, 5, 5);
//		predefined[13] = new Rectangle(219, 250, 5, 5);
//		predefined[14] = new Rectangle(294, 319, 5, 5);
//		predefined[15] = new Rectangle(249, 225, 5, 5);
//		predefined[16] = new Rectangle(170, 180, 5, 5);
//		predefined[17] = new Rectangle(271, 244, 5, 5);
//		predefined[18] = new Rectangle(163, 330, 5, 5);
//		predefined[19] = new Rectangle(250, 280, 5, 5);
//		predefined[20] = new Rectangle(237, 247, 5, 5);
//		predefined[21] = new Rectangle(240, 263, 5, 5);
//		predefined[22] = new Rectangle(229, 255, 5, 5);
//		predefined[23] = new Rectangle(255, 238, 5, 5);
//		predefined[24] = new Rectangle(234, 224, 5, 5);
//		predefined[25] = new Rectangle(331, 236, 5, 5);
//		predefined[26] = new Rectangle(249, 268, 5, 5);
//		predefined[27] = new Rectangle(211, 195, 76, 76);
//		predefined[28] = new Rectangle(270, 265, 5, 5);

		List<Data> list = new ArrayList<Data>();
		Random rand = new Random();
		for (int i = 0; i < predefined.length; i++) {
			Data d = new Data();
			
			if (predefined[i] == null) {
				int x = rand.nextInt(200) + 100;
				int y = rand.nextInt(100) + 100;
				int width = rand.nextInt(50) + 50;
				int height = rand.nextInt(50) + 50;
				d.rect = new Rectangle(x, y, width, height);
				System.out.printf("predefined[%d] = new Rectangle(%d, %d, %d, %d);\n",
						i, x, y, width, height);
			} else {
				d.rect = predefined[i];
			}

			d.name = Integer.toString(i);
			g2.drawRect(d.rect.x, d.rect.y, d.rect.width, d.rect.height);
			g2.drawString(d.name, d.rect.x, d.rect.y);
			list.add(d);
		}
		
		System.out.println();
		g2.drawLine(500, 0, 500, img.getHeight());
		FTAOverlapRemoval<Data> or = new FTAOverlapRemoval<Data>(list, new RectConverter<Data>() {
			@Override
			public Rectangle convert(Data obj) {
				return obj.rect;
			}
		});
		or.run();
		for (FTAOverlapRemoval.Entry<Data> d: or.entryList()) {
			draw(d, g2);
		}
		
		JFrame frame = new TestFrame(img);
		frame.setLocation(100, 400);
		frame.setSize(1300, 350);
		frame.setVisible(true);
		frame.setTitle("Java Overlap Removal - FTA");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("Main exited.");
	}
	
	private static void draw(FTAOverlapRemoval.Entry<Data> d, Graphics2D g) {
		int x = d.getNewRect().x + 500;
		int y = d.getNewRect().y;
		g.drawRect(x, y, d.getElement().rect.width, d.getElement().rect.height);
		g.drawString(d.getElement().name, x, y);
	}
	
	private static class TestFrame extends JFrame {
		private static final long serialVersionUID = 1L;
		private BufferedImage img;
		
		public TestFrame(BufferedImage i) {
			img = i;
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(img, 0, 0, null);
		}

		@Override
		protected void processKeyEvent(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				e.consume();
				this.dispose();
			}
			super.processKeyEvent(e);
		}
	}
}
