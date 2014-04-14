package mapwiki.preprocessor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

public class QueryStats {
	private static WikiTreeEntry entry;
	private static int allCatCount;
	private static int allArticleCount;
	
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		op.accepts("tree").withRequiredArg().describedAs("tree_file");
		op.accepts("i").withRequiredArg().describedAs("category_id");
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		OptionSet options = op.parse(args);
		if (options.has("?") || options.has("h") || options.has("help")) {
			op.printHelpOn(System.err);
			return;
		}

		WikiTree tree = new WikiTree((String)options.valueOf("tree"));
		entry = tree.findByPageID(Integer.parseInt((String)options.valueOf("i")));
		if (entry == null) {
			System.err.println("Cannot find the specified category.");
			return;
		}
		
		System.out.printf("Category: %d - %s (Level %d)%n", entry.getPageID(),
				entry.getPageTitle(), entry.getLevel());
		runStats(entry.getChildren(), entry.getLevel() + 1);
		System.out.printf("Overall:\t%d\tArticle:\t%d\tAverage:\t%f%n", allCatCount,
				allArticleCount, allCatCount == 0 ? 0 : (double)allArticleCount / allCatCount);
	}

	private static void runStats(List<WikiTreeEntry> list, int level) {
		int articleCount = 0;
		int catCount = list.size();
		LinkedList<WikiTreeEntry> childrenList = new LinkedList<WikiTreeEntry>();
		for (WikiTreeEntry e: list) {
			articleCount += e.getArticleCount();
			childrenList.addAll(e.getChildren());
		}
		System.out.printf("%d Level:\t%d\tArticle:\t%d\tAverage:\t%f%n", level,
				catCount, articleCount, catCount == 0 ? 0 : (double)articleCount / catCount);
		allArticleCount += articleCount;
		allCatCount += catCount;

		if (!childrenList.isEmpty())
			runStats(childrenList, level + 1);
	}
}
