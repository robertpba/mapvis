package mapwiki.spectrum;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class DrawingPlane {
	public static final int LENGTH_FOR_EACH_ITEM = 30;
	public static final int MIN_CIRCLE_RADIUS = 3;
	public static final int MARGIN_FOR_LABEL = 250;
	public static final int TRIM_NAME_LEN = 12;
	
	private Spectrum<String> topSpectrum;
	private Map<String, Spectrum<String>> subCatSpectrum =
		new HashMap<String, Spectrum<String>>();
	private String outputFilename;
	private int totalSpectrumItemCount;
	private CatInfoProvider provider;
	
	public DrawingPlane(CatInfoProvider p) {
		provider = p;
	}
	
	public void draw() throws IOException {
		if (totalSpectrumItemCount == 0)
			totalSpectrumItemCount = 10 * topSpectrum.size();
		int w = totalSpectrumItemCount * LENGTH_FOR_EACH_ITEM + MARGIN_FOR_LABEL + 50;
		int h = w;
		System.err.println(String.format("Image size: %dx%d", w, h));
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		try {
			g2.setBackground(new Color(255, 255, 255, 0));
			g2.clearRect(0, 0, w, h);
			internalDraw(g2);
			ImageIO.write(img, "png", new File(outputFilename));
		} finally {
			g2.dispose();
		}
	}
	
	private void internalDraw(Graphics2D g) {
		List<String> allCats = new ArrayList<String>(totalSpectrumItemCount);
		List<String> topCats = new ArrayList<String>(topSpectrum.size());
		List<Integer> topCatWidth = new ArrayList<Integer>(topSpectrum.size());
		for (String topCat: topSpectrum) {
			Spectrum<String> subCat = subCatSpectrum.get(topCat);
			topCats.add(topCat);
			if (subCat != null) {
				allCats.addAll(subCat);
				topCatWidth.add(subCat.size());
			} else {
				topCatWidth.add(10);
			}
		}
		
		// Draw the X and Y axis, with label and spectrum.
		drawAxes(g, allCats, topCatWidth);
		
		// Draw circles for each top category.
		//int avgAreaWidth = totalSpectrumItemCount * LENGTH_FOR_EACH_ITEM / topSpectrum.size();
		int posX = 0, posY = 0;
		for (int i = 0; i < topCats.size(); i++) {
			String topCat1 = topCats.get(i);
			int width1 = LENGTH_FOR_EACH_ITEM * topCatWidth.get(i);
			Color c1 = provider.getColour(topCat1);
			for (int j = i; j < topCats.size(); j++) {
				String topCat2 = topCats.get(j);
				int width2 = LENGTH_FOR_EACH_ITEM * topCatWidth.get(j);
				Color c2 = provider.getColour(topCat2);
				c1 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 50);
				c2 = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 50);
				int count = provider.getCoAssignedArticleCount(topCat1, topCat2);
				if (count > 0)
					fillBigCircle(g, posX, posY, width1, width2, scaleBigCircle(count), c1, c2);
				posY += width2;
			}
			posX += width1;
			posY = posX;
		}
		
		// Draw circles for each 2nd-level category pairs.
		for (int i = 0; i < allCats.size(); i++) {
			String c1 = allCats.get(i);
			Color colour1 = provider.getColour(c1);
			for (int j = 0; j <= i; j++) {
				String c2 = allCats.get(j);
				Color colour2 = provider.getColour(c2);
				int count = provider.getCoAssignedArticleCount(c1, c2);
				if (count > 0)
					fillCircle(g, j, i, scaleCircle(count), colour1, colour2);
			}
		}
	}
	
	private void drawAxes(Graphics2D g, List<String> names, List<Integer> topWidths) {
		int top = LENGTH_FOR_EACH_ITEM;
		int left = MARGIN_FOR_LABEL;
		int bottom = top + totalSpectrumItemCount * LENGTH_FOR_EACH_ITEM;
		int right = left + totalSpectrumItemCount * LENGTH_FOR_EACH_ITEM;
		g.setColor(Color.BLACK);
		g.drawLine(left, top, left, bottom);
		g.drawLine(left, bottom, right, bottom);
		
		AffineTransform rotateAT = new AffineTransform();
		Font fontNormal = new Font("SansSerif", Font.PLAIN, 24);
		Font fontLarge = new Font(fontNormal.getFontName(), Font.PLAIN, 32);
		Font fontSmall = new Font(fontNormal.getFontName(), Font.PLAIN, 20);
		g.setFont(fontNormal);
		
		top += 22;
		left = 10;
		int altLeft = MARGIN_FOR_LABEL + LENGTH_FOR_EACH_ITEM;
		int fontHeight = g.getFontMetrics().getAscent();
		for (String s: names) {
			internalDrawAxisSubCat(g, left, top, s);
			
			AffineTransform oldAT = g.getTransform();
			rotateAT.setToRotation(Math.toRadians(-90), altLeft, bottom + MARGIN_FOR_LABEL - fontHeight);
			g.setTransform(rotateAT);
			internalDrawAxisSubCat(g, altLeft, bottom + MARGIN_FOR_LABEL - fontHeight, s);
			g.setTransform(oldAT);
			
			top += LENGTH_FOR_EACH_ITEM;
			altLeft += LENGTH_FOR_EACH_ITEM;
		}
		
		top = LENGTH_FOR_EACH_ITEM;
		left = MARGIN_FOR_LABEL;
		int index = 0;
		for (String s: topSpectrum) {
			int topWidth = topWidths.get(index++);
			int realWidth = LENGTH_FOR_EACH_ITEM * topWidth;
			
			if (realWidth < 150)
				g.setFont(fontSmall);
			else
				g.setFont(fontLarge);
			
			internalDrawAxisTop(g, left, bottom, realWidth, s);
			AffineTransform oldAT = g.getTransform();
			rotateAT.setToRotation(Math.toRadians(90), MARGIN_FOR_LABEL, top);
			g.setTransform(rotateAT);
			internalDrawAxisTop(g, MARGIN_FOR_LABEL, top, realWidth, s);
			g.setTransform(oldAT);
			
			left += realWidth;
			top += realWidth;
			g.setColor(Color.GRAY);
			
			g.drawLine(left, LENGTH_FOR_EACH_ITEM, left, bottom + MARGIN_FOR_LABEL);
			g.drawLine(10, top, right, top);
		}
	}
	
	private void internalDrawAxisTop(Graphics g, int x, int y, int width, String name) {
		g.setColor(provider.getColour(name));
		g.fillRect(x, y + 10, width, 20);
		g.setColor(Color.BLACK);
		g.drawString(name, x + 5, y + 60);
	}
	
	private void internalDrawAxisSubCat(Graphics g, int x, int y, String catName) {
		String s = catName.length() <= TRIM_NAME_LEN ? catName : catName.substring(0, TRIM_NAME_LEN);
		g.drawString(s, x, y);
	}
	
	private int scaleCircle(int articleCount) {
		if (articleCount == 1)
			return MIN_CIRCLE_RADIUS;
		double areaWidth = LENGTH_FOR_EACH_ITEM / 2;
		return (int)(areaWidth - areaWidth / log(articleCount));
	}
	
	private int scaleBigCircle(int articleCount) {
		if (articleCount == 1)
			return MIN_CIRCLE_RADIUS;
		final double areaWidth = 250; 
		return (int)(areaWidth - areaWidth / log(articleCount));
	}
	
	private double log(int number) {
		return Math.log(number) / Math.log(5);	// 5-based logarithm.
	}
	
	private void fillBigCircle(Graphics g, int posX, int posY, int width1, int width2,
			int size, Color c1, Color c2) {
		int r = size;
		if (r == 0)
			return; 
		int left = MARGIN_FOR_LABEL + posX + (width1 / 2) - r;
		int top = LENGTH_FOR_EACH_ITEM + posY + (width2 / 2) - r;
		int w = 2 * r;
		int h = 2 * r;
		g.setColor(c1);
		g.fillOval(left, top, w, h);
		if (!c1.equals(c2)) {
			g.setColor(c2);
			g.fillArc(left, top, w, h, 0, 180);
		}
		g.setColor(Color.GREEN);
		g.drawOval(left, top, w, h);
	}

	private void fillCircle(Graphics g, int x, int y, int size, Color c1, Color c2) {
		int r = size;
		if (r == 0)
			return;
		int halfCell = LENGTH_FOR_EACH_ITEM / 2;
		x = MARGIN_FOR_LABEL + x * LENGTH_FOR_EACH_ITEM - r + halfCell;
		y = LENGTH_FOR_EACH_ITEM + y * LENGTH_FOR_EACH_ITEM - r + halfCell;
		int w = 2 * r;
		int h = 2 * r;
		
		g.setColor(c2);
		g.fillOval(x, y, w, h);
		if (!c1.equals(c2)) {
			g.setColor(c1);
			g.fillArc(x, y, w, h, 0, 180);
		}
		g.setColor(Color.BLACK);
		g.drawOval(x, y, w, h);
	}

	public String getOutputFilename() {
		return outputFilename;
	}

	public void setOutputFilename(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	public Spectrum<String> getTopSpectrum() {
		return topSpectrum;
	}
	
	public void setTopSpectrum(Spectrum<String> topSpectrum) {
		this.topSpectrum = topSpectrum;
	}
	
	public void addSubCatSpectrum(String catName, Spectrum<String> sp) {
		if (topSpectrum.contains(catName)) {
			subCatSpectrum.put(catName, sp);
			totalSpectrumItemCount += sp.size();
		}
	}
	
	public Spectrum<String> removeSubCatSpectrum(String subCatName) {
		if (subCatSpectrum.containsKey(subCatName)) {
			Spectrum<String> sp = subCatSpectrum.get(subCatName);
			subCatSpectrum.remove(subCatName);
			totalSpectrumItemCount -= sp.size();
			return sp;
		} else {
			return null;
		}
	}
}
