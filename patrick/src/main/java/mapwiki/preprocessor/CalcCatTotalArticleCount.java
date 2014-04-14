package mapwiki.preprocessor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

public class CalcCatTotalArticleCount {
	private WikiTree tree;
	private List<WikiTreeEntry> topLevelCategories = new LinkedList<WikiTreeEntry>();;
	private Map<WikiTreeEntry, Integer> counts = new HashMap<WikiTreeEntry, Integer>();
	
	public static void main(String[] args) throws Exception {
		String filename = args.length == 0 ? "cattree.txt" : args[0];
		WikiTree wt = new WikiTree(filename);
		new CalcCatTotalArticleCount(wt).run();
	}
	
	public CalcCatTotalArticleCount(WikiTree tree) {
		this.tree = tree;
	}
	
	public void run() {
		findTopLevelCategories();
		for (WikiTreeEntry category: topLevelCategories) {
			counts.put(category, 0);
			sumChildrenCount(category.getPageID(), category);
			System.out.printf("%d\t%s\t%d%n", category.getPageID(), category.getPageTitle(),
					counts.get(category));
		}
	}

	private void sumChildrenCount(int parent, WikiTreeEntry topLevelCat) {
		List<Integer> childrenPageIDs = new LinkedList<Integer>();
		WikiTreeEntry parentEntry = tree.findByPageID(parent);
		for (WikiTreeEntry child: parentEntry.getChildren()) {
			childrenPageIDs.add(child.getPageID());
			counts.put(topLevelCat, counts.get(topLevelCat) + child.getArticleCount());
		}
		for (Integer child: childrenPageIDs)
			sumChildrenCount(child, topLevelCat);
	}

	private void findTopLevelCategories() {
		topLevelCategories.addAll(tree.findByLevel((byte)1));
	}
}
