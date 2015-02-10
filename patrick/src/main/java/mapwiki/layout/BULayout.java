package mapwiki.layout;

import mapwiki.layout.categoryprovider.NodeCategory;
import mapwiki.layout.categoryprovider.NodeCategoryProvider;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval;
import mapwiki.layout.overlapremoval.FTAOverlapRemoval.Entry;
import mapwiki.layout.overlapremoval.RectConverter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class BULayout {
	private NodeCategoryProvider cp;

    HexagonLayout2.LayoutConfig layoutcfg;

	public BULayout(NodeCategoryProvider provider, HexagonLayout2.LayoutConfig layoutcfg) {
		this.cp = provider;
        this.layoutcfg = layoutcfg;
	}
	
	public void run() {
		// Layout each level in a bottom-up approach.
		for (int i = layoutcfg.startLevel; i >= 0; i--)
			processLevel(i);
	}
	
	private void processLevel(int level) {
		// Get all nodes in this level.
		List<Category> nodesOnLevel = cp.findNodesOnLevel(level);

        for (Category ci : nodesOnLevel) {
            // Create some area with the articles assigned with itself.
            processNode(level, (NodeCategory)ci);
        }
	}

	private void processNode(int level, NodeCategory ci) {
		// Deepest level only counts for its own area.
		ci.setPivot(new Point(0, 0));
		makeAreaFromArticleNumber(level, ci);
		
		if (level != layoutcfg.startLevel) {
			List<Category> ciChildren = cp.findChildren(ci);
			
			// Run the category node and its children with the spring layout.
			SpringLayoutForCategory sl = new SpringLayoutForCategory(ci, cp, level);
			sl.run();
			
			// Perform overlap removal.
			doOverlapRemoval(level, ci, ciChildren);

			// Determine the border that contains all children.
			determineBorder(ci, ciChildren);
    	}
	}
	
	private void doOverlapRemoval(int level, final Category ci, List<Category> ciChildren) {
		// Create a list with both parent and children.
		ArrayList<Category> list = new ArrayList<Category>(ciChildren.size() + 1);
		list.add(ci);
		list.addAll(ciChildren);
		
		// Run the overlap removal with our own rectangle converter.
		//final double af = layoutcfg.areaFrac;
        final double af = 0;

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

	private void makeAreaFromArticleNumber(int level, NodeCategory c) {
		int area = c.getArea();

		if (layoutcfg.maxSizeForLevels[level] != 0 && area > layoutcfg.maxSizeForLevels[level])
			area = layoutcfg.maxSizeForLevels[level];
		
		int width = layoutcfg.layoutRectFixedSize != -1 ? layoutcfg.layoutRectFixedSize :
			(int)Math.ceil(Math.sqrt(area));
		if (width < layoutcfg.layoutRectMinSize)
			width = layoutcfg.layoutRectMinSize;

        c.setSize(new Dimension(width, width));
	}
	
	private void determineBorder(Category c, List<Category> children) {
		if (children != null && !children.isEmpty()) {
			int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
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
}
