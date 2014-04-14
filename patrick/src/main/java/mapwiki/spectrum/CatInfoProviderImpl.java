package mapwiki.spectrum;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import mapwiki.common.AggregatedSim;
import mapwiki.common.WikiSimilarity;
import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

class CatInfoProviderImpl implements CatInfoProvider {
	public static final String[] CATEGORY_COLOURS = new String[] {
		"#ff0000", "#0000ff", "#00ff00", "#ffcc00", "#990000",
		"#33ffff", "#99ff33", "#cc00cc", "#000000", "#cccccc",
		"#5200a3", "#0066ff", "#ffca7a", "#ff4d00", "#336600"};
	
	private WikiTree tree;
	private WikiSimilarity sim;
	private AggregatedSim aggSim;
	private Map<String, Integer> lookupMap = new HashMap<String, Integer>();
	private Map<String, Color> colourMap = new HashMap<String, Color>();
	
	public CatInfoProviderImpl(String treeFile, String simFile, String aggSimFile) throws IOException {
		tree = new WikiTree(treeFile);
		sim = new WikiSimilarity(simFile);
		if (aggSimFile != null)
			aggSim = new AggregatedSim(aggSimFile);
		
		int lastColour = 0;
		for (WikiTreeEntry top: tree.findByLevel((byte)1)) {
			Color c = Color.decode(CATEGORY_COLOURS[lastColour++]);
			colourMap.put(top.getPageTitle(), c);
			if (lastColour >= CATEGORY_COLOURS.length)
				lastColour = 0;
		}
	}

	@Override
	public Collection<? extends CatLink> findLinksBetweenSubCategoriesOf(String cat) {
		if (!lookupMap.containsKey(cat))
			throw new NoSuchElementException("Cannot find category ID: " + cat);
		
		int pageID = lookupMap.get(cat);
		List<WikiTreeEntry> children = tree.findByPageID(pageID).getChildren();
		LinkedList<CatLink> list = new LinkedList<CatLink>();
		for (int i = 0; i < children.size(); i++) {
			WikiTreeEntry ci = children.get(i);
			lookupMap.put(ci.getPageTitle(), ci.getPageID());
			for (int j = i + 1; j < children.size(); j++) {
				WikiTreeEntry cj = children.get(j);
				if (ci.getPageID() != cj.getPageID()) {
					list.add(new CatLink(ci.getPageTitle(), cj.getPageTitle(),
							findSimilarity(ci.getPageID(), ci.getPageTitle(), cj.getPageID(), cj.getPageTitle())));
				}
			}
		}
		return list;
	}
	
	@Override
	public Collection<? extends CatLink> findLinksBetweenTopCategories() {
		LinkedList<CatLink> list = new LinkedList<CatLink>();
		for (String c1: findTopCategories()) {
			int id1 = lookupMap.get(c1);
			for (String c2: findTopCategories()) {
				int id2 = lookupMap.get(c2);
				if (c1 != null && !c1.equals(c2) && id1 < id2)
					list.add(new CatLink(c1, c2, findSimilarity(id1, c1, id2, c2)));
			}
		}
		return list;
	}
	
	private double findSimilarity(int id1, String cat1, int id2, String cat2) {
		double result = 0;
		if (aggSim != null)
			result = aggSim.findSimilarity(cat1, cat2);
		if (result == 0)
			result = sim.findSimilarity(id1, id2);
		return result;
	}

	@Override
	public Collection<String> findTopCategories() {
		LinkedList<String> list = new LinkedList<String>();
		for (WikiTreeEntry entry: tree.findByLevel((byte)1)) {
			list.add(entry.getPageTitle());
			lookupMap.put(entry.getPageTitle(), entry.getPageID());
		}
		return list;
	}

	@Override
	public int getCoAssignedArticleCount(String cat1, String cat2) {
		int id1 = lookupMap.get(cat1);
		int id2 = lookupMap.get(cat2);
		int sum = getCoAssignedArticleCount(id1, id2);
		
		if (id1 != id2) {
			LinkedList<Integer> list1 = new LinkedList<Integer>();
			findAllChildrenDFS(tree.findByPageID(id1), list1);
			LinkedList<Integer> list2 = new LinkedList<Integer>();
			findAllChildrenDFS(tree.findByPageID(id2), list2);
			for (Integer i: list1)
				for (Integer j: list2)
					sum += getCoAssignedArticleCount(i, j);
		}
		
		return sum;
	}
	
	private void findAllChildrenDFS(WikiTreeEntry startPoint, List<Integer> list) {
		for (WikiTreeEntry child: startPoint.getChildren()) {
			list.add(child.getPageID());
			findAllChildrenDFS(child, list);
		}
	}
	
	private int getCoAssignedArticleCount(int c1, int c2) {
		return sim.findCoCount(c1, c2);
	}

	@Override
	public Color getColour(String category) {
		// Find page ID and its WikiTreeEntry.
		int id = lookupMap.get(category);
		WikiTreeEntry entry = tree.findByPageID(id);
		if (entry == null)
			return Color.WHITE;
		
		// Find its top level category.
		while (entry.getLevel() != 1)
			entry = entry.getParent();
		
		// Retrieve the colour from the map.
		if (colourMap.containsKey(entry.getPageTitle()))
			return colourMap.get(entry.getPageTitle());
		else
			return Color.GRAY;
	}
}
