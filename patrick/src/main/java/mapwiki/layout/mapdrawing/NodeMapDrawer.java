package mapwiki.layout.mapdrawing;

import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;
import mapwiki.layout.LayoutGraphic;
import mapwiki.layout.categoryprovider.NodeCategory;
import mapwiki.layout.categoryprovider.NodeCategoryProvider;
import mapwiki.layout.hexagon.DefaultHexagonDrawer;
import mapwiki.layout.hexagon.HexDir;
import mapwiki.layout.hexagon.Hexagon;
import mapwiki.layout.hexagon.HexagonText;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval.Entry;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NodeMapDrawer extends DefaultHexagonDrawer {
	Stroke[] borderStrokes;		// 1-based index array.

	private NodeCategoryProvider cp;
	private Map<Category, CategoryBoundary> catBounds = new HashMap<Category, CategoryBoundary>();

	public NodeMapDrawer(double hexSideLen, NodeCategoryProvider cp) {
		super(hexSideLen);

		this.cp = cp;

        this.borderStrokes = new Stroke[]{
                null,
                new BasicStroke(2.0f),
                new BasicStroke(0.5f),
                new BasicStroke(0.25f)

        };
    }

	@Override
	protected void beforeDraw(int x, int y) {
		Hexagon h = canvas.get(x, y);
		
		// Altitude is too low. Make it water.
		if (isWater(h))
			h.resetCatInfo();
		
		int waterCount = 0, landCount = 0, alienCount = 0, rockCount = 0;
		Category lastCat = null;
		float lastAlt = 0;
		for (Hexagon n: canvas.findNeighbours(h)) {
			if (!n.isEmpty() && n.getAltitude() > lastAlt) {
				lastCat = n.getCategory(cp);
				lastAlt = n.getAltitude();
			}
			if (!h.isEmpty() && !h.getCategory(cp).equals(n.getCategory(cp)))
				alienCount++;
			if (isWater(n)) {
				waterCount++;
			} else {
				landCount++;
				if (n.isEssential())
					rockCount++;
			}
		}
		
		// Check if it is surrounded by water. Make it water if so.
		if (!isWater(h) && waterCount >= 6) {
			h.resetCatInfo();
			return;
		}
		
		// Also check whether it is surrounded by land. Make it land if it is water.
		// If the land is surrounded by different neighbours, make it as if it is those neighbours.
		if (isWater(h) && landCount >= 6 || alienCount >= 5) {
			h.resetCatInfo();
			if (lastCat != null) {
				float newAlt = Math.max(lastAlt, 0);
				h.assignCategory(lastCat, newAlt, 0.1, Hexagon.CATEGORY_ESSENTIAL);
			}
		}
		
		if (!h.isEssential() && rockCount >= 6) {
			h.resetCatInfo();
			h.assignCategory(lastCat, lastAlt, 0.1, Hexagon.CATEGORY_ESSENTIAL);
		}
		
		// Adjust the boundary of the category.
		markCategoryBound(h.getCategory(cp), pointsX[0], pointsY[0]);
	}
	
	@Override
	protected void internalDrawHexagon(int x, int y) {
		Hexagon h = canvas.get(x, y);
		if (!h.isEmpty()) {
            NodeCategory category = (NodeCategory) h.getCategory(cp);
            Color color = new Color((int) category.node.getVal("color"));
            g.setColor(color);
			g.fillPolygon(pointsX, pointsY, 6);
        }
	}

    @Override
    protected void internalDrawHexagonLine(int x, int y) {
        Hexagon h = canvas.get(x, y);
        if (h.isEmpty() || isWater(h) || !isBorderHexagon(h))
            return;

        g.setColor(new Color(0xaaaaaa));
        drawBorder(h, HexDir.NORTH_EAST, pointsX[0], pointsY[0], pointsX[1], pointsY[1]);
        drawBorder(h, HexDir.SOUTH_EAST, pointsX[1], pointsY[1], pointsX[2], pointsY[2]);
        drawBorder(h, HexDir.SOUTH, pointsX[2], pointsY[2], pointsX[3], pointsY[3]);
        drawBorder(h, HexDir.SOUTH_WEST, pointsX[3], pointsY[3], pointsX[4], pointsY[4]);
        drawBorder(h, HexDir.NORTH_WEST, pointsX[4], pointsY[4], pointsX[5], pointsY[5]);
        drawBorder(h, HexDir.NORTH, pointsX[5], pointsY[5], pointsX[0], pointsY[0]);
    }
	
	private int drawBorder(Hexagon h, HexDir dir, int x1, int y1, int x2, int y2) {
		// First check the category between two hexagons.
		Category hcat = h.getCategory(cp);
		Hexagon n = canvas.findNeighbour(h, dir);
		Category ncat = null;
		if (n != null && !isWater(n))
			ncat = n.getCategory(cp);
		if (hcat.equals(ncat))
			return 0;
		
		// Determine the type of border.
		Category hparent = null, nparent = null;
		Category htop = null, ntop = null;
		int hlevel = 0, nlevel = 0;
		if (hcat != null) {
			hlevel = cp.getLevel(hcat);
			hparent = cp.findParent(hcat);
			htop = cp.findTopCategory(hcat);
		}
		if (ncat != null) {
			nlevel = cp.getLevel(ncat);
			nparent = cp.findParent(ncat);
			ntop = cp.findTopCategory(ncat);
		}

		int level = Math.max(hlevel, nlevel);
		Stroke selectedStroke = null;
		if (hparent == null || nparent == null) {
			return 0;		// Don't draw for the coast line.
		} else {
			if (hparent.getPageID() != nparent.getPageID() &&
					hcat.getPageID() != nparent.getPageID() &&
					hparent.getPageID() != ncat.getPageID())
				level = level - 1;
			if (htop.getPageID() != ntop.getPageID())
				level = 1;
			selectedStroke = borderStrokes[level];
		}
		g.setStroke(selectedStroke);
		g.drawLine(x1, y1, x2, y2);
		return level;
	}
	
	private boolean isBorderHexagon(Hexagon h) {
		if (h.isEmpty() || isWater(h))
			return false;
		Category hcat = h.getCategory(cp);
		int alienCount = 0, fellowCount = 0;
		for (Hexagon n: canvas.findNeighbours(h)) {
			Category ncat = null;
			if (!isWater(n))
				ncat = n.getCategory(cp);
			if (!hcat.equals(ncat))
				alienCount++;
			else
				fellowCount++;
		}
		return alienCount >= 1 && fellowCount >= 1;
	}

	private boolean isWater(Hexagon h) {
		return h.getAltitude() < 0;
	}
	

	private void markCategoryBound(Category c, int x, int y) {
		if (c == null) {
			return;
		} else if (!catBounds.containsKey(c)) {
			CategoryBoundary cb = new CategoryBoundary();
			cb.diagonalX1 = cb.diagonalX2 = cb.rectX1 = cb.rectX2 = x;
			cb.diagonalY1 = cb.diagonalY2 = cb.rectY1 = cb.rectY2 = y;
			catBounds.put(c, cb);
		} else {
			CategoryBoundary cb = catBounds.get(c);
			if (x < cb.diagonalX1) {
				cb.diagonalX1 = x;
				cb.diagonalY1 = y;
			}
			if (x > cb.diagonalX2) {
				cb.diagonalX2 = x;
				cb.diagonalY2 = y;
			}
			cb.rectX1 = Math.min(cb.rectX1, x);
			cb.rectX2 = Math.max(cb.rectX2, x);
			cb.rectY1 = Math.min(cb.rectY1, y);
			cb.rectY2 = Math.max(cb.rectY2, y);
		}
	}


}
