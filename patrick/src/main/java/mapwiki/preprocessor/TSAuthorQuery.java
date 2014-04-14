package mapwiki.preprocessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public final class TSAuthorQuery {
	// Settings that required for this piece of code to work.
	// Where is the Wikipedia database?
	// Format: jdbc:mysql://hostname:port/db_name
	static final String URL = "jdbc:mysql://umsurvey.umac.mo:3306/test";
	
	// Username and password.
	static final String USERNAME = "cipang";
	static final String PASSWORD = "wiki1111";
	
	// The timestamp value of the dump.
	static final String TIMESTAMP = "20110115080216";
	
	// Table to store the temporary result.
	static final String TABLE_STORAGE = "test.tmp_page_author_count";
	
	// How many pages to be processed in every cycle?
	static final int PAGES_IN_BATCH = 200;
	
	// Below are SQL statements used in the program. Not recommend to change.
	private static final String SQL_GET_ALL_PAGE = "SELECT page_id FROM page " +
		"WHERE page_namespace = 0 ORDER BY page_id ASC";
	private static final String SQL_CALC = "REPLACE /* SLOW_OK */ INTO %s " +
		"SELECT r.rev_page, COUNT( DISTINCT r.rev_user_text ) AS c " +
		"FROM revision r, page p WHERE r.rev_timestamp <= '%s' AND " +
		"r.rev_page = p.page_id AND p.page_namespace = 0 AND " +
		"p.page_id > ? AND p.page_id <= ? " +
		"GROUP BY rev_page;";
	
	// Private variables.
	private static List<Integer> pageList = new LinkedList<Integer>();
	private static Connection conn;
	
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		try {
			readAllPages();
			processBatches();
			System.err.printf("Done at %s.%n", new Date());
		} finally {
			conn.close();
		}
	}

	private static void processBatches() throws SQLException {
		if (pageList.isEmpty())
			throw new IllegalStateException("Nothing to do since no pages found.");
		
		PreparedStatement ps = conn.prepareStatement(String.format(SQL_CALC,
				TABLE_STORAGE, TIMESTAMP));
		try {
			int batchNo = 1;
			int sectionMin = 0;
			for (int sectionMax: pageList) {
				long lastTick = System.currentTimeMillis();
				ps.setInt(1, sectionMin);
				ps.setInt(2, sectionMax);
				ps.executeUpdate();
				System.err.printf("Batch %d/%d saved in %.2fs.%n", batchNo,
						pageList.size(), (System.currentTimeMillis() - lastTick) / 1000.0);
				sectionMin = sectionMax;
				batchNo++;
			}
		} finally {
			ps.close();
		}
	}

	private static void readAllPages() throws SQLException {
		System.err.println("Caching articles...");
		pageList.clear();
		PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_PAGE);
		try {
			int count = 0, pageID = 0;
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				pageID = rs.getInt(1);
				count++;
				if (count >= PAGES_IN_BATCH) {
					count = 0;
					pageList.add(pageID);
				}
			}
			// Last item.
			if (count != 0)
				pageList.add(pageID);
		} finally {
			ps.close();
		}
	}
}
