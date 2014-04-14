package mapwiki.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import mapwiki.common.Tick;
import mapwiki.spectrum.AddDirection;
import mapwiki.spectrum.Spectrum;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout3;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

class SpringLayoutForCategory {
	public enum CenterNodeMode { LARGEST, MOST_RELATED, SMALLEST };
	
	private Category mainNode;
	private Category rootNode;
	private boolean isTopLevel;
	private Graph<Category, SpringLayoutLink> g;
	private SpringLayout<Category, SpringLayoutLink> sl;
	private CategoryProvider cp;
	private Dimension layoutSize;
	private Point centerPoint;
	private int canvasWidth;
	private int springLength;
	private int maxIterationTimes;
	private double minWeightToParent;
	private double maxSimilarityThisRound;
	private int repulsionRange;
	private int level;
	private CenterNodeMode centerMode;
	private Category centerNode;
	private PrintStream systemErrStream = System.err;
	private PrintStream nullErrStream = new PrintStream(new NullOutputStream());
	
	public SpringLayoutForCategory(Category cat, CategoryProvider provider, int lv) {
		maxIterationTimes = LayoutGraphic.propertyAsInteger("MAX_ITERATION_TIMES");
		minWeightToParent = Double.parseDouble(LayoutGraphic.propertyAsString("MIN_WEIGHT_TO_PARENT"));
		repulsionRange = LayoutGraphic.propertyAsInteger("REPULSION_RANGE");
		mainNode = cat;
		rootNode = provider.findRoot();
		isTopLevel = cat.getPageID() == rootNode.getPageID();
		cp = provider;
		level = lv;
		centerMode = LayoutGraphic.centeringMode;
	}
	
	public void run() {
		initLayout();
		ArrayList<Category> catList = new ArrayList<Category>();
		ArrayList<SpringLayoutLink> linkList = new ArrayList<SpringLayoutLink>();
		TreeSet<SpringLayoutLink> linksForSort = new TreeSet<SpringLayoutLink>(new ReverseLinkComparator());
		List<Category> children = cp.findChildren(mainNode);
		
		// Add the category itself as a center point into the layout.
		if (!isTopLevel) {
			mainNode.setLocation(centerPoint);
			mainNode.setPivot(centerPoint);
			catList.add(mainNode);
			g.addVertex(mainNode);
		}
		
		// Process sub-category data.
		int subAmount = 0, lastSize = 0;
		centerNode = null;
		HashMap<Category, Integer> subCounts = new HashMap<Category, Integer>(children.size());
		for (Category child: children) {
			int n = cp.findChildren(child).size() + 1;	// Ensure no zero values.
			subAmount += n;
			subCounts.put(child, n);
			catList.add(child);
			if ((centerMode == CenterNodeMode.LARGEST && centerNode == null || n > lastSize) ||
				(centerMode == CenterNodeMode.SMALLEST && centerNode == null || n < lastSize)) {
				lastSize = n;
				centerNode = child;
			}
		}
		
		// Add their mutual similarities to a temporary list.
		for (int i = 0; i < catList.size(); i++) {
			Category ci = catList.get(i);
			for (int j = 0; j < catList.size(); j++) {
				Category cj = catList.get(j);
				if (i == 0 && j != 0) {
					// If it's the parent and the child relationship.
					double sim = cp.getSimilarity(ci, cj);
					if (i == 0 && sim < minWeightToParent)
						sim = minWeightToParent;
					SpringLayoutLink l = new SpringLayoutLink(sim, ci, cj);
					linkList.add(l);
				} else if (i > 0 && j > 0 && ci.getPageID() < cj.getPageID()) {
					// Linkages between children.
					double sim = cp.getSimilarity(ci, cj);
					SpringLayoutLink l = new SpringLayoutLink(sim, ci, cj);
					linkList.add(l);
					linksForSort.add(l);
				}
			}
		}
		
		// Determine the angle of each region by their number of sub-categories.
		double sliceAngle;
		if (isTopLevel) {
			assert subAmount != 0;
			sliceAngle = 360.0 / (subAmount - lastSize);
		} else if (subAmount == 0) {
			sliceAngle = 360.0;
		} else {
			sliceAngle = 360.0 / subAmount;
		}
		
		// Add its children into the graph.
		double angle = -90;
		int radius = springLength / 2;
		Collection<Category> sp = arrangeSpectrum(children, linksForSort); 
		
		if (centerMode == CenterNodeMode.MOST_RELATED && !sp.isEmpty())
			centerNode = sp.iterator().next();
		
		for (Category c: sp) {
			int x, y;
			if (isTopLevel && c.equals(centerNode)) {
				// Place it in the center...
				x = centerPoint.x;
				y = centerPoint.y;
			} else {
				// OR Place the children around the parent.
				double a = Math.toRadians(angle);
				angle += sliceAngle * subCounts.get(c);
				x = (int)(centerPoint.x + radius * Math.cos(a));
				y = (int)(centerPoint.y + radius * Math.sin(a));
			}
			c.setLocation(new Point(x, y));
			g.addVertex(c);
		}
		linksForSort = null;	// Release memory.
		
		// Add only related edges into the graph.
		Tick t = new Tick();
		System.err.append("Spring layout: ").println(mainNode.getPageTitle());
		for (SpringLayoutLink link: linkList) {
			if (link.isRelated()) {
				g.addEdge(link, link.getCategory1(), link.getCategory2(), EdgeType.UNDIRECTED);
				maxSimilarityThisRound = Math.max(maxSimilarityThisRound, link.getSimilarity());
				System.err.printf("[%.7s %.7s %d] ", link.getCategory1().getPageTitle(),
						link.getCategory2().getPageTitle(), computeLinkDistance(link));
				// Wrap the line every 3 records.
				if (t.nextIs(3))
					System.err.println();
			}
		}
		System.err.println();
		
		// Run layout iteration.
		int loopCount = 0;
		System.setErr(nullErrStream);
		try {
			while (loopCount++ < maxIterationTimes)
				sl.step();
		} catch (Throwable ex) {
			// Print the exception to the real System.err stream.
			ex.printStackTrace(systemErrStream);
		} finally {
			System.setErr(systemErrStream);
		}
		
		// Set the final location back to the nodes.
		for (Category c: catList) {
			if (c.equals(mainNode))
				continue;
			int cx = (int) Math.round(sl.getX(c));
			int cy = (int) Math.round(sl.getY(c));
			c.setLocation(new Point(cx, cy));
		}
		if (isTopLevel) {
			rootNode.setLocation(centerPoint);
			rootNode.setPivot(centerPoint);
		}
	}
	
