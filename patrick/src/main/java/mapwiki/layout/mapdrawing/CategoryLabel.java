package mapwiki.layout.mapdrawing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.List;

class CategoryLabel implements MapLabel {
	List<String> lines;
	int level;
	CategoryBoundary catBoundary;
	
	private static final AffineTransform TRANSFORM = new AffineTransform();
	private int lineHeight;
	private int maxLineWidth;
	private Font font;
	private FontMetrics fm;
	private Color colour;
	private MapDrawer md;
	
	public CategoryLabel(MapDrawer md, String catTitle, CategoryBoundary catBound, Graphics2D g, int level) {
		this.md = md;
		this.catBoundary = catBound;
		this.level = level;
		this.lines = LineBreaker.wrapLine(catTitle);
		this.font = md.categoryFonts[level];
		this.fm = g.getFontMetrics(font);
		this.lineHeight = fm.getHeight();
		setFontAndColour(g);
	}
	
	private void setFontAndColour(Graphics2D g) {
		if (level <= 2) {
			// Fixed font style and size for top categories.
			font = md.categoryFonts[level];
			if (level == 1)
				font = font.deriveFont(MapDrawer.UNDERLINE_FONT_ATTR);
			colour = md.categoryColours[level];
			
			// Compute longest line width.
			this.fm = g.getFontMetrics(font);
			for (String line: lines)
				maxLineWidth = Math.max(maxLineWidth, fm.stringWidth(line));
			lineHeight = fm.getHeight();
		} else {
			// Colour by level.
			colour = md.categoryColours[level];
			
			// Find the longest line.
			String longestLine = "";
			for (String line: lines)
				if (longestLine.length() < line.length())
					longestLine = line;
			
			// Use the drawing width of the longest line to test the font size.
			for (int i = 3; i < md.categoryFonts.length; i++) {
				Font f = md.categoryFonts[i];
				this.fm = g.getFontMetrics(f);
				int width = fm.stringWidth(longestLine);
				int height = fm.getHeight() * lines.size();
				if (i == md.categoryFonts.length - 1 ||
						width <= catBoundary.rectWidth() && height <= catBoundary.rectHeight()) {
					font = f;
					lineHeight = fm.getHeight();
					maxLineWidth = fm.stringWidth(longestLine);
					break;
				}
			}
		}
	}
	
	private double refineAngle(double angle) {
		angle = Math.toDegrees(angle);
		double result = 0;
		if (angle >= -22.5 && angle <= 22.5)
			result = 0;
		else if (angle >= 22.5 && angle <= 67.5)
			result = 45;
		else if (angle >= -67.5 && angle <= -22.5)
			result = -45;
		else
			result = 90;	// Equals to the line at 135 degree.
		return Math.toRadians(result);
	}

	@Override
	public void draw(Graphics2D g) {
		g.setFont(font);
		g.setColor(colour);
		
		// Compute the starting point of the text, centering the text along the line.
		int x = catBoundary.rectX1 + ((catBoundary.rectWidth() - maxLineWidth) / 2);
		int y = catBoundary.rectY1 + (catBoundary.rectHeight() / 2) -
			(lineHeight * lines.size() / 2) + fm.getAscent();
		
		// Save the previous matrix for transformation.
		Point centerPoint = catBoundary.rectCenter();
		AffineTransform saveAT = g.getTransform();
		if (level > 1) {	// Only rotate the text if it is not top level label.
			TRANSFORM.setToRotation(refineAngle(catBoundary.diagonalAngle()), 
					centerPoint.x, centerPoint.y);
			g.setTransform(TRANSFORM);
		}
		for (String line: lines) {
			String s = level <= 2 ? line.toUpperCase() : line;
			g.drawString(s, x, y);
			y += lineHeight;
		}
		g.setTransform(saveAT);
	}

	@Override
	public Rectangle getRectangle() {
		return null;
	}

	@Override
	public void setOffset(int offsetX, int offsetY) {
		throw new UnsupportedOperationException();
	}
}
