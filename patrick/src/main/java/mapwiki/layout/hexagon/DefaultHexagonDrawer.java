package mapwiki.layout.hexagon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class DefaultHexagonDrawer implements HexagonDrawer {
	private double sideLen;
	private double halfSideLen;
	private double rtHeight;
	private double rtBottom;
	private double hexWidth;
	private double hexHeight;
	
	public static final double MARGIN_LEFT = 10;
	public static final double MARGIN_TOP = 10;
	public static final double COS60 = Math.cos(Math.toRadians(60));
	public static final double SIN60 = Math.sin(Math.toRadians(60));
	
	protected HexagonCanvas canvas;
	protected Graphics2D g;
	
	protected int[] pointsX = new int[6];
	protected int[] pointsY = new int[6];
	
	public DefaultHexagonDrawer(double sideLength) {
		sideLen = sideLength;			// Side length.
		halfSideLen = sideLen / 2.0;	// Half of side length.
		rtHeight = sideLen * SIN60;
		rtBottom = sideLen * COS60;
		hexWidth = sideLen + rtBottom;	// The total width of the hexagon.
		hexHeight = rtHeight * 2;		// The height of the hexagon.
	}
	
	public void draw(HexagonCanvas hc, Graphics2D g) {
		this.canvas = hc;
		this.g = g;

        // FIXME: remove the comment to draw the legend
        // drawLegend();
		
		// Let the sub-class has a chance to adjust the hexagons first.
		for (int i = 0; i < this.canvas.getWidth(); i++) {
			for (int j = 0; j < this.canvas.getHeight(); j++) {
				calcPoints(i, j);
				beforeDraw(i, j);
			}
		}
		
		// Draw the background.
		for (int i = 0; i < this.canvas.getWidth(); i++) {
			for (int j = 0; j < this.canvas.getHeight(); j++) {
				calcPoints(i, j);
				internalDrawHexagon(i, j);
			}
		}
		
		// Draw the line.
		for (int i = 0; i < this.canvas.getWidth(); i++) {
			for (int j = 0; j < this.canvas.getHeight(); j++) {
				calcPoints(i, j);
				internalDrawHexagonLine(i, j);
			}
		}
		
		// Draw the text label or other marker images.
		beforeDrawLabel();
		internalDrawLabel();
		
		drawComplete();
	}

	protected void calcPoints(int x, int y) {
		double startX = MARGIN_LEFT + x * (sideLen + rtBottom);
		double startY = MARGIN_TOP + y * hexHeight;
		double centerX = startX + rtBottom + halfSideLen;
		double centerY = startY + rtHeight + (x % 2 != 0 ? rtHeight : 0);
		
		pointsX[0] = (int)(centerX + halfSideLen);
		pointsX[1] = (int)(centerX + rtBottom + halfSideLen);
		pointsX[2] = pointsX[0];
		pointsX[3] = (int)(centerX - halfSideLen);
		pointsX[4] = (int)(centerX - rtBottom - halfSideLen);
		pointsX[5] = pointsX[3];
		
		pointsY[0] = (int)(centerY - rtHeight);
		pointsY[1] = (int)centerY;
		pointsY[2] = (int)(centerY + rtHeight);
		pointsY[3] = pointsY[2];
		pointsY[4] = pointsY[1];
		pointsY[5] = pointsY[0];
	}

	protected void internalDrawHexagon(int x, int y) {
		g.setColor(Color.WHITE);
		g.fillPolygon(pointsX, pointsY, 6);
	}
	
	protected void internalDrawHexagonLine(int x, int y) {
		g.setColor(Color.BLACK);
		g.drawPolygon(pointsX, pointsY, 6);
	}
	
	protected void internalDrawLabel() {
		// The functionality should be provided by sub-classes.
	}
	
	protected void beforeDraw(int x, int y) {
		// The functionality should be provided by sub-classes.
	}
	
	protected void beforeDrawLabel() {
		// The functionality should be provided by sub-classes.
	}
	
	protected void drawLegend() {
		// The functionality should be provided by sub-classes.
	}
	
	protected void drawComplete() {
		// The functionality should be provided by sub-classes.
	}

	@Override
	public Dimension computeImageSize(HexagonCanvas hc) {
		int w = (int)(hc.getWidth() * hexWidth + MARGIN_LEFT * 2);
		int h = (int)(hc.getHeight() * hexHeight + MARGIN_TOP * 2);
		return new Dimension(w, h);
	}
}