	private void initLayout() {
		prepareSpringLengths();
		layoutSize = new Dimension(canvasWidth, canvasWidth);
		centerPoint = new Point(layoutSize.width / 2, layoutSize.height / 2);
		g = createGraph();
		sl = createLayout();
	}
	
	private void prepareSpringLengths() {
		List<Category> children = cp.findChildren(mainNode);
		double metric = children.size() + 1;
		if (level < LayoutGraphic.layoutDepth - 1) {
			// The lowest level uses sub-category count as metric.
			// Other levels use sub-category area size as metric.
			double sum = 0;
			for (Category c: children) {
				Dimension d = c.getBorder();
				sum += Math.sqrt(Math.pow(d.width, 2) + Math.pow(d.height, 2)) / 2;
			}
			metric = sum / (Math.PI * 3);
		}
		canvasWidth = (int) (2.0 * metric);
		springLength = (int) (0.87 * metric);
	}
	
	private Collection<Category> arrangeSpectrum(List<Category> categories, SortedSet<SpringLayoutLink> links) {
		Spectrum<Category> sp = new Spectrum<Category>();
		if (categories.isEmpty() || links.isEmpty())
			return sp;
		
		// Insert the categories with the highest similarity at the center.
		SpringLayoutLink entry = links.first();
		Category currentLeft = entry.getCategory1();
		Category currentRight = entry.getCategory2();
		sp.add(currentLeft);
		sp.add(currentRight);
		links.remove(entry);
		if (centerMode == CenterNodeMode.MOST_RELATED)
			centerNode = currentLeft;
		
		while (!links.isEmpty()) {
			// Find suitable category that matches either the left or right node.
			AddDirection dir = null;
			Category current = null;
			for (SpringLayoutLink cl: links) {
				entry = cl;
				if (currentLeft.equals(cl.getCategory1())) {
					current = cl.getCategory2();
					dir = AddDirection.LEFT;
					break;
				} else if (currentLeft.equals(cl.getCategory2())) {
					current = cl.getCategory1();
					dir = AddDirection.LEFT;
					break;
				} else if (currentRight.equals(cl.getCategory1())) {
					current = cl.getCategory2();
					dir = AddDirection.RIGHT;
					break;
				} else if (currentRight.equals(cl.getCategory2())) {
					current = cl.getCategory1();
					dir = AddDirection.RIGHT;
					break;
				}
			}
			
			// Either left or right can match the pair.
			if (current != null) {
				if (dir == AddDirection.LEFT) {
					sp.addLeft(current);
					currentLeft = current;
				} else {
					sp.addRight(current);
					currentRight = current;
				}
			} else {
				entry = links.first();
				if (sp.addLeft(entry.getCategory1()))
					currentLeft = entry.getCategory1();
				if (sp.addRight(entry.getCategory2()))
					currentRight = entry.getCategory2();
			}
			
			// Modify the spectrum.
			links.remove(entry);
		}
		return sp;
	}
	
	private int computeLinkDistance(SpringLayoutLink link) {
		double sim = link.getSimilarity();
		double distance = springLength * (1 - sim / maxSimilarityThisRound);
		return (int)Math.ceil(distance);
	}
	
	private SpringLayout<Category, SpringLayoutLink> createLayout() {
		Transformer<SpringLayoutLink, Integer> lengthFunc = new Transformer<SpringLayoutLink, Integer>() {
			@Override
			public Integer transform(SpringLayoutLink arg0) {
				int distance = computeLinkDistance(arg0);
				return distance;
			}
		};
		
		Transformer<Category, Point2D> pointFunc = new Transformer<Category, Point2D>() {
			@Override
			public Point2D transform(Category c) {
				if (c.getLocation() != null)
					return c.getLocation();
				else
					return new Point2D.Double(LayoutGraphic.rand.nextDouble() * layoutSize.width,
							LayoutGraphic.rand.nextDouble() * layoutSize.height);
			}
		};
		
		SpringLayout<Category, SpringLayoutLink> sl = 
			new SpringLayout3<Category, SpringLayoutLink>(g, lengthFunc, LayoutGraphic.rand);
		sl.setInitializer(pointFunc);
		sl.setRepulsionRange(repulsionRange);
		sl.setSize(layoutSize);
		return sl;
	}
	
	private Graph<Category, SpringLayoutLink> createGraph() {
		return new UndirectedSparseGraph<Category, SpringLayoutLink>();
	}
	
	public static class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			// Ignore the content written to this stream.
		}
	}
	
	public static class ReverseLinkComparator implements Comparator<SpringLayoutLink> {
		@Override
		public int compare(SpringLayoutLink link1, SpringLayoutLink link2) {
			// Reverse the order.
			return link2 == null ? -1 : link2.compareTo(link1);
		}
	}
}