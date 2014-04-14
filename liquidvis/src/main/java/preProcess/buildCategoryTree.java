package preProcess;
import java.util.*;
import java.io.*;

public class buildCategoryTree{
  static ArrayList[] map=new ArrayList [353727];
  static Boolean[] mark=new Boolean[353727];
  static Boolean[] done=new Boolean[353727];
  
  public static void initialize(){
    for (int i=0;i<353727;i++){
      mark[i]=false;
      done[i]=false;
    }
  }
  
  public static void cycleDetect(int parent){
    if (map[parent]==null){
      done[parent]=true;
    }
    else{
      for(int i= map[parent].size()-1;i>=0;i--){
        if (mark[(Integer)map[parent].get(i)] && !done[(Integer)map[parent].get(i)])
          map[parent].remove(i);
        else{
          mark[(Integer)map[parent].get(i)]=true;
          cycleDetect((Integer)map[parent].get(i));
          done[(Integer)map[parent].get(i)]=true;
        }
      }
    }
  }
  
  public static void writeToFile()
  {
    int i,j;
    
    try {
      FileWriter outFile = new FileWriter("multipleParentCategoryTree.txt");
      PrintWriter out = new PrintWriter(outFile);
      
      for (i=0; i<353727; i++)
        if (done[i] && map[i]!=null && map[i].size()>0){
          out.print(i+" ");
          for (j=0;j<map[i].size();j++){
            out.print(map[i].get(j)+" ");
          }
          out.println();
        }
      out.close();
    } catch (IOException e){
      e.printStackTrace();
    }

  }
  
  public static void cutToThreeLever(){
    int i,j,k ;
    
    for (i=0;i<353727;i++)
    { mark[i]=false; done[i]=false; }
    
    map[137597].remove(map[137597].indexOf(295515));
    map[137597].remove(map[137597].indexOf(295516));
    
    done[137597]=true;
    mark[137597]=true;
    for (i=0; i<map[137597].size(); i++){
      done[(Integer)map[137597].get(i)]=true;
      mark[(Integer)map[137597].get(i)]=true;
      
      if (map[ (Integer)map[137597].get(i) ]!=null && map[(Integer) map[137597].get(i) ].size()>0 ){
        for (j=0; j< map[(Integer) map[137597].get(i) ].size(); j++){
          done[ (Integer) map[ (Integer) map[137597].get(i)].get(j)]=true;
          mark[ (Integer) map[ (Integer) map[137597].get(i)].get(j)]=true;
          
          if (map[(Integer) map[ (Integer) map[137597].get(i)].get(j)]!=null && map[(Integer) map[ (Integer) map[137597].get(i)].get(j)].size()>0)
            for (k=0; k< map[(Integer) map[ (Integer) map[137597].get(i)].get(j)].size(); k++)
             mark[(Integer)map[(Integer) map[ (Integer) map[137597].get(i)].get(j)].get(k)]=true;
        }
      }
    }
  }
  
  public static void main(String[] args)
  { 
    String line = null;
    String[] strArray;
    
    initialize();
    
    // make the array list 
    int previousParent=-1;
    try{
      BufferedReader reader = new BufferedReader(new FileReader("rawdata.txt"));
      while((line = reader.readLine()) != null) {
        strArray = line.split("\t");
        if (Integer.parseInt(strArray[0])!=previousParent){
          previousParent=Integer.parseInt(strArray[0]);
          map[previousParent]=new ArrayList<Integer>();
        }
        map[Integer.parseInt(strArray[0])].add(Integer.parseInt(strArray[1]));
      }
      reader.close();
    }
    catch(Exception ex) { 
      System.out.println("Error reading file "); 
      ex.printStackTrace(); 
    }
    
    for(int i=1;i<=353705;i++){
      if (map[i]!=null){
        mark[i]=true;
        cycleDetect(i);
        done[i]=true;
      }
    }
    
    cutToThreeLever();
    writeToFile();
    
  }
}