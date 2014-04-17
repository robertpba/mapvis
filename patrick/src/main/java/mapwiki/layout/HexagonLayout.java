package mapwiki.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import mapwiki.common.PageDatabase;
import mapwiki.common.PageEntry;
import mapwiki.layout.hexagon.Hexagon;
import mapwiki.layout.hexagon.HexagonCanvas;
import mapwiki.layout.hexagon.HexagonText.Type;
import mapwiki.layout.mapdrawing.MapDrawer;
import mapwiki.layout.mapdrawing.MapExporter;

public class HexagonLayout implements BottomUpLayout.DrawDebugImageProvider {
	private static int cacheAreaMultiplier = -1;
	private static double cacheLogBase;
	
	private BottomUpLayout layout;
	private HexagonCanvas hexCanvas;
	private CategoryProvider cp;
	private String tempDir = System.getProperty("java.io.tmpdir") + "/MapWiki";
	private int affectRange;
	private int areaSizeCityVisible;
	private double hexSideLength;
	private int minCategorySize;
	private PageDatabase pageDB;
	private int[] counters;
	private int breakCount;
	private boolean debug;
	
	public static int computeCategorySize(int n) {
		if (n == 0)
			return 0;
		if (cacheAreaMultiplier < 0) {
			cacheAreaMultiplier = LayoutGraphic.propertyAsInteger("AREA_MULTIPLIER");
			cacheLogBase = Double.parseDouble(LayoutGraphic.propertyAsString("LOG_BASE"));
		}
		double log = Math.log10(n) / Math.log10(cacheLogBase);
		return (int)Math.ceil(log * cacheAreaMultiplier);
	}
	
	public HexagonLayout(CategoryProvider cp, PageDatabase pdb) {
		this.cp = cp;
		this.pageDB = pdb;
		this.affectRange = LayoutGraphic.affectRange;
		this.hexSideLength = Double.parseDouble(LayoutGraphic.propertyAsString("HEX_SIDE_LEN"));
		this.minCategorySize = LayoutGraphic.propertyAsInteger("MIN_SIZE_IN_HEXAGONS");
		this.areaSizeCityVisible = LayoutGraphic.propertyAsInteger("AREA_SIZE_CITY_VISIBLE");
		this.counters = new int[LayoutGraphic.layoutDepth + 1];
		this.debug = LayoutGraphic.debug;
		
		// Validate existence of the temporary directory.
		if (LayoutGraphic.tmpDir != null && !"".equals(LayoutGraphic.tmpDir.trim()))
			this.tempDir = LayoutGraphic.tmpDir;
		File tmp = new File(tempDir);
		if (!tmp.exists())
			tmp.mkdir();
	}
	
