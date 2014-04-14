package mapwiki.layout.mapdrawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;
import mapwiki.layout.LayoutGraphic;
import mapwiki.layout.hexagon.DefaultHexagonDrawer;
import mapwiki.layout.hexagon.HexDir;
import mapwiki.layout.hexagon.Hexagon;
import mapwiki.layout.hexagon.HexagonText;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval.Entry;

public class MapDrawer extends DefaultHexagonDrawer {
	public static Map<TextAttribute, Integer> UNDERLINE_FONT_ATTR = null;
	
	public static final String[] CATEGORY_COLOURS = new String[] {
		"#ff0000", "#0000ff", "#00ff00", "#ffcc00", "#990000",
		"#33ffff", "#666666", "#cc00cc", "#000000", "#cccccc" };
	
	AltitudeLevel[] altitudeLevels;
	Font[] categoryFonts;		// 1-based index array.
	Color[] categoryColours;	// 1-based index array.
	Stroke[] borderStrokes;		// 1-based index array.
	Font pageFont;
	float minAltInColour = Float.MAX_VALUE;
	
	private CategoryProvider cp;
	private boolean debug;
	private MapExporter export;
	private List<MapLabel> labelList = new LinkedList<MapLabel>();
	private Map<Category, CategoryBoundary> catBounds = new HashMap<Category, CategoryBoundary>();
	private int[] levelCategoryCounts;
	
	public MapDrawer(double hexSideLen, CategoryProvider cp, boolean debug, MapExporter exp) {
		super(hexSideLen);
		
		if (UNDERLINE_FONT_ATTR == null) {
			UNDERLINE_FONT_ATTR = new HashMap<TextAttribute, Integer>();
			UNDERLINE_FONT_ATTR.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		}
		
		this.cp = cp;
		this.debug = debug;
		this.export = exp;
		
		// Read altitude levels.
		String[] tmpAltHeight = LayoutGraphic.propertyAsString("ALT_LEVELS_HEIGHT").split(",");
		String[] tmpAltColour = LayoutGraphic.propertyAsString("ALT_LEVELS_COLOUR").split(",");
		this.altitudeLevels = new AltitudeLevel[tmpAltHeight.length];
		for (int i = 0; i < tmpAltHeight.length; i++) {
			int height = Integer.parseInt(tmpAltHeight[i]);
			if (height != 0)
				this.minAltInColour = Math.min(this.minAltInColour, height);
			this.altitudeLevels[i] = new AltitudeLevel(height, Color.decode(tmpAltColour[i]));
		}
		tmpAltHeight = null;
		tmpAltColour = null;
		
		// Read fonts for category names.
		String tmpCatFontFace = LayoutGraphic.propertyAsString("CAT_FONT_FACE");
		String[] tmpCatFontSize = LayoutGraphic.propertyAsString("CAT_FONT_SIZES").split(",");
		this.categoryFonts = new Font[tmpCatFontSize.length + 1];
		for (int i = 0; i < tmpCatFontSize.length; i++)
			this.categoryFonts[i + 1] = new Font(tmpCatFontFace, i == 0 ? Font.BOLD : Font.PLAIN,
					Integer.parseInt(tmpCatFontSize[i]));
		tmpCatFontFace = null;
		tmpCatFontSize = null;
		
		// Read colours for category names.
		String[] tmpCatColours = LayoutGraphic.propertyAsString("CAT_COLOURS").split(",");
		this.categoryColours = new Color[tmpCatColours.length + 1];
		for (int i = 0; i < tmpCatColours.length; i++)
			this.categoryColours[i + 1] = Color.decode(tmpCatColours[i]);
		tmpCatColours = null;
		
		// Read thickness values for category borders.
		String[] tmpBorderSizes = LayoutGraphic.propertyAsString("BORDER_SIZES").split(",");
		this.borderStrokes = new Stroke[tmpBorderSizes.length + 1];
		for (int i = 0; i < tmpBorderSizes.length; i++)
			this.borderStrokes[i + 1] = new BasicStroke(Float.parseFloat(tmpBorderSizes[i]));
		tmpBorderSizes = null;
		
		// Create font for pages (cities).
		this.pageFont = new Font(LayoutGraphic.propertyAsString("PAGE_FONT_FACE"), Font.ITALIC,
				LayoutGraphic.propertyAsInteger("PAGE_FONT_SIZE"));
	}
	
