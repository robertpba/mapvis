package preProcess;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.*;

public class countArtical{
  
  static jdbcmysql ac = new jdbcmysql();
  static ResultSet rs=null;
  static int map[]=new int [353727];
  
  private static void writeToFile(){
    int i;
    
    try {
      FileWriter outFile = new FileWriter("numberOfArticle.txt");
      PrintWriter out = new PrintWriter(outFile);
      
      for (i=0; i<353727; i++)
        if (map[i]!=-1)
          out.println(i+" "+map[i]);
      out.close();
    } catch (IOException e){
      e.printStackTrace();
    }
  }
  
  private static int getNumberOfArticle(int category){
    rs=ac.selectStatement("SELECT count(*) as c FROM articletree, page WHERE articletree.page_id ="+ category+" AND cl_from = page.page_id AND page_namespace = 0");
    try {
      rs.next();
      return rs.getInt("c");
    } catch(SQLException e) { 
      System.out.println("DropDB Exception :" + e.toString()); 
    } 
    return 0;
  }
  
  public static void main(String[] args)
  {  
    String line = null;
    String[] strArray;
    int i,articals,parent,child;
    
    for(i=0; i<353727; i++)
      map[i]=-1;
        
    try{
      BufferedReader reader = new BufferedReader(new FileReader("multipleParentCategoryTree.txt"));
      while((line = reader.readLine()) != null) 
      {
        strArray = line.split(" ");
        parent=Integer.parseInt(strArray[0]);
        if (map[parent]==-1){
          articals=getNumberOfArticle(parent);
          map[parent]=articals;
          System.out.println(articals);
        }
        for (i=1; i<strArray.length; i++){
          child=Integer.parseInt(strArray[i]);
          if (map[child]==-1){
            articals=getNumberOfArticle(child);
            map[child]=articals;
            System.out.println(articals);
          }

        }
      }
      reader.close();
    } catch(Exception ex) { 
      ex.printStackTrace(); 
    } finally {
      ac.close();
    }
    writeToFile();
  }
  
  
}