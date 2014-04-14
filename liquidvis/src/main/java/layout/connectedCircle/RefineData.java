package layout.connectedCircle;
import java.io.*;
import java.util.*;

public class RefineData{
          
          static final int numberOfPoints = 1098;
          
          static ArrayList[] map=new ArrayList [numberOfPoints*2];
          static double[][] position = new double[numberOfPoints*2][5];
          
          public static void main (String[] args){ 
                    String line = null;
                    String[] strArray;
                    int count=numberOfPoints;
                    
                    int i,j,previousParent=-1;
                    
                    try{
                              BufferedReader reader = new BufferedReader (new FileReader("pointInfo.txt"));
                              for (i=0;i<numberOfPoints;i++){
                                        line=reader.readLine();
                                        strArray = line.split(" ");
                                        position[i][0]=Double.parseDouble(strArray[0]);//page_id
                                        position[i][1]=Double.parseDouble(strArray[1]);//level
                                        position[i][2]=Double.parseDouble(strArray[2]);//figure
                                        position[i][3]=Double.parseDouble(strArray[3]);//x
                                        position[i][4]=Double.parseDouble(strArray[4]);//y
                              }
                              reader.close();
                              
                              reader= new BufferedReader (new FileReader("../preprocess/edgeInfo.txt"));
                              while((line = reader.readLine()) != null) {
                                        strArray = line.split(" ");
                                        if (Integer.parseInt(strArray[0])!=previousParent){
                                                  previousParent=Integer.parseInt(strArray[0]);
                                                  map[previousParent]=new ArrayList<Integer>();
                                        }
                                        map[Integer.parseInt(strArray[0])].add(Integer.parseInt(strArray[1]));
                              }
                              reader.close();
                              
                    } catch(Exception ex) { 
                              ex.printStackTrace(); 
                    }
                    
                    for (i=0; i<numberOfPoints; i++){
                              if (position[i][1]==3 && map[i]==null){
                                        map[i]=new ArrayList<Integer>();
                                        map[i].add(count);
                                        position[count][0]=position[i][0];
                                        position[count][1]=4;
                                        position[count][2]=position[i][2];
                                        position[count][3]=position[i][3];
                                        position[count][4]=position[i][4];
                                        count++;
                              }
                    }
                    
                    
                    try{
                              FileWriter outFile = new FileWriter("refindPointInfo.txt");
                              PrintWriter out = new PrintWriter(outFile);
                              for (i=0; i<count; i++)
                                        out.println((int)position[i][0]+" "+(int)position[i][1]+" "+(int)position[i][2]+" "+ position[i][3]+" "+position[i][4]);
                              out.close();
                              
                              outFile = new FileWriter("edgeInfo.txt");
                              out = new PrintWriter (outFile);
                              for ( i=0; i< count; i++){
                                        if (map[i]!=null)
                                                  for (j=0; j<map[i].size(); j++)
                                                  out.println(i+" "+map[i].get(j));
                              }
                              out.close();
                    } catch(Exception ex) { 
                              ex.printStackTrace(); 
                    }
                    
          }
          
}