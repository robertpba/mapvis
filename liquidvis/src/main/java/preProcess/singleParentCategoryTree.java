package preProcess;
import java.util.*;
import java.io.*;
import org.apache.commons.collections.map.MultiKeyMap;


public class singleParentCategoryTree{

  static ArrayList[] map=new ArrayList [353727];
  static ArrayList[] threeLevelMap = new ArrayList [353727];
  static MultiKeyMap similarityMap = new MultiKeyMap();
  static int parents[]=new int[353727];
  static Boolean[] mark=new Boolean[353727];
  static Boolean[] done=new Boolean[353727];
  static int[] level = new int[353727]; 
  static int[] numArtical = new int[353727];
  
  private static void cutToThreeLevel(){
    int i,j,k;
    for(i=0; i<353727; i++){
      if(parents[i]!=0){
        if (threeLevelMap[parents[i]]==null){
          threeLevelMap[parents[i]]=new ArrayList<Integer>();
          threeLevelMap[parents[i]].add(i);
        }
        else
          threeLevelMap[parents[i]].add(i);
      }
    }
    
    for (i=0;i<353727;i++)
    { mark[i]=false; done[i]=false; level[i]=0;}
    
    done[137597]=true;
    mark[137597]=true;
    level[137597]=1;
    for (i=0; i<threeLevelMap[137597].size(); i++){
      done[(Integer)threeLevelMap[137597].get(i)]=true;
      mark[(Integer)threeLevelMap[137597].get(i)]=true;
      if( level[(Integer)threeLevelMap[137597].get(i)]==0)
        level[(Integer)threeLevelMap[137597].get(i)]=2;
      
      if (threeLevelMap[ (Integer)threeLevelMap[137597].get(i) ]!=null && threeLevelMap[(Integer) threeLevelMap[137597].get(i) ].size()>0 ){
        for (j=0; j< threeLevelMap[(Integer) threeLevelMap[137597].get(i) ].size(); j++){
          done[ (Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)]=true;
          mark[ (Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)]=true;
          if (level[ (Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)]==0)
            level[ (Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)]=3;
            
          
          if (threeLevelMap[(Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)]!=null && threeLevelMap[(Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)].size()>0)
            for (k=0; k< threeLevelMap[(Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)].size(); k++){
             mark[(Integer)threeLevelMap[(Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)].get(k)]=true;
             if (level[(Integer)threeLevelMap[(Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)].get(k)]==0)
               level[(Integer)threeLevelMap[(Integer) threeLevelMap[ (Integer) threeLevelMap[137597].get(i)].get(j)].get(k)]=4;
          }
        }
      }
    }
    
    
  }
  
  private static void writeToFile(){
    int i,j,count=0,match[]=new int [353727];
    try {
      FileWriter outFile = new FileWriter("singleParentCategoryTree.xml");
      FileWriter edgeFile = new FileWriter("edgeInfo.txt");
      PrintWriter out = new PrintWriter(outFile);
      PrintWriter edgeOut = new PrintWriter(edgeFile);
      
      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      out.println("<graphml xmlns=\"http:\\\\graphml.graphdrawing.org\\\\xmlns\">");
      out.println("<graph edgedefault=\"undirected\">");
      out.println("<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>");
      out.println("<key id=\"level\" for=\"node\" attr.name=\"level\" attr.type=\"string\"/>");
      out.println("<key id=\"size\" for=\"node\" attr.name=\"size\" attr.type=\"integer\"/>");
      out.println("<key id=\"mass\" for=\"node\" attr.name=\"mass\" attr.type=\"float\"/>");
      out.println("<key id=\"spring\" for=\"edge\" attr.name=\"spring\" attr.type=\"float\"/>");
      
      for(i=0;i<353727;i++){
        if (mark[i]){
          out.println("<node id=\""+count+"\">");
          out.println("<data key=\"name\">"+i+"</data>");
          out.println("<data key=\"level\">"+level[i]+"</data>");
          out.println("<data key=\"size\">"+numArtical[i]+"</data>");
          out.println("<data key=\"mass\">"+numArtical[i]+"</data>");
          out.println("</node>");
          match[i]=count;
          count++;
        }
      }
      
      for(i=0;i<353727;i++){
        if (done[i] && threeLevelMap[i]!=null && threeLevelMap[i].size()>0){
          for (j=0;j<threeLevelMap[i].size();j++){
              out.println("<edge source=\""+match[i]+"\" target=\""+match[(Integer)threeLevelMap[i].get(j)]+"\"><data key=\"spring\">0.00005</data></edge>");
              edgeOut.println(match[i]+" "+match[(Integer)threeLevelMap[i].get(j)] );
          }
        }
      }
      
      out.println("</graph></graphml>");
      out.close();
      edgeOut.close();
    } catch (IOException e){
      e.printStackTrace();
    }
  }
  
  private static void eliminateParent(){
    int i,j;
    for (i =0; i < 353727; i++) { parents[i]=0;}
  
    for (i=0; i<353727; i++){
      if (map[i]!=null){
        for (j=0; j<map[i].size(); j++){
          if (parents[(Integer)map[i].get(j)]==0)
            parents[(Integer)map[i].get(j)]=i;
          else{
            if ((Double)similarityMap.get(i,map[i].get(j)) > (Double)similarityMap.get(parents[(Integer)map[i].get(j)],map[i].get(j)) )
              parents[(Integer)map[i].get(j)]=i;
          }  
        }
      }
    }
    
  }
  
  private static void scaleNumArtical(){
    for (int i=0; i<353727; i++)
      if (numArtical[i] != -1 )
         numArtical[i]=(int)(Math.log(numArtical[i]+10)*40);
  }
  
  public static void main(String[] args)
  {
    String line = null;
    String[] strArray;
    int parent,child,i;
    double cosSim;
        
    try{
      BufferedReader reader = new BufferedReader (new FileReader("threeLevelSimilarity.txt"));
      while ((line=reader.readLine()) !=null)
      {
        strArray = line.split(" ");
        parent = Integer.parseInt(strArray[0]);
        child = Integer.parseInt(strArray[1]);
        cosSim = Double.parseDouble(strArray[2]);
        similarityMap.put(parent, child, cosSim);
      }
      
      reader = new BufferedReader(new FileReader("multipleParentCategoryTree.txt"));
      while((line = reader.readLine()) != null) 
      {
        strArray = line.split(" ");
        parent=Integer.parseInt(strArray[0]);
        map[parent]=new ArrayList<Integer>();
        for (i=1; i<strArray.length; i++){
          child=Integer.parseInt(strArray[i]);
          map[parent].add(child);
        }
      }
      
      for(i=0;i<353727;i++) numArtical[i]=-1;
      reader = new BufferedReader(new FileReader("numberOfArticle.txt"));
      while((line = reader.readLine()) != null) 
      {
        strArray = line.split(" ");
        parent = Integer.parseInt(strArray[0]);
        child = Integer.parseInt(strArray[1]);
        numArtical[parent]=child;
      }
      //System.out.println(max);
      scaleNumArtical();
      
      reader.close();
    } catch(Exception ex) { 
      System.out.println("Error reading file "); 
      ex.printStackTrace(); 
    }
    
    eliminateParent();
    cutToThreeLevel();
    writeToFile();
  }


}