package mapwiki.preprocessor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import mapwiki.common.WikiSimilarity;
import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

public final class AggSimilarity {
	private int targetLevel = 1;
	private int levelToReach = 2;
	private String catTreeFile = "cattree.txt";
	private String cosSimFile = "cossim.txt";
	private String outputFile = "aggsim.txt";
	private List<WikiTreeEntry> targetLevelNodes = new ArrayList<WikiTreeEntry>();
	private WikiTree tree;
	private WikiSimilarity sim;
	private PrintStream out;
	
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		op.accepts("t").withRequiredArg().describedAs("target_level").ofType(Integer.class);
		op.accepts("l").withRequiredArg().describedAs("level_to_reach").ofType(Integer.class);
		op.accepts("tree").withRequiredArg().describedAs("tree_file");
		op.accepts("sim").withRequiredArg().describedAs("similarity_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		OptionSet options = op.parse(args);
		if (options.has("?") || options.has("h") || options.has("help")) {
			op.printHelpOn(System.err);
			return;
		}
		
		AggSimilarity aggSim = new AggSimilarity();
		if (options.has("t"))
			aggSim.targetLevel = (Integer)options.valueOf("t");
		if (options.has("l"))
			aggSim.levelToReach = (Integer)options.valueOf("l");
		if (options.has("tree"))
			aggSim.catTreeFile = (String)options.valueOf("tree");
		if (options.has("sim"))
			aggSim.cosSimFile = (String)options.valueOf("sim");
		if (options.has("o"))
			aggSim.outputFile = (String)options.valueOf("o");
		
		aggSim.out = new PrintStream(aggSim.outputFile, "utf-8");
		try {
			aggSim.run();
		} finally {
			aggSim.out.close();
		}
	}
	
	public void run() throws IOException {
		System.err.printf("TARGET_LEVEL=%d, LEVEL_TO_REACH=%d%n",
				targetLevel, levelToReach);
		
		tree = new WikiTree(catTreeFile);
		sim = new WikiSimilarity(cosSimFile);

		findTargetLevelNodes();
		int nodeCount = targetLevelNodes.size();
		for (int i = 0; i < nodeCount; i++) {
			for (int j = i + 1; j < nodeCount; j++) {
				WikiTreeEntry cat1 = targetLevelNodes.get(i);
				WikiTreeEntry cat2 = targetLevelNodes.get(j);
				double sim = calcAggSimilarity(cat1, cat2);
				// Print out the result.
				out.printf("%s\t%s\t%f%n", cat1.getPageTitle(), cat2.getPageTitle(), sim);
			}
		}
	}
	
	private double calcAggSimilarity(WikiTreeEntry cat1, WikiTreeEntry cat2) {
		List<WikiTreeEntry> cat1Children = new ArrayList<WikiTreeEntry>();
		cat1Children.add(cat2);
		findChildren(cat1, cat1Children);
		List<WikiTreeEntry> cat2Children = new ArrayList<WikiTreeEntry>();
		cat2Children.add(cat1);
		findChildren(cat2, cat2Children);
		System.err.printf("C1=%s (%d), C2=%s (%d)%n", cat1.getPageTitle(),
				cat1Children.size(), cat2.getPageTitle(), cat2Children.size());
		
		double sum = 0;
		int count = 0;
		for (WikiTreeEntry c1: cat1Children) {
			for (WikiTreeEntry c2: cat2Children) {
				sum += findSimilarity(c1, c2);
				count++;
				if (count % 10000 == 0)
					System.err.printf("Processed %,d records...%n", count);
			}
		}
		return count == 0 ? 0 : sum / count;
	}
	
	private double findSimilarity(WikiTreeEntry c1, WikiTreeEntry c2) {
		return sim.findSimilarity(c1.getPageID(), c2.getPageID());
	}

	private void findChildren(WikiTreeEntry cat, List<WikiTreeEntry> output) {
		if (cat.getLevel() < levelToReach) {
			output.addAll(cat.getChildren());
			for (WikiTreeEntry child: cat.getChildren())
				findChildren(child, output);
		}
	}

	private void findTargetLevelNodes()  {
		targetLevelNodes.clear();
		targetLevelNodes.addAll(tree.findByLevel((byte)targetLevel));
	}
}
