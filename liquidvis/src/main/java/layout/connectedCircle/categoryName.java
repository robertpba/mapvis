package layout.connectedCircle;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;

import preProcess.jdbcmysql;

public class categoryName{
  static final int numPoints = 1143;
  
  public static void main (String[] args) {
    String line = null;
    String[] strArray;
    int i,pageId;
    jdbcmysql ac = new jdbcmysql();
    ResultSet rs=null;
    
    try{
      FileWriter outFile = new FileWriter("categoryName.txt");
      PrintWriter out = new PrintWriter(outFile);
      BufferedReader reader = new BufferedReader (new FileReader("finalPointInfo.txt"));
      line=reader.readLine();
      
      for (i=0;i<numPoints;i++){
        line=reader.readLine();
        strArray = line.split(" ");
        pageId=Integer.parseInt(strArray[0]);//page_id
        rs=ac.selectStatement("SELECT page_title FROM page WHERE page_id ="+ pageId);
        
        rs.next();
        out.println( rs.getString("page_title"));
        
      }
      reader.close();
      out.close();
	  ac.close();
      
    } catch(Exception ex) { 
      ex.printStackTrace(); 
    }
    
  }
  
}