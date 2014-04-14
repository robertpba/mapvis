package mapwiki.preprocessor;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.Database;
import mapwiki.common.WikiTree;
import mapwiki.common.WikiTreeEntry;

public class BuildPageData {
	private static PrintStream out;
	private Database db;
	private WikiTree tree;
	private String catTreeFile;
	private PreparedStatement ps;
	private int pagesForCategory;
	private TreeSet<Integer> usedSet = new TreeSet<Integer>();

	public BuildPageData(Database db, String catFile, int n) {
		this.db = db;
		this.catTreeFile = catFile;
		this.pagesForCategory = n;
	}

	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		Database.initOptionParser(op);
		op.accepts("n").withRequiredArg().describedAs("page_each_cat").ofType(Integer.class).defaultsTo(1);
		op.accepts("tree").withRequiredArg().describedAs("tree_file").defaultsTo("cattree.txt");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.acceptsAll(Arrays.asList("?", "h", "help"), "Shows the help screen.");
		
		OptionSet options = op.parse(args);
		if (options.has("?") || options.has("h") || options.has("help")) {
			op.printHelpOn(System.err);
			return;
		}
		
		OptionSet opts = op.parse(args);
		Database db = new Database(opts);
		String catTreeFile = (String)opts.valueOf("tree");
		String outputFile = (String)opts.valueOf("o");
		int pageEachCategory = (Integer)opts.valueOf("n");
		
		if (outputFile == null) {
			System.err.println("Please specify the output file.");
			return;
		}
		
		out = new PrintStream(outputFile, "utf-8");
		try {
			BuildPageData bpd = new BuildPageData(db, catTreeFile, pageEachCategory);
			bpd.run();
		} finally {
			out.close();
		}
	}

	public void run() throws IOException, ClassNotFoundException, SQLException {
		Connection conn = db.getConnection();
		ps = conn.prepareStatement("SELECT p.page_id, p.page_title, p.page_len FROM page p, " +
				"categorylinks c WHERE p.page_namespace = 0 AND c.cl_from = p.page_id AND " +
				"c.cl_to = ? ORDER BY page_len DESC");
		try {
			tree = new WikiTree(catTreeFile);
			findLargestPages(pagesForCategory, tree.getRoot().getChildren());
		} finally {
			ps.close();
			conn.close();
		}
	}
	
	private void findLargestPages(int topN, List<WikiTreeEntry> cats) throws SQLException, IOException {
		if (topN <= 0 || cats == null || cats.isEmpty())
			return;
		
		for (WikiTreeEntry e: cats) {
			ps.setString(1, e.getPageTitle());
			ResultSet rs = ps.executeQuery();
			try {
				int i = 0;
				while (rs.next() && i < topN) {
					int pageID = rs.getInt("page_id");
					if (usedSet.contains(pageID))
						continue;
					
					String pageTitle = new String(rs.getBytes("page_title"), "utf-8");
					String catTitle = e.getPageTitle();
					int pageLength = rs.getInt("page_len");
					outputPage(pageID, pageTitle, catTitle, pageLength, null);
					usedSet.add(pageID);
					i++;
				}
			} finally {
				rs.close();
			}
			findLargestPages(topN, e.getChildren());
		}
	}
	
	private void outputPage(int pageID, String pageTitle, String category,
			int length, String flags) {
		out.print(pageID);
		out.print("\t");
		out.print(pageTitle);
		out.print("\t");
		out.print(category);
		out.print("\t");
		out.print(length);
		out.print("\t");
		out.print(flags == null ? "" : flags);
		out.println();
	}
}
