package mapwiki.preprocessor;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

public class GenUselessList {
	private static ArrayList<String> keywords = new ArrayList<String>();
	private static long count;
	private static double similarityAsList = 0.8;

	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		op.accepts("tree").withRequiredArg().describedAs("tree_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.accepts("k").withRequiredArg().describedAs("ignore_keywords");
		op.accepts("sl").withRequiredArg().describedAs("similarity_limit_for_lists");
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		OptionSet options = op.parse(args);
		if (options.has("?") || options.has("h") || options.has("help")) {
			op.printHelpOn(System.err);
			return;
		}

		for (Object obj: options.valuesOf("k"))
			keywords.add(obj.toString().toUpperCase());
		if (options.has("sl"))
			similarityAsList = Double.parseDouble((String)options.valueOf("sl"));
		
		WikiTree tree = new WikiTree((String)options.valueOf("tree"));
		PrintStream out = new PrintStream((String)options.valueOf("o"), "utf-8");
		try {
			processNode(out, tree.getRoot());
			System.err.printf("Found %,d categories.%n", count);
		} finally {
			out.close();
		}
	}
	
	private static void output(PrintStream out, String node) {
		out.println(node);
	}
	
	private static void processNode(PrintStream out, WikiTreeEntry node) {
		ArrayList<String> childNames = new ArrayList<String>();
		for (WikiTreeEntry child: node.getChildren()) {
			if (isIgnoreByKeywords(child)) {
				output(out, child.getPageTitle());
				System.err.printf("Ignore by keywords:\t%s%n", child.getPageTitle());
				count++;
			} else {
				childNames.add(child.getPageTitle());
			}
		}
		
		if (childNames.size() >= 2) {
			double avgSim = 0;
			for (int i = 0; i < childNames.size() - 2; i++) {
				String s1 = childNames.get(i);
				String s2 = childNames.get(i + 1);
				avgSim += sim(s1, s2);
			}
			avgSim = avgSim / (childNames.size() - 1);
			
			if (avgSim > similarityAsList) {
				for (String s: childNames) {
					output(out, s);
					System.err.printf("Ignore by AutoSim:\t%s\tParent=%s,AvgSim=%f%n",
							s, node.getPageTitle(), avgSim);
					count++;
				}
			}
		}
		
		for (WikiTreeEntry child: node.getChildren())
			processNode(out, child);
	}

	private static boolean isIgnoreByKeywords(WikiTreeEntry entry) {
		for (String s: keywords)
			if (entry.getPageTitle().toUpperCase().contains(s))
				return true;
		return false;
	}
	
	private static double sim(String s1, String s2) {
		s1 = s1.toUpperCase();
		s2 = s2.toUpperCase();
		
		HashMap<Character, Integer> c1 = new HashMap<Character, Integer>();
		for (int i = 0; i < s1.length(); i++) {
			char c = s1.charAt(i);
			c1.put(c, c1.containsKey(c) ? c1.get(c) + 1 : 1);
		}
		
		HashMap<Character, Integer> c2 = new HashMap<Character, Integer>();
		for (int i = 0; i < s2.length(); i++) {
			char c = s2.charAt(i);
			c2.put(c, c2.containsKey(c) ? c2.get(c) + 1 : 1);
		}
		
		int count1 = 0;
		for (Integer i: c1.values())
			count1 += i * i;
		
		int count2 = 0;
		for (Integer j: c2.values())
			count2 += j * j;

		int co = 0;
		for (Character c: c1.keySet())
			if (c2.containsKey(c))
				co += c1.get(c) * c2.get(c);
		
		return co / Math.sqrt(count1 * count2);
	}
}