	public Color oceanColour() {
		return altitudeLevels[0].colour;
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
				float newAlt = Math.max(lastAlt, minAltInColour);
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
			int alt = findColour(h);
			g.setColor(altitudeLevels[alt].colour);
			g.fillPolygon(pointsX, pointsY, 6);
			
			if (export != null) {
				Category cat = h.getCategory(cp);
				export.exportHexagon(h, cat.getPageTitle(), cp.getLevel(cat), alt);
			}
		}
	}
	
	@Override
	protected void internalDrawHexagonLine(int x, int y) {
		Hexagon h = canvas.get(x, y);
		if (h.isEmpty() || isWater(h) || !isBorderHexagon(h))
			return;
		
		g.setColor(Color.BLACK);
		int ne = drawBorder(h, HexDir.NORTH_EAST, pointsX[0], pointsY[0], pointsX[1], pointsY[1]);
		int se = drawBorder(h, HexDir.SOUTH_EAST, pointsX[1], pointsY[1], pointsX[2], pointsY[2]);
		int s = drawBorder(h, HexDir.SOUTH, pointsX[2], pointsY[2], pointsX[3], pointsY[3]);
		int sw = drawBorder(h, HexDir.SOUTH_WEST, pointsX[3], pointsY[3], pointsX[4], pointsY[4]);
		int nw = drawBorder(h, HexDir.NORTH_WEST, pointsX[4], pointsY[4], pointsX[5], pointsY[5]);
		int n = drawBorder(h, HexDir.NORTH, pointsX[5], pointsY[5], pointsX[0], pointsY[0]);
		
		if (export != null && ne + se + s + sw + nw + n > 0)
			export.exportBorder(h, ne, se, s, sw, nw, n);
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
	
	private int findColour(Hexagon h) {
		// Determine colour by the altitude value.
		int result = 0;
		for (int i = 0; i < altitudeLevels.length; i++) {
			AltitudeLevel level = altitudeLevels[i];
			if (level.altitude <= h.getAltitude())
				result = i;
		}
		return result;
	}
	
	private boolean isWater(Hexagon h) {
		return h.getAltitude() < minAltInColour;
	}
	
	@Override
	protected void beforeDrawLabel() {
		System.err.println("Processing text labels...");
		labelList.clear();
		
		// Process category label first.
		for (Map.Entry<Category, CategoryBoundary> e: catBounds.entrySet()) {
			CategoryLabel cl = new CategoryLabel(this, e.getKey().getPageTitle(),
					e.getValue(), g, cp.getLevel(e.getKey()));
			labelList.add(cl);
			if (export != null)
				export.exportCatLabel(cl);
		}
		
		// Process page label.
		for (HexagonText ht: canvas.textLabels()) {
			Hexagon h = canvas.get(ht.getX(), ht.getY());
			calcPoints(h.x, h.y);
			CategoryBoundary cb = catBounds.get(h.getCategory(cp));
			if (cb == null)
				continue;
			PageLabel pl = new PageLabel(this, ht.getText(), g, cb, pointsX[0], pointsY[0]);
			labelList.add(pl);
		}
		
		// Overlap removal for page labels.
		LinkedList<MapLabel> list = new LinkedList<MapLabel>();
		for (MapLabel label: labelList) {
			Rectangle r = label.getRectangle();
			if (r != null)
				list.add(label);
		}
		FTAOverlapRemoval<MapLabel> or = new FTAOverlapRemoval<MapLabel>(list,
				new MapLabel.RectangleConverter());
		or.run();
		for (Entry<MapLabel> entry: or.entryList())
			entry.getElement().setOffset((int)entry.offsetX(), (int)entry.offsetY());
	}

	@Override
	protected void internalDrawLabel() {
		g.setColor(Color.BLACK);
		for (MapLabel lbl: labelList) {
			lbl.draw(g);
		}
		
		if (debug) {
			for (Map.Entry<Category, CategoryBoundary> e: catBounds.entrySet()) {
				g.setColor(Color.RED);
				CategoryBoundary cb = e.getValue();
				g.drawLine(cb.diagonalX1, cb.diagonalY1, cb.diagonalX2, cb.diagonalY2);
				g.drawRect(cb.rectX1, cb.rectY1, cb.rectX2 - cb.rectX1, cb.rectY2 - cb.rectY1);
				g.setColor(Color.BLUE);
				Point p = cb.rectCenter();
				g.fillArc(p.x, p.y, 8, 8, 0, 360);
			}
		}
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

	@Override
	protected void drawLegend() {
		drawParameters();
		Rectangle rect = new Rectangle(10,
				computeImageSize(canvas).height - LayoutGraphic.propertyAsInteger("LEGEND_HEIGHT") - 10, 
				LayoutGraphic.propertyAsInteger("LEGEND_WIDTH"), 
				LayoutGraphic.propertyAsInteger("LEGEND_HEIGHT"));
		Font font = new Font(LayoutGraphic.propertyAsString("LEGEND_FONT_FACE"), Font.PLAIN,
				LayoutGraphic.propertyAsInteger("LEGEND_FONT_SIZE"));
		FontMetrics fm = g.getFontMetrics(font);
		int x = rect.x + 10, y = rect.y + 10;
		
		// Draw the white background.
		g.setColor(Color.WHITE);
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g.setColor(Color.BLACK);
		g.drawRect(rect.x, rect.y, rect.width, rect.height);
		
		// Draw the title and properties.
		g.setFont(font);
		g.drawString("Legend:", x, y + fm.getAscent());
		y += 20 + fm.getHeight() + 30;
		
		// Draw category title styles.
		for (int i = 1; i <= LayoutGraphic.propertyAsInteger("LEGEND_DEPTH"); i++) {
			int count = 0;
			if (levelCategoryCounts != null && i < levelCategoryCounts.length)
				count = levelCategoryCounts[i];
			String text = String.format("Level %d Category (%,d)", i, count);
			Font catFont = categoryFonts[i];
			
			if (i == 1)
				catFont = catFont.deriveFont(UNDERLINE_FONT_ATTR);
			if (i <= 2)
				text = text.toUpperCase();
			
			g.setFont(catFont);
			g.setColor(categoryColours[i]);
			g.drawString(text, x, y + fm.getAscent());
			y += g.getFontMetrics().getHeight();
		}
		
		// Draw altitude bar title.
		y += 20;
		g.setFont(font);
		g.setColor(Color.BLACK);
		g.drawString("Average Co-author Count:", x, y + fm.getAscent());
		y += 5 + fm.getHeight();
		
		// Draw altitude bar.
		int cellWidth = (rect.width - 20) / altitudeLevels.length;
		final int cellHeight = 30;
		for (int i = 0; i < altitudeLevels.length; i++) {
			AltitudeLevel level = altitudeLevels[i];
			String cellText = Integer.toString((int)level.altitude);
			if (i == 0)
				cellText = "None";
			else if (i == altitudeLevels.length - 1)
				cellText = cellText + "+";
			
			g.setColor(level.colour);
			g.fillRect(x, y, cellWidth, cellHeight);
			g.setColor(Color.BLACK);
			g.drawString(cellText, x, y + cellHeight + fm.getAscent() + 1);
			x += cellWidth;
		}
	}

	private void drawParameters() {
		g.setColor(Color.BLACK);
		g.setFont(new Font(LayoutGraphic.propertyAsString("LEGEND_FONT_FACE"), Font.PLAIN,
				LayoutGraphic.propertyAsInteger("LEGEND_FONT_SIZE") / 2));
		FontMetrics fm = g.getFontMetrics();
		int y = 10 + fm.getAscent();
		for (String s: LayoutGraphic.propertiesToStrings()) {
			g.drawString(s, 10, y);
			y += fm.getHeight();
		}
		
		if (export != null) {
			export.exportCanvas(canvas);
			export.exportColours(altitudeLevels);
		}
	}
	
	public void assignLevelCategoryCounts(int[] values) {
		this.levelCategoryCounts = values;
	}

	@Override
	protected void drawComplete() {
		super.drawComplete();
		if (export != null)
			export.close();
	}
}
