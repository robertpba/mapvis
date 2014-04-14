package mapwiki.preprocessor;

import java.io.FileInputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.Database;
import mapwiki.common.TabbedDataEvent;
import mapwiki.common.TabbedDataListener;
import mapwiki.common.TabbedDataReader;
import mapwiki.common.Tick;

public class CalcCosSim2 {
	private static Database db;
	private static String outputFile;
	private static PrintStream out;
	private static int depth;
	
	private static long saveCount;
	private static long totalCount;
	
	private static Map<String, CalcCosSim2Category> tree = new TreeMap<String, CalcCosSim2Category>();
	private static Map<Long, CalcCosSim2Pair> pairs = new TreeMap<Long, CalcCosSim2Pair>();
	
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		Database.initOptionParser(op);
		op.accepts("f").withRequiredArg().describedAs("cat_tree_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.accepts("d").withRequiredArg().describedAs("depth").ofType(Integer.class).defaultsTo(999);
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		// Process arguments.
		OptionSet opts = op.parse(args);
		if (args.length == 0 || opts.has("?") || opts.has("h") || opts.has("help")) {
			op.printHelpOn(System.err);
			return;
		} else if (!opts.has("f") || opts.valueOf("f") == null) {
			throw new IllegalArgumentException("Please specify an input file.");
		}
		outputFile = (String)opts.valueOf("o");
		depth = (Integer)opts.valueOf("d");
		
		// Routine to read the plain text category tree.
		db = new Database(opts);
		TabbedDataReader reader = new TabbedDataReader(new TabbedDataListener() {
			@Override
			public void tabbedValuesRead(TabbedDataEvent event) {
				String[] fieldValues = event.getData();
				int level = Integer.parseInt(fieldValues[2]);
				if (level <= depth) {
					String pageTitle = fieldValues[1];
					CalcCosSim2Category entry = new CalcCosSim2Category();
					entry.setPageID(Integer.parseInt(fieldValues[0]));
					entry.setArticleCount(Integer.parseInt(fieldValues[5]));
					tree.put(pageTitle, entry);
				}
			}
		});
		reader.readFromStream(new FileInputStream((String)opts.valueOf("f")));
		
		// Main program to compute similarities.
		System.err.printf("Started at %s with %,d categories.%n", new Date(), tree.size());
		out = new PrintStream(outputFile, "utf-8");
		try {
			run();
		} finally {
			out.close();
		}
		System.err.printf("Done. Total %,d articles are processed. %,d are saved.%n",
				totalCount, saveCount);
	}
	
	private static void run() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
		writeCategoriesCosSim();
		summarisePairs();
		writePairs();
	}

	private static void summarisePairs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
		ArrayList<String> catLinkList = new ArrayList<String>(50);
		Connection conn = db.getConnection();
		try {
			PreparedStatement page = conn.prepareStatement("SELECT page_id FROM page WHERE page_namespace = 0");
			PreparedStatement catLink = conn.prepareStatement("SELECT cl_to FROM categorylinks WHERE cl_from = ?");
			
			long sectionLastTick = System.currentTimeMillis();
			System.err.print("Querying for all pages... ");
			ResultSet pageRS = page.executeQuery();
			System.err.printf("Done (%.3fs)%n", (System.currentTimeMillis() - sectionLastTick) / 1000.0);
			
			Tick sectionCount = new Tick();
			sectionLastTick = System.currentTimeMillis();
			while (pageRS.next()) {
				int pageID = pageRS.getInt(1);
				catLinkList.clear();
				catLink.setInt(1, pageID);
				
				ResultSet catLinkRS = catLink.executeQuery();
				while (catLinkRS.next())
					catLinkList.add(new String(catLinkRS.getBytes(1), "utf-8"));
				catLinkRS.close();
				
				if (!catLinkList.isEmpty())
					processCatList(catLinkList);
				totalCount++;
				
				if (sectionCount.nextIs(1000)) {
					System.err.printf("Count: %,d  Pair: %,d  Last 1k: %,ds.%n",
							totalCount, pairs.size(),
							(System.currentTimeMillis() - sectionLastTick) / 1000);
					sectionLastTick = System.currentTimeMillis();
				}
			}
			pageRS.close();
		} finally {
			conn.close();
		}
	}

	private static void processCatList(List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String cat1 = list.get(i);
			CalcCosSim2Category obj1 = tree.get(cat1);
			if (obj1 == null || obj1.getArticleCount() == 0)
				continue;
			
			for (int j = i + 1; j < list.size(); j++) {
				String cat2 = list.get(j);
				CalcCosSim2Category obj2 = tree.get(cat2);
				if (obj2 == null || obj2.getArticleCount() == 0)
					continue;
				
				long key = BitwiseLong.makeKey(obj1.getPageID(), obj2.getPageID());
				CalcCosSim2Pair p = pairs.get(key);
				if (p == null) 
					pairs.put(key, new CalcCosSim2Pair(obj1.getArticleCount(),
							obj2.getArticleCount()));
				else
					p.increaseCoCount();
			}
		}
	}

	private static void writeCategoriesCosSim() {
		System.err.println("Writing default similarities of categories themselves...");
		for (Map.Entry<String, CalcCosSim2Category> e: tree.entrySet()) {
			CalcCosSim2Category c = e.getValue();
			if (writeCosSim(c.getPageID(), c.getPageID(), 1.0, c.getArticleCount()))
				saveCount++;
		}
	}
	
	private static void writePairs() {
		System.err.print("Writing similarity pairs");
		Tick t = new Tick();
		for (Map.Entry<Long, CalcCosSim2Pair> e: pairs.entrySet()) {
			int pageID1 = BitwiseLong.hiPart(e.getKey());
			int pageID2 = BitwiseLong.loPart(e.getKey());
			CalcCosSim2Pair pair = e.getValue();
			if (writeCosSim(pageID1, pageID2, pair.cosSim(), pair.getCoCount()))
				saveCount++;
			if (t.nextIs(10000))
				System.err.print(".");
		}
		System.err.println();
	}

	private static boolean writeCosSim(int pageIdX, int pageIdY, double value, int coCount) {
		if (coCount != 0 || value != 0) {
			out.printf("%d\t%d\t%f\t%d%n", pageIdX, pageIdY, value, coCount);
			return true;
		} else {
			return false;
		}
	}
}
