package preProcess;
import java.util.*;
import java.io.*;
import org.apache.commons.collections.map.MultiKeyMap;
import java.sql.ResultSet;
import java.sql.SQLException;

public class calSimilarity{
  
  static ArrayList[] map=new ArrayList [353727];
  static MultiKeyMap similarityMap = new MultiKeyMap();

  static ResultSet rs1=null;
  static ResultSet rs2=null;
  static ResultSet rs3=null;
  
  static jdbcmysql ci = new jdbcmysql();
  static jdbcmysql cj = new jdbcmysql();
  static jdbcmysql coCount = new jdbcmysql();
  
  private static void writeToFile(){
    int i,j;
    
    try {
      FileWriter outFile = new FileWriter("threeLevelSimilarity.txt");
      PrintWriter out = new PrintWriter(outFile);
      
      for (i=0; i<353727; i++)
        if (map[i]!=null)
          for (j=0;j<map[i].size();j++){
            out.println(i+" "+map[i].get(j)+" "+similarityMap.get(i,map[i].get(j)));
          }
        
      out.close();
    } catch (IOException e){
      e.printStackTrace();
    }
  }
  
  private static double getSimilarity(int parent, int child){
    rs1=ci.selectStatement("SELECT count(*) as c FROM articletree, page WHERE articletree.page_id ="+ parent+" AND cl_from = page.page_id AND page_namespace = 0");
    rs2=cj.selectStatement("SELECT count(*) as c FROM articletree, page WHERE articletree.page_id ="+ child+" AND cl_from = page.page_id AND page_namespace = 0");
    rs3=coCount.selectStatement("SELECT COUNT(*) AS c FROM page p, articletree c1, articletree c2 WHERE c1.page_id = "+parent+" AND c2.page_id = "+child+" AND c1.cl_from = c2.cl_from AND p.page_id = c1.cl_from AND p.page_namespace = 0");
    try {
      rs1.next();
      if(rs1.getInt("c")==0) return 0;
      rs2.next();
      if(rs2.getInt("c")==0) return 0;
      rs3.next();
      return (double)rs3.getInt("c")/Math.sqrt((double)rs1.getInt("c") * rs2.getInt("c"));
    } catch(SQLException e) { 
      System.out.println("DropDB Exception :" + e.toString()); 
    } 
    return 0;
  }
  
  public static void main(String[] args)
  { 
    String line = null;
    String[] strArray;
    int parent,child,i;
    double cosSim;
        
    try{
      BufferedReader reader = new BufferedReader(new FileReader("multipleParentCategoryTree.txt"));
      while((line = reader.readLine()) != null) 
      {
        strArray = line.split(" ");
        parent=Integer.parseInt(strArray[0]);
        map[parent]=new ArrayList<Integer>();
        for (i=1; i<strArray.length; i++){
          child=Integer.parseInt(strArray[i]);
          map[parent].add(child);
          cosSim = getSimilarity(parent,child);
          similarityMap.put(parent,child,cosSim);
          System.out.println(cosSim);
        }
      }
      reader.close();
    } catch(Exception ex) { 
      System.out.println("Error reading file "); 
      ex.printStackTrace(); 
    } finally {
      ci.close();
      cj.close();
      coCount.close();
    }
    writeToFile();
   // System.out.println(similarityMap.size());
  }
  
  
  
  
}