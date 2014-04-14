package preProcess;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class jdbcmysql {
  private Connection con = null; //Database objects
  //connect object
  private Statement stat = null;
  //execute, complete string to be exe
  private ResultSet rs = null;
  
  private PreparedStatement pst = null;
  
  private String createdbSQL = "CREATE TABLE Usertest (" +
    "    id     INTEGER " +
    "  , name    VARCHAR(20) " +
    "  , passwd  VARCHAR(20))";
  
  public jdbcmysql()
  {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      //regester driver
      con = DriverManager.getConnection(
                                        "jdbc:mysql://localhost/simplewiki?useUnicode=true&characterEncoding=Big5",
                                        "root","john");
      //get connection
      //jdbc:mysql://localhost/plainmapper.metadata.test?useUnicode=true&characterEncoding=Big5
      //computer place ,plainmapper.metadata.test name of database
      //useUnicode=true&characterEncoding=Big5 coding in use
      
    }
    catch(ClassNotFoundException e)
    {
      System.out.println("DriverClassNotFound :"+e.toString());
    }
    catch(SQLException x) {
      System.out.println("Exception :"+x.toString());
    }
    
  }
  
  public ResultSet selectStatement(String sql){
    try { 
     stat = con.createStatement(); 
     rs = stat.executeQuery(sql); 
    } 
    catch(SQLException e) {
      System.out.println("selectDB Exception :" + e.toString()); 
    }
    finally{
      return rs;
    }
  }
  
  //close connect...
  public void close()
  {
    try
    {
      if(rs!=null)
      {
        rs.close();
        this.rs = null;
      }
      if(stat!=null)
      {
        stat.close();
        stat = null;
      }
      if(pst!=null)
      {
        pst.close();
        pst = null;
      }
    }
    catch(SQLException e)
    {
      System.out.println("Close Exception :" + e.toString());
    }
  }
}