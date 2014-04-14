package mapvis.utility

import java.sql.{ResultSet, Connection, PreparedStatement, DriverManager}

class SqlContext  {
  private var conn    : Connection = null

  def SqlContext (db       : String = "test",
                  server   : String = "jdbc:mysql://localhost:3306/",
                  username : String = "root",
                  password : String = "root" ) {
    val url = server + db
    conn = DriverManager.getConnection(url, username, password);
  }

  def query(sql : String, param : String* ) = {
    val stmt: PreparedStatement = conn.prepareStatement(sql)
    var i: Int = 0
    param foreach {i = i + 1; stmt.setObject(i, _) }
    val rs = stmt.executeQuery
    new Iterator[ResultSet] {
      override def hasNext = !rs.isLast
      override def next    = { rs.next ; rs }
    }
  }

}