	public void drawIntermediateResult(int level, Category c, List<Category> children, int w, int h) {
		if (!debug)
			return;
		
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		try {
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, w, h);
			g.setColor(Color.GREEN);
			g.drawString(c.getPageTitle() + " - Subcount: " + children.size(), 5, 15);
			g.fillRect(c.getPivot().x, c.getPivot().y, 3, 3);
			g.drawRect(c.getPivot().x, c.getPivot().y, c.getSize().width, c.getSize().height);
			
			g.setColor(Color.RED);
			for (Category child: children) {
				Point loc = child.getLocation();
				Rectangle rect = child.drawingRect();
				g.fillRect(loc.x, loc.y, 3, 3);
				g.drawRect(rect.x, rect.y, rect.width, rect.height);
			}
			
			ImageIO.write(img, "png", getTempFilename(level + c.getPageTitle()));
		} catch (Exception ex) {
			System.err.println("Cannot write intermediate result: " + ex.getMessage());
		} finally {
			g.dispose();
		}
	}
	
	private void drawRectangleOutput() throws IOException {
		if (!debug)
			return;
		
		// Draw the rectangle drawings.
		Dimension rootSize = cp.findRoot().getBorder();
		BufferedImage img = new BufferedImage(rootSize.width, rootSize.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		try {
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, img.getWidth(), img.getHeight());
			g.setColor(Color.BLACK);
			
			// Draw rectangles output.
			System.err.println("Generating rectangle output...");
			List<Category> list = cp.findChildren(cp.findRoot());
			for (int i = 0; i < list.size(); i++) {
				Category c = list.get(i);
				Color colour = Color.decode(MapDrawer.CATEGORY_COLOURS[i % MapDrawer.CATEGORY_COLOURS.length]);
				drawCatRect(c, g, colour, 1, new Point(0, 0));
			}
			ImageIO.write(img, "png", getTempFilename("_dumpall"));
		} finally {
			g.dispose();
		}
	}
	
	public void run() throws IOException, ClassNotFoundException {
		bottomUpLayout();
		drawRectangleOutput();
		
		// Output the hexagon data if argument "exp" specified.
		MapExporter exporter = null;
		if (LayoutGraphic.exportFile != null)
			exporter = new MapExporter(LayoutGraphic.exportFile);
		
		// Output graphics.
		if (breakCount > 0)
			System.err.printf("%,d categories are not in full size.%n", breakCount);
		System.gc();
		drawOutputImage(LayoutGraphic.outputFile, exporter);
	}

	private void drawOutputImage(String outputFile, MapExporter me) throws IOException {
		// Draw the hexagon canvas into a file.
		MapDrawer mapDrawer = new MapDrawer(hexSideLength, cp, debug, me);
		mapDrawer.assignLevelCategoryCounts(counters);
		
		Dimension realImageSize = mapDrawer.computeImageSize(hexCanvas);
		System.err.printf("Creating a canvas with %dx%d (Real output size: %dx%d).%n",
				hexCanvas.getWidth(), hexCanvas.getHeight(),
				realImageSize.width, realImageSize.height);
		BufferedImage img = new BufferedImage(realImageSize.width, realImageSize.height,
				BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D g = img.createGraphics();
		
		try {
			g.setBackground(mapDrawer.oceanColour());
			g.clearRect(0, 0, img.getWidth(), img.getHeight());

			// Perform the real drawing.
			System.err.println("Drawing on the canvas...");
			hexCanvas.draw(mapDrawer, g);
			
			long imageOutputStartTime = System.currentTimeMillis();
			System.err.println("Writing image...");
			ImageIO.write(img, "png", new File(outputFile));
			System.err.printf("Done. Image output duration: %,dms%n",
					System.currentTimeMillis() - imageOutputStartTime);
		} finally {
			g.dispose();
		}
	}
	
	private List<Category> sortBySize(List<Category> list) {
		ArrayList<Category> newList = new ArrayList<Category>(list);
		Comparator<Category> comp = new Comparator<Category>() {
			@Override
			public int compare(Category c1, Category c2) {
				int i1 = c1.getArea(), i2 = c2.getArea();
				return i1 < i2 ? -1 : (i1 == i2 ? 0 : 1);
			}
		};
		Collections.sort(newList, comp);
		return newList;
	}

	private void bottomUpLayout() {
		layout = new BottomUpLayout(cp, this);
		layout.run();
		
		// Create hexagon canvas.
		Dimension rootSize = cp.findRoot().getBorder();
		int w = rootSize.width + 2 * affectRange;
		int h = rootSize.height + 2 * affectRange;
		hexCanvas = new HexagonCanvas(w, h, hexSideLength);
		
		// Assign hexagons.
		Point start = new Point(LayoutGraphic.affectRange, LayoutGraphic.affectRange);
		List<Category> list = sortBySize(cp.findChildren(cp.findRoot()));
		for (int i = 0; i < list.size(); i++) {
			Category c = list.get(i);
			makeHexagons(c, 1, start);
		}
	}
	
	private void makeHexagons(Category c, int level, Point origin) {
		if (c.getLocation() == null)
			return;
		
		Rectangle drawRect = c.drawingRect();
		Point pivot = c.getPivot();
		int px = origin.x + drawRect.x + pivot.x + c.getSize().width / 2;
		int py = origin.y + drawRect.y + pivot.y + c.getSize().height / 2;
		int areaSize = assignHexagons(c, px, py);
		
		// Don't show capital city label if the area is too small.
		if (pageDB != null && areaSize > areaSizeCityVisible) {
			PageEntry page = pageDB.findEntry(c.getPageTitle());
			if (page != null)
				hexCanvas.addTextLabel(px, py, page.getPageTitle(), 0, Type.PAGE);
		}
		
		// Increase the counter that counts nodes in each level.
		if (level < counters.length)
			counters[level]++;
		
		Point newPoint = new Point(origin.x + drawRect.x, origin.y + drawRect.y);
		for (Category subCat: sortBySize(cp.findChildren(c)))
			makeHexagons(subCat, level + 1, newPoint);
	}
	
	private Hexagon findAvailableNeighbour(Hexagon h, List<Hexagon> buffer, Category c) {
		hexCanvas.findNeighbours(h, buffer);
		for (Iterator<Hexagon> itor = buffer.iterator(); itor.hasNext(); ) {
			Hexagon n = itor.next();
			if (n.isEssential())
				itor.remove();
		}
		if (buffer.isEmpty())
			return null;
		else
			return buffer.get(LayoutGraphic.rand.nextInt(buffer.size()));
	}
	
	private int assignHexagons(Category c, int centerX, int centerY) {
		System.err.println("Assign hexagons: " + c.getPageTitle());
		int count = 1, loopCount = 0;
		int size = Math.max(c.getArea(), minCategorySize);	// Set min size to MIN_SIZE.
		
		ArrayList<Hexagon> territory = new ArrayList<Hexagon>();
		LinkedList<Hexagon> neighbourBuffer = new LinkedList<Hexagon>();
		Hexagon center = hexCanvas.get(centerX, centerY);
		center.assignCategory(c, cp.getAltitude(c), 0, Hexagon.CATEGORY_ESSENTIAL);
		territory.add(center);
		
		while (count < size) {
			Hexagon picked = territory.get(LayoutGraphic.rand.nextInt(territory.size()));
			Hexagon target = findAvailableNeighbour(picked, neighbourBuffer, c);
			if (target != null) {
				count++;
				territory.add(target);
				target.assignCategory(c, cp.getAltitude(c), 0, Hexagon.CATEGORY_ESSENTIAL);
				affectNeighbours(target, c, center);
			}
			if (loopCount++ > size * 20) {
				System.err.printf("Warning: Too many loops: %s %d/%d%n",
						c.getPageTitle(), count, size);
				breakCount++;
				break;
			}
		}
		return count;
	}

	private void affectNeighbours(Hexagon h, Category c, Hexagon centerHex) {
		int left = Math.max(0, h.x - affectRange);
		int top = Math.max(0, h.y - affectRange);
		int right = Math.min(hexCanvas.getWidth(), h.x + affectRange);
		int bottom = Math.min(hexCanvas.getHeight(), h.y + affectRange);
		
		for (int i = left; i < right; i++) {
			for (int j = top; j < bottom; j++) {
				Hexagon n = hexCanvas.get(i, j);
				double distance = pointDistance(h.x, h.y, i, j);
				double disToCenter = pointDistance(centerHex.x, centerHex.y, i, j);
				int randomLimit = LayoutGraphic.rand.nextInt(affectRange) + 1;
				if (distance < disToCenter)
					randomLimit = (int)(randomLimit * 0.75);
				if (distance <= randomLimit)
					n.assignCategory(c, cp.getAltitude(c), Math.min(distance, disToCenter), 0);
			}
		}
	}
	
	public static double pointDistance(int x1, int y1, int x2, int y2) {
		return Math.abs(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
	}
	
	private void drawCatRect(Category c, Graphics2D g, Color colour, int level, Point origin) {
		if (c.getLocation() == null)
			return;

		Rectangle drawRect = c.drawingRect();
		int a = (int)(255 * (1 - level * (1.0 / LayoutGraphic.layoutDepth)));
		g.setColor(new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), a));
		
		int x = origin.x + drawRect.x;
		int y = origin.y + drawRect.y;
		g.drawRect(x, y, drawRect.width, drawRect.height);
		if (level == 1)
			g.drawString(c.getPageTitle(), x, y);
		
		Point pivot = c.getPivot();
		if (pivot.x > 0 || pivot.y > 0) {
			int px = origin.x + drawRect.x + pivot.x;
			int py = origin.y + drawRect.y + pivot.y;
			g.fillArc(px, py, c.getSize().width, c.getSize().height, 0, 360);
		}

        int px = origin.x + drawRect.x + pivot.x + c.getSize().width / 2;
        int py = origin.y + drawRect.y + pivot.y + c.getSize().height / 2;
        System.err.printf("%d\t%s\t%d\t%d\t%d\n",
                c.getPageID(),c.getPageTitle(), level, px, py);


        //System.err.printf("id\ttitle\tlevel\tx\ty\n")

		
		for (Category subCat: cp.findChildren(c))
			drawCatRect(subCat, g, colour, level + 1, new Point(x, y));
	}
	
	private File getTempFilename(String name) {
		File file = new File(tempDir, name + ".png");
		return file.getAbsoluteFile();
	}
}
