package mapwiki.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Database {
	private String driverName = "com.mysql.jdbc.Driver";
	private String connString;
	private String username;
	private String password;
	
	public Database(String driverName, String connString,
			String username, String password) {
		this.driverName = driverName;
		this.connString = connString;
		this.username = username;
		this.password = password;
	}

	public Database(String connString, String username, String password) {
		this.connString = connString;
		this.username = username;
		this.password = password;
	}
	
	public Database(OptionSet opts) {
		if (opts.has("driver"))
			this.driverName = (String)opts.valueOf("driver");
		if (opts.has("conn"))
			this.connString = (String)opts.valueOf("conn");
		if (opts.has("u"))
			this.username = (String)opts.valueOf("u");
		if (opts.has("p"))
			this.password = (String)opts.valueOf("p");
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(driverName);
		Connection conn = DriverManager.getConnection(connString, username, password);
		return conn;
	}
	
	public static OptionParser initOptionParser(OptionParser op) {
		op.accepts("driver").withRequiredArg().describedAs("jdbc_driver");
		op.accepts("conn").withRequiredArg().describedAs("jdbc_connect_string");
		op.accepts("u").withRequiredArg().describedAs("db_username");
		op.accepts("p").withRequiredArg().describedAs("db_password");
		return op;
	}
}
