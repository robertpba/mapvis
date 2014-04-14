package mapwiki.preprocessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.Database;
import mapwiki.common.TabbedDataEvent;
import mapwiki.common.TabbedDataListener;
import mapwiki.common.TabbedDataReader;

public class CalcCosSim implements TabbedDataListener {
	private Database db;
	private PreparedStatement psCo;
	private SortedSet<CalcCosSimEntry> tree = new TreeSet<CalcCosSimEntry>();
	private String outputFile;
	private PrintStream out;
	private int depth;
	private int startFrom;

	public void run() throws SQLException, ClassNotFoundException, IOException {
		System.err.printf("Started from ID %,d at %s with %,d items.%n", startFrom,
				new Date(), tree.size());
		Connection conn = db.getConnection();
		out = new PrintStream(outputFile, "utf-8");
		try {
			int sectionCount = 0;
			int sectionSkipCount = 0;
			long totalCount = 0;
			long saveCount = 0;
			long sectionLastTick = System.currentTimeMillis();
			
			// SQL for querying co-occurrence count.
			psCo = conn.prepareStatement("SELECT COUNT(*) AS c FROM page p, " +
					"categorylinks c1, categorylinks c2 WHERE c1.cl_to = ? AND c2.cl_to = ? AND " +
					"c1.cl_from = c2.cl_from AND p.page_id = c1.cl_from AND p.page_namespace = 0");
			
			for (CalcCosSimEntry x: tree) {
				int pageIdX = x.getPageID();
				String pageTitleX = x.getPageTitle();
				int countX = x.getArticleCount();
				
				if (pageIdX < startFrom) {
					sectionSkipCount++;
					if (sectionSkipCount == 10000) {
						System.err.print(".");
						sectionSkipCount = 0;
					}
					continue;
				}
				
				for (CalcCosSimEntry y: tree.tailSet(x)) {
					int pageIdY = y.getPageID();
					String pageTitleY = y.getPageTitle();
					int countY = y.getArticleCount();
					double cosSim = 0;
					int coCount = 0;
					
					if (pageIdX == pageIdY) {
						coCount = x.getArticleCount();
						cosSim = 1;
					} else if (countX != 0 && countY != 0) {
						coCount = articleCoCount(pageTitleX, pageTitleY);
						cosSim = (double)coCount / Math.sqrt((double)countX * countY);
					}
					if (writeCosSim(pageIdX, pageIdY, cosSim, coCount))
						saveCount++;
					
					sectionCount++;
					totalCount++;
					if (sectionCount >= 50000) {
						sectionCount = 0;
						System.err.printf("Count (50k): %,d  Saved (k): %,d  Last 50k: %,ds.%n",
								totalCount / 50000, saveCount / 1000,
								(System.currentTimeMillis() - sectionLastTick) / 1000);
						sectionLastTick = System.currentTimeMillis();
					}
				}
			}
			System.err.printf("Done. Total %,d records are processed. %,d are saved.%n",
					totalCount, saveCount);
		} finally {
			conn.close();
			out.close();
		}
	}
	
	public CalcCosSim(Database db, InputStream catTreeStream,
			String output, int depth, int start) throws IOException {
		this.db = db;
		this.depth = depth;
		this.startFrom = start;
		this.outputFile = output;
		new TabbedDataReader(this).readFromStream(catTreeStream);
	}
	
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		Database.initOptionParser(op);
		op.accepts("i", "Use stdin for the cattree file.");
		op.accepts("f").withRequiredArg().describedAs("cat_tree_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.accepts("d").withRequiredArg().describedAs("depth").ofType(Integer.class).defaultsTo(999);
		op.accepts("s").withRequiredArg().describedAs("start_page_id").ofType(Integer.class).defaultsTo(999);
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		OptionSet opts = op.parse(args);
		if (args.length == 0 || opts.has("?") || opts.has("h") || opts.has("help")) {
			op.printHelpOn(System.err);
			return;
		}
		
		// Determine the input stream for reading the cattree file.
		Database db = new Database(opts);
		InputStream is;
		if (opts.has("i"))
			is = System.in;
		else if (opts.has("f"))
			is = new FileInputStream((String)opts.valueOf("f"));
		else
			throw new IllegalArgumentException("Please specify stdin or an input file.");
		
		// Other arguments.
		String output = (String)opts.valueOf("o");
		int depth = (Integer)opts.valueOf("d");
		int start = opts.has("s") ? (Integer)opts.valueOf("s") : 0;
		
		// Give the database and stream to CalcCosSim and run it.
		new CalcCosSim(db, is, output, depth, start).run();
	}
	
	private boolean writeCosSim(int pageIdX, int pageIdY, double value, int coCount) {
		if (coCount != 0 || value != 0) {
			out.printf("%d\t%d\t%f\t%d%n", pageIdX, pageIdY, value, coCount);
			return true;
		} else {
			return false;
		}
	}
	
	private int articleCoCount(String c1, String c2) throws SQLException {
		psCo.setString(1, c1);
		psCo.setString(2, c2);
		ResultSet rs = psCo.executeQuery();
		try {
			rs.next();
			return rs.getInt("c");
		} finally {
			rs.close();
		}
	}

	@Override
	public void tabbedValuesRead(TabbedDataEvent event) {
		String[] fieldValues = event.getData();
		int level = Integer.parseInt(fieldValues[2]);
		if (level <= depth) {
			CalcCosSimEntry entry = new CalcCosSimEntry();
			entry.setPageID(Integer.parseInt(fieldValues[0]));
			entry.setPageTitle(fieldValues[1]);
			entry.setArticleCount(Integer.parseInt(fieldValues[5]));
			tree.add(entry);
		}
	}
}
