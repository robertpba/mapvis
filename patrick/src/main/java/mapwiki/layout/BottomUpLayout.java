package mapwiki.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import mapwiki.layout.overlapremoval.FTAOverlapRemoval;
import mapwiki.layout.overlapremoval.RectConverter;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval.Entry;

public final class BottomUpLayout {
	private CategoryProvider cp;
	private int startLevel = 2;
	private int layoutRectMinSize;
	private int layoutRectFixedSize;
	private int[] maxSizeForLevels;
	private DrawDebugImageProvider drawDebug;
	
	public BottomUpLayout(CategoryProvider provider, DrawDebugImageProvider drawDebugProvider) {
		this.cp = provider;
		this.drawDebug = drawDebugProvider;
		this.startLevel = LayoutGraphic.layoutDepth;
		
		this.layoutRectMinSize = LayoutGraphic.propertyAsInteger("LAYOUT_RECT_MIN_SIZE");
		this.layoutRectFixedSize = LayoutGraphic.propertyAsInteger("LAYOUT_RECT_FIXED_SIZE");
		String[] tmpMaxSizeForLevels = LayoutGraphic.propertyAsString("MAX_SIZE_FOR_LEVELS").split(",");
		this.maxSizeForLevels = new int[tmpMaxSizeForLevels.length + 1];
		this.maxSizeForLevels[0] = Integer.MAX_VALUE;
		for (int i = 0; i < tmpMaxSizeForLevels.length; i++)
			this.maxSizeForLevels[i + 1] = Integer.parseInt(tmpMaxSizeForLevels[i]);
	}
	
	public void run() {
		// Layout each level in a bottom-up approach.
		for (int i = startLevel; i >= 0; i--)
			processLevel(i);
	}
	
	private void processLevel(int level) {
		// Get all nodes in this level.
		List<Category> nodesOnLevel = cp.findNodesOnLevel(level);
		
		// Find the biggest category size, for normalizing the category size.
		int maxSize = 0;
		for (Category ci: nodesOnLevel) {
			int size = cp.getDirectCount(ci);
			maxSize = Math.max(maxSize, size);
		}
		
		for (int i = 0; i < nodesOnLevel.size(); i++) {
			// Create some area with the articles assigned with itself.
			Category ci = nodesOnLevel.get(i);
			processNode(level, maxSize, ci);
		}
	}

	private void processNode(int level, int maxSizeOnLevel, Category ci) {
		// Deepest level only counts for its own area.
		ci.setPivot(new Point(0, 0));
		makeAreaFromArticleNumber(level, ci);
		
		if (level != startLevel) {
			List<Category> ciChildren = cp.findChildren(ci);
			
			// Run the category node and its children with the spring layout.
			SpringLayoutForCategory sl = new SpringLayoutForCategory(ci, cp, level);
			sl.run();
			
			// Perform overlap removal.
			doOverlapRemoval(level, ci, ciChildren);

			// Determine the border that contains all children.
			determineBorder(ci, ciChildren);
			
			// Debug drawing.
			if (drawDebug != null)
				drawDebug.drawIntermediateResult(level, ci, ciChildren,
						ci.getBorder().width, ci.getBorder().height);
		}
	}
	
	private void doOverlapRemoval(int level, final Category ci, List<Category> ciChildren) {
		// Create a list with both parent and children.
		ArrayList<Category> list = new ArrayList<Category>(ciChildren.size() + 1);
		list.add(ci);
		list.addAll(ciChildren);
		
		// Run the overlap removal with our own rectangle converter.
		final double af = LayoutGraphic.areaFrac;
		FTAOverlapRemoval<Category> or = new FTAOverlapRemoval<Category>(list, new RectConverter<Category> () {
			@Override
			public Rectangle convert(Category obj) {
				Rectangle r;
				if (!obj.equals(ci))
					r = obj.drawingRect();
				else 
					r = new Rectangle(obj.getPivot().x, obj.getPivot().y,
							obj.getSize().width, obj.getSize().height);
				r.width = (int) Math.ceil(r.width * af);
				r.height = (int) Math.ceil(r.height * af);
				return r;
			}
		});
		or.run();
		
		// Find the minimum X and Y values to adjust, so that nodes will not move out of screen.
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Entry<Category> entry: or.entryList()) {
			minX = Math.min(minX, entry.getNewRect().x);
			minY = Math.min(minY, entry.getNewRect().y);
		}
		
		// Done. Loop for every node to adjust their location.
		for (Entry<Category> entry: or.entryList()) {
			Category c = entry.getElement();
			Point p = c.equals(ci) ? c.getPivot() : c.getLocation();
			double ox = entry.offsetX() - minX;
			double oy = entry.offsetY() - minY;
			p.x += ox;
			p.y += oy;
		}
	}

	private void makeAreaFromArticleNumber(int level, Category c) {
		int articleCount = cp.getDirectCount(c);
		int area = (int)HexagonLayout.computeCategorySize(articleCount);
		if (maxSizeForLevels[level] != 0 && area > maxSizeForLevels[level])
			area = maxSizeForLevels[level]; 
		
		int width = layoutRectFixedSize != -1 ? layoutRectFixedSize :
			(int)Math.ceil(Math.sqrt(area));
		if (width < layoutRectMinSize)
			width = layoutRectMinSize;
		
		c.setSize(new Dimension(width, width));
		c.setArticleCount(articleCount);
		c.setArea(area);
	}
	
	private void determineBorder(Category c, List<Category> children) {
		if (children != null && !children.isEmpty()) {
			int minX = Integer.MAX_VALUE, minY = minX;
			int maxX = Integer.MIN_VALUE, maxY = maxX;
			for (int i = -1; i < children.size(); i++) {
				Category current = i < 0 ? c : children.get(i);
				Rectangle r = current.drawingRect();
				int left = r.x;
				int top = r.y;
				int right = r.x + r.width + 1;
				int down = r.y + r.height + 1;
				if (left < minX)
					minX = left;
				if (top < minY)
					minY = top;
				if (right > maxX)
					maxX = right;
				if (down > maxY)
					maxY = down;
			}
			c.setBorder(new Dimension(maxX - minX, maxY - minY));
		} else {
			c.setBorder(c.getSize());
		}
	}
	
	interface DrawDebugImageProvider {
		void drawIntermediateResult(int level, Category c, List<Category> children, int w, int h);
	};
}
