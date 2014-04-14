package mapwiki.preprocessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import mapwiki.common.Database;

public class BuildCategoryTree {
	public enum PruneMode { BY_PAGE_ID, BY_SIM };
	
	private int rootID;
	private String rootTitle;
	private Database db;
	private Set<String> blacklist = new HashSet<String>();
	public String outputFile;
	private PrintStream out;
	private PruneMode mode;
	private String catAuthorTable;
	
	// Prepared statements.
	private PreparedStatement stmtSubCat;
	private PreparedStatement stmtCount;
	private PreparedStatement stmtCo;
	private PreparedStatement stmtAuthor;
	private Map<Integer, BuildCategoryTreeNode> result;
	
	public static void main(String[] args) throws Exception {
		OptionParser op = new OptionParser();
		Database.initOptionParser(op);
		op.accepts("i").withRequiredArg().describedAs("root_page_id").ofType(Integer.class);
		op.accepts("t").withRequiredArg().describedAs("root_page_title");
		op.accepts("b").withRequiredArg().describedAs("blacklist_file");
		op.accepts("o").withRequiredArg().describedAs("output_file");
		op.accepts("at").withRequiredArg().describedAs("no_of_author_in_a_cat_table");
		op.accepts("cs", "prune_with_similarity");
		
		if (args.length == 0) {
			op.printHelpOn(System.err);
			return;
		}
		OptionSet opts = op.parse(args);
		
		int rootID = 137597;
		String rootTitle = "Articles";
		Database db = new Database(opts);
		if (opts.has("i"))
			rootID = (Integer)opts.valueOf("i");
		if (opts.has("t"))
			rootTitle = (String)opts.valueOf("t");
		
		BuildCategoryTree bct = new BuildCategoryTree(rootID, rootTitle, db,
				opts.has("cs") ? PruneMode.BY_SIM : PruneMode.BY_PAGE_ID,
				opts.has("at") ? (String)opts.valueOf("at") : "");
		if (opts.has("b"))
			bct.initBlacklist((String)opts.valueOf("b"));
		if (opts.has("o"))
			bct.outputFile = (String)opts.valueOf("o");
		bct.run();
	}
	
	public BuildCategoryTree(int rootPageID, String rootPageTitle,
			Database db, PruneMode pm, String authorTable) {
		this.rootID = rootPageID;
		this.rootTitle = rootPageTitle;
		this.db = db;
		this.mode = pm;
		this.catAuthorTable = authorTable;
	}
	
	public void run() throws SQLException, ClassNotFoundException, IOException {
		System.err.println("Building category tree...");
		Queue<BuildCategoryTreeNode> queue = new LinkedList<BuildCategoryTreeNode>();
		result = new TreeMap<Integer, BuildCategoryTreeNode>();
		int pathFound = 0, pathCut = 0;
		
		// Create the root "Articles" node.
		// Run SQL to get page_id: SELECT * FROM page WHERE page_title = 'Articles' AND
		//      page_namespace = 14
		BuildCategoryTreeNode root = new BuildCategoryTreeNode(rootID, rootTitle);
		System.err.printf("Root node: %d %s%n", rootID, rootTitle);
		queue.add(root);
		
		out = new PrintStream(outputFile, "utf-8");
		Connection conn = db.getConnection();
		
		// For getting children of a category.
		stmtSubCat = conn.prepareStatement("SELECT cl_from, page_title " +
				"FROM categorylinks, page WHERE cl_to = ? AND cl_from = page_id AND " +
				"page_namespace = 14 ORDER BY page_id");
		// For finding number of articles directly in the category.
		stmtCount = conn.prepareStatement("SELECT COUNT(*) AS c " +
				"FROM categorylinks, page WHERE cl_to = ? AND cl_from = page_id AND " +
				"page_namespace = 0");
		// For finding number of co-assigned articles between two categories.
		stmtCo = conn.prepareStatement("SELECT COUNT(*) AS c FROM page p, " +
				"categorylinks c1, categorylinks c2 WHERE c1.cl_to = ? AND c2.cl_to = ? AND " +
				"c1.cl_from = c2.cl_from AND p.page_id = c1.cl_from AND p.page_namespace = 0");
		// For finding number of co-assigned articles between two categories.
		stmtAuthor = conn.prepareStatement("SELECT avg_author FROM " + catAuthorTable +
				" WHERE cat_page_id = ?");
		
		try {
			// Write the root node.
			root.write(out);
			
			BuildCategoryTreeNode queueHead;
			while ((queueHead = queue.poll()) != null) {
				// Query all children.
				stmtSubCat.setString(1, queueHead.getPageTitle());
				ResultSet rs = stmtSubCat.executeQuery();
				while (rs.next()) {
					pathFound++;
					int pageID = rs.getInt("cl_from");
					String pageTitle = new String(rs.getBytes("page_title"), "utf-8");
					
					// Check blacklist first.
					if (blacklist.contains(pageTitle)) {
						System.err.println(pageTitle + " ignored by blacklist.");
						pathCut++;
						continue;
					}
					
					// Check repetition.
					if (result.containsKey(pageID)) {
						pathCut++;
						System.err.println(pageTitle + " repeated.");
						BuildCategoryTreeNode existNode = result.get(pageID);
						
						if (mode == PruneMode.BY_PAGE_ID) {
							continue;
						} else if (mode == PruneMode.BY_SIM) {
							if (isShouldPrune(queueHead, existNode))
								continue;
						} else {
							throw new RuntimeException("Invalid pruning mode.");
						}
						
						// Overwrite parent information.
						System.err.printf("\t%s wins the parent %s.%n",
								queueHead.getPageTitle(), existNode.getParentPageTitle());
						existNode.setParentPageID(queueHead.getPageID());
						existNode.setParentPageTitle(queueHead.getPageTitle());
						existNode.setLevel(queueHead.getLevel() + 1);
						
						// Update level counter affected by this node.
						System.err.print("\tUpdating dependence... ");
						for (BuildCategoryTreeNode bctn: result.values())
							if (bctn.getParentPageID() == existNode.getPageID())
								bctn.setLevel(bctn.getLevel() + 1);
						System.err.println("Done.");
					} else {
						// A newly discovered node.
						BuildCategoryTreeNode n = new BuildCategoryTreeNode(pageID, pageTitle);
						n.setLevel(queueHead.getLevel() + 1);
						n.setParentPageID(queueHead.getPageID());
						n.setParentPageTitle(queueHead.getPageTitle());
						n.setArticleCount(fetchArticleCount(n.getPageTitle()));
						/*if (catAuthorTable != null && !"".equals(catAuthorTable))
							n.setAltitude(fetchAuthorCount(n.getPageID()));
						else
							n.setAltitude(n.getArticleCount());*/
						queue.add(n);
						result.put(n.getPageID(), n);
					}
				}
				rs.close();
			}
			
			System.err.println("Writing to disk...");
			outputResult(result);
			
		} finally {
			stmtSubCat.close();
			stmtCount.close();
			stmtCo.close();
			conn.close();
			out.close();
		}
		
		System.err.printf("%,d items processed for the category tree.%n", result.size());
		System.err.printf("%,d paths found; %,d dropped.%n", pathFound, pathCut);
	}
	
