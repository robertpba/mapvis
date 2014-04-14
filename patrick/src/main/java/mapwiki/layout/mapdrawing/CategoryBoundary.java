package mapwiki.layout.mapdrawing;

import java.awt.Point;

class CategoryBoundary {
	// Coordinates to identify the longest diagonal line.
	public int diagonalX1;
	public int diagonalY1;
	public int diagonalX2;
	public int diagonalY2;
	
	// Two corners of the largest rectangle contains the category.
	public int rectX1;
	public int rectY1;
	public int rectX2;
	public int rectY2;
	
	public Point rectCenter() {
		return new Point(rectX1 + rectWidth() / 2, rectY1 + rectHeight() / 2);
	}
	
	public int rectWidth() {
		return rectX2 - rectX1;
	}
	
	public int rectHeight() {
		return rectY2 - rectY1;
	}
	
	public double diagonalSlope() {
		return ((double)diagonalY2 - diagonalY1) / (diagonalX2 - diagonalX1);
	}
	
	public double diagonalAngle() {
		if (diagonalX1 == diagonalX2)
			return Math.PI / 2;
		else
			return Math.atan(diagonalSlope());
	}
	
	public double diagonalLength() {
		return Math.abs(Math.sqrt(Math.pow(diagonalX1 - diagonalX2, 2) + Math.pow(diagonalY1 - diagonalY2, 2)));
	}
}
