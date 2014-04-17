package mapwiki.layout.categoryprovider;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import mapwiki.common.AggregatedSim;
import mapwiki.common.WikiSimilarity;
import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;
import mapwiki.layout.Category;
import mapwiki.layout.CategoryProvider;

public class MemoryCategoryProvider implements CategoryProvider {
	private WikiTree tree;
	private WikiSimilarity sim;
	private AggregatedSim aggSim;
	private MemoryCategory rootCategory;
	private Map<Integer, MemoryCategory> mapID = new TreeMap<Integer, MemoryCategory>();
	private Map<String, MemoryCategory> mapTitle = new TreeMap<String, MemoryCategory>();
	
	public MemoryCategoryProvider(WikiTree tree, WikiSimilarity sim, AggregatedSim aggSim) {
		this.tree = tree;
		this.sim = sim;
		this.aggSim = aggSim;
		WikiTreeEntry rootEntry = tree.getRoot();
		
		// Create a MemoryCategory object for each WikiTreeEntry, and add them
		// to a lookup map.
		rootCategory = initForEntry(rootEntry, null);
	}
	
	private MemoryCategory initForEntry(WikiTreeEntry entry, MemoryCategory topCat) {
		MemoryCategory mc = new MemoryCategory(entry);
		if (entry.getLevel() == 1)
			topCat = mc;
		mc.topCategory = topCat;
		mapID.put(entry.getPageID(), mc);
		mapTitle.put(entry.getPageTitle(), mc);
		for (WikiTreeEntry child: entry.getChildren()) {
			mc.children.add(initForEntry(child, topCat));
		}
		return mc;
	}

	@Override
	public List<Category> findChildren(Category c) {
		return Collections.unmodifiableList(((MemoryCategory)c).children);
	}

	@Override
	public List<Category> findNodesOnLevel(int level) {
		LinkedList<Category> output = new LinkedList<Category>();
		for (WikiTreeEntry entry: tree.findByLevel((byte)level)) {
			MemoryCategory mc = mapID.get(entry.getPageID()); 
			if (mc != null)
				output.add(mc);
		}
		return Collections.unmodifiableList(output);
	}

	@Override
	public Category findParent(Category c) {
		MemoryCategory mc = (MemoryCategory)c;
		return mapID.get(mc.treeEntry.getParent().getPageID());
	}

	@Override
	public Category findRoot() {
		return rootCategory;
	}

	@Override
	public int getCoCount(Category c1, Category c2) {
		return sim.findCoCount(c1.getPageID(), c2.getPageID());
	}

	@Override
	public int getDirectCount(Category c) {
		return ((MemoryCategory)c).treeEntry.getArticleCount();
	}

	@Override
	public double getSimilarity(Category c1, Category c2) {
		if (aggSim != null) {
			double value = aggSim.findSimilarity(c1.getPageTitle(), c2.getPageTitle());
			if (value != 0)
				return value;
		}
		return sim.findSimilarity(c1.getPageID(), c2.getPageID());
	}

	@Override
	public Category findTopCategory(Category c) {
		return ((MemoryCategory)c).topCategory;
	}

	@Override
	public Category findByID(int pageID) {
		return mapID.get(pageID);
	}

	@Override
	public Category findByTitle(String pageTitle) {
		return mapTitle.get(pageTitle);
	}

	@Override
	public int getLevel(Category c) {
		return ((MemoryCategory)c).treeEntry.getLevel();
	}

	@Override
	public float getAltitude(Category c) {
        // TODO remove it
		return  100;
        //return ((MemoryCategory)c).treeEntry.getAltitude();
	}
}
