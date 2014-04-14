package mapwiki.layout.hexagon;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface HexagonDrawer {
	public void draw(HexagonCanvas hc, Graphics2D g);
	public Dimension computeImageSize(HexagonCanvas hc);
}