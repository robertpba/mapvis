package mapwiki.preprocessor;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.TabbedDataEvent;
import mapwiki.common.TabbedDataListener;
import mapwiki.common.TabbedDataReader;

public final class AppendAuthorCount {
	private static PrintStream out;
	private static Map<Integer, Double> alts = new TreeMap<Integer, Double>();
	
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		op.accepts("tree").withRequiredArg().describedAs("cat_tree_file");
		op.accepts("a").withRequiredArg().describedAs("author_count_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		if (args.length == 0) {
			op.printHelpOn(System.err);
			return;
		}
		
		OptionSet opts = op.parse(args);
		System.err.println("Reading author counts...");
		readAuthorCounts((String)opts.valueOf("a"));
		
		System.err.println("Appending data...");
		out = new PrintStream((String)opts.valueOf("o"), "utf-8");
		try {
			TabbedDataReader treeReader = new TabbedDataReader(new TabbedDataListener() {
				@Override
				public void tabbedValuesRead(TabbedDataEvent event) {
					for (String s: event.getData()) {
						out.print(s);
						out.print("\t");
					}
					int pageID = Integer.parseInt(event.getData()[0]);
					out.println(alts.containsKey(pageID) ? (double)alts.get(pageID) : 0.0);
				}
			});
			treeReader.readFromStream(new FileInputStream((String)opts.valueOf("tree")));
		} finally {
			out.close();
		}
		System.err.println("Done.");
	}

	private static void readAuthorCounts(String authorFile) throws Exception {
		TabbedDataReader tdr = new TabbedDataReader(new TabbedDataListener() {
			@Override
			public void tabbedValuesRead(TabbedDataEvent event) {
				int pageID = Integer.parseInt(event.getData()[0]);
				double alt = Double.parseDouble(event.getData()[1]);
				alts.put(pageID, alt);
			}
		});
		tdr.readFromStream(new FileInputStream(authorFile));
	}
}
