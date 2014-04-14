package mapwiki.layout.mapdrawing;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import mapwiki.layout.overlapremoval.RectConverter;

public interface MapLabel {
	void draw(Graphics2D g);
	void setOffset(int offsetX, int offsetY);
	Rectangle getRectangle();
	
	public class RectangleConverter implements RectConverter<MapLabel> {
		@Override
		public Rectangle convert(MapLabel l) {
			return l.getRectangle();
		}
	}
}
