package mapwiki.preprocessor;

import java.io.PrintStream;
import java.util.Arrays;

import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public final class ListTree {
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		op.accepts("tree").withRequiredArg().describedAs("tree_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		OptionSet options = op.parse(args);
		if (options.has("?") || options.has("h") || options.has("help")) {
			op.printHelpOn(System.err);
			return;
		}

		WikiTree tree = new WikiTree((String)options.valueOf("tree"));
		PrintStream out = new PrintStream((String)options.valueOf("o"), "utf-8");
		try {
			outputNode(out, tree.getRoot(), 0);
		} finally {
			out.close();
		}
	}

	private static void outputNode(PrintStream out, WikiTreeEntry node, int level) {
		for (int i = 0; i < level; i++)
			out.print('\t');
		out.print(node.getPageID());
		out.print('\t');
		out.print(node.getPageTitle());
		out.println();
		for (WikiTreeEntry child: node.getChildren())
			outputNode(out, child, level + 1);
	}
}
