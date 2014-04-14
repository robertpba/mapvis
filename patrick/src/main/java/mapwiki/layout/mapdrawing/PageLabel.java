package mapwiki.layout.mapdrawing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class PageLabel implements MapLabel {
	public static final int LINE_BREAK_LEN = 7;
	public static final int MAX_TAIL_LEN = 2;
	public static final int SPACE_TO_CITY = 10;
	
	public enum TextLabelDirection {
		UPPER_LEFT, UPPER_CENTER, UPPER_RIGHT,
		LOWER_LEFT, LOWER_CENTER, LOWER_RIGHT
	};
	
	private Point pageLocation;
	private Font font;
	private FontMetrics fm;
	private List<String> lines;
	private CategoryBoundary catBound;
	private int maxLineWidth;
	private Point textLocation;
	private Point offset = new Point(0, 0);
	
	public PageLabel(MapDrawer md, String pageTitle, Graphics2D g, CategoryBoundary cb, int x, int y) {
		this.pageLocation = new Point(x, y);
		this.catBound = cb;
		this.font = md.pageFont;
		this.fm = g.getFontMetrics(font);
		this.lines = LineBreaker.wrapLine(pageTitle);
		
		// Calculate parameters for drawing the text label.
		for (String s: lines)
			this.maxLineWidth = Math.max(maxLineWidth, fm.stringWidth(s));
		double angle = angleToCenterPoint();
		TextLabelDirection textDir = directionFromAngle(angle);
		textLocation = textPosFromDirection(x, y, textDir);
	}

	@Override
	public void draw(Graphics2D g) {
		g.fillArc(pageLocation.x, pageLocation.y, 10, 10, 0, 360);
		g.setFont(font);
		g.setColor(Color.BLACK);

		int x = textLocation.x + offset.x;
		int y = textLocation.y + offset.y;
		for (String line: lines) {
			g.drawString(line, x, y);
			y += fm.getHeight();
		}
	}
	
	private Point textPosFromDirection(int cx, int cy, TextLabelDirection dir) {
		int x, y;
		if (dir == TextLabelDirection.UPPER_LEFT) {
			x = cx - maxLineWidth;
			y = cy - SPACE_TO_CITY - fm.getHeight() * (lines.size() - 1);
		} else if (dir == TextLabelDirection.UPPER_CENTER) {
			x = cx - maxLineWidth / 2;
			y = cy - SPACE_TO_CITY - fm.getHeight() * (lines.size() - 1);
		} else if (dir == TextLabelDirection.UPPER_RIGHT) {
			x = cx + SPACE_TO_CITY;
			y = cy - SPACE_TO_CITY - fm.getHeight() * (lines.size() - 1);
		} else if (dir == TextLabelDirection.LOWER_LEFT) {
			x = cx - maxLineWidth;
			y = cy + SPACE_TO_CITY + fm.getAscent();
		} else if (dir == TextLabelDirection.LOWER_CENTER) {
			x = cx - maxLineWidth / 2;
			y = cy + SPACE_TO_CITY + fm.getAscent();
		} else {
			x = cx + SPACE_TO_CITY;
			y = cy + SPACE_TO_CITY + fm.getAscent();
		}
		return new Point(x, y);
	}
	
	private double angleToCenterPoint() {
		// Center point of the category.
		Point center = catBound.rectCenter();
		int cx = center.x;
		int cy = center.y;
		
		// The location of page.
		int px = pageLocation.x;
		int py = pageLocation.y;
		
		if (cx == px) {
			if (cy > py)
				return 90;
			else if (py < cy)
				return 270;
			else
				return 0;
		}
		
		double d = Math.toDegrees(Math.atan(((double)cy - py) / (cx - px)));
		// Deal with degree > 180.
		if (cx < px)
			return 180 + d;
		else if (d < 0)
			return 360 + d;
		else
			return d;
	}
	
	private TextLabelDirection directionFromAngle(double angle) {
		if (angle >= 315)
			return TextLabelDirection.LOWER_LEFT;
		else if (angle >= 225)
			return TextLabelDirection.LOWER_CENTER;
		else if (angle >= 180)
			return TextLabelDirection.LOWER_RIGHT;
		else if (angle >= 135)
			return TextLabelDirection.UPPER_RIGHT;
		else if (angle >= 45)
			return TextLabelDirection.UPPER_CENTER;
		else
			return TextLabelDirection.UPPER_LEFT;
	}

	@Override
	public Rectangle getRectangle() {
		Rectangle rect = new Rectangle(textLocation.x,
				textLocation.y - fm.getAscent(),
				maxLineWidth,
				lines.size() * fm.getHeight());
		return rect;
	}

	@Override
	public void setOffset(int offsetX, int offsetY) {
		offset.x = offsetX;
		offset.y = offsetY;
	}
}