	private boolean isShouldPrune(BuildCategoryTreeNode newParent,
			BuildCategoryTreeNode node) throws SQLException {
		node = result.get(node.getPageID());
		int nodeCount = node.getArticleCount();
		int oldParentCount = result.containsKey(node.getParentPageID()) ?
				result.get(node.getParentPageID()).getArticleCount() :
				fetchArticleCount(node.getParentPageTitle());
		int newParentCount = newParent.getArticleCount();
		int oldCoCount = fetchArticleCoCount(node.getParentPageTitle(), node.getPageTitle());
		int newCoCount = fetchArticleCoCount(newParent.getPageTitle(), node.getPageTitle());
		double oldSim = cosSim(oldParentCount, nodeCount, oldCoCount);
		double newSim = cosSim(newParentCount, nodeCount, newCoCount);
		return oldSim >= newSim;
	}

	private void outputResult(Map<Integer, BuildCategoryTreeNode> result) {
		for (BuildCategoryTreeNode n: result.values())
			n.write(out);
	}

	public void initBlacklist(String filename) throws FileNotFoundException, IOException {
		InputStreamReader isr = new InputStreamReader(new FileInputStream(filename), "utf-8");
		BufferedReader br = new BufferedReader(isr);
		try {
			String s;
			while ((s = br.readLine()) != null) {
				if ("".equals(s))
					continue;
				blacklist.add(s);
			}
		} finally {
			br.close();
		}
	}
	
	private int fetchArticleCount(String category) throws SQLException {
		ResultSet rs = null;
		try {
			stmtCount.setString(1, category);
			rs = stmtCount.executeQuery();
			rs.next();
			return rs.getInt("c");
		} finally {
			if (rs != null)
				rs.close();
		}
	}
	
	private float fetchAuthorCount(int catPageID) throws SQLException {
		ResultSet rs = null;
		try {
			stmtAuthor.setInt(1, catPageID);
			rs = stmtAuthor.executeQuery();
			if (rs.next())
				return rs.getFloat("avg_author");
			else
				return 0;
		} finally {
			if (rs != null)
				rs.close();
		}
	}
	
	private int fetchArticleCoCount(String c1, String c2) throws SQLException {
		ResultSet rs = null;
		try {
			stmtCo.setString(1, c1);
			stmtCo.setString(2, c2);
			rs = stmtCo.executeQuery();
			rs.next();
			return rs.getInt("c");
		} finally {
			if (rs != null)
				rs.close();
		}
	}
	
	private double cosSim(int ci, int cj, int coCount) {
		if (ci == 0 || cj == 0)
			return 0.0;
		else
			return (double)coCount / Math.sqrt((double)ci * cj);
	}
}
