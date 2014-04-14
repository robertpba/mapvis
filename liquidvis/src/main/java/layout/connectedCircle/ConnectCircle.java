package layout.connectedCircle;
import java.io.*;
import java.util.*;

public class ConnectCircle {
          
          static final int numberOfPoints = 1143;
          
          static ArrayList[] map=new ArrayList [numberOfPoints];
          static double[][] position = new double[numberOfPoints][5];
          static double[][] rectangular, rectangular2;
          static double maxX=0, maxY=0;
          
          private static void writeToFile(){
                    try{
                              FileWriter outFile = new FileWriter("finalPointInfo.txt");
                              PrintWriter out = new PrintWriter(outFile);
                              out.println(maxX+" "+maxY);
                              for (int i=0; i<numberOfPoints; i++)
                                        out.println((int)position[i][0]+" "+(int)position[i][1]+" "+(int)position[i][2]+" "+ position[i][3]+" "+position[i][4]);
                              out.close();
                    } catch(Exception ex) { 
                              ex.printStackTrace(); 
                    }
          }
          
          private static void relocatePoints(){
                    int i ;
                    double minX=1000000,minY=1000000;
                    int margin=400;
                    
                    for(i=0;i<numberOfPoints;i++){
                              if (position[i][3]-position[i][2]< minX) minX=position[i][3]-position[i][2];
                              if (position[i][4]-position[i][2]< minY) minY=position[i][4]-position[i][2];
                              if (position[i][3]+position[i][2]> maxX) maxX=position[i][3]+position[i][2];
                              if (position[i][4]+position[i][2]> maxY) maxY=position[i][4]+position[i][2];
                    }
                    
                    minX -= margin; minY -= margin;
                    maxX -= minX; maxY -=minY;
                    maxX +=margin; maxY +=margin;
                    for (i=0;i<numberOfPoints;i++){
                              position[i][3] -=minX;
                              position[i][4] -=minY;
                    }
                    
          }
          
          private static void iniRectangular(int root){
                    int i,j,k, numChildren=map[root].size(),parentLevel2,parentLevel3;
                    
                    for (i =0; i<numChildren; i++) {
                              rectangular[i][0]=1000000;
                              rectangular[i][1]=1000000;
                              rectangular[i][2]=0;
                              rectangular[i][3]=0;
                    }
                    
                    for (i=0; i<numChildren; i++){
                              parentLevel2=(Integer)map[root].get(i);
                              for (j=0; j<map[parentLevel2].size(); j++){
                                        parentLevel3=(Integer)map[parentLevel2].get(j);
                                        if (map[parentLevel3] != null)
                                                  for(k=0; k<map[parentLevel3].size(); k++){
                                                  if (position[(Integer)map[parentLevel3].get(k)][3]-position[(Integer)map[parentLevel3].get(k)][2]<rectangular[i][0])
                                                            rectangular[i][0]=position[(Integer)map[parentLevel3].get(k)][3]-position[(Integer)map[parentLevel3].get(k)][2];
                                                  if (position[(Integer)map[parentLevel3].get(k)][4]-position[(Integer)map[parentLevel3].get(k)][2]<rectangular[i][1])
                                                            rectangular[i][1]=position[(Integer)map[parentLevel3].get(k)][4]-position[(Integer)map[parentLevel3].get(k)][2];
                                                  if (position[(Integer)map[parentLevel3].get(k)][3]+position[(Integer)map[parentLevel3].get(k)][2]>rectangular[i][2])
                                                            rectangular[i][2]=position[(Integer)map[parentLevel3].get(k)][3]+position[(Integer)map[parentLevel3].get(k)][2];
                                                  if (position[(Integer)map[parentLevel3].get(k)][4]+position[(Integer)map[parentLevel3].get(k)][2]>rectangular[i][3])
                                                            rectangular[i][3]=position[(Integer)map[parentLevel3].get(k)][4]+position[(Integer)map[parentLevel3].get(k)][2];
                                        }
                              }
                    }
                    
                    
                    
                    for (i =0; i<numChildren; i++) {
                              rectangular[i][0] -=100;
                              rectangular[i][1] -=100;
                              rectangular[i][2] +=100;
                              rectangular[i][3] +=100;
                              rectangular2[i][0]=rectangular[i][0];
                              rectangular2[i][1]=rectangular[i][1];
                    }
          }
          
          private static void moveChildrenOfRoot(int root){
                    int i,j,k, numChildren=map[root].size(),parentLevel2,parentLevel3;
                    double x,y;
                    for (i=0; i<numChildren; i++){
                              x=rectangular[i][0]-rectangular2[i][0];y=rectangular[i][1]-rectangular2[i][1];
                              parentLevel2=(Integer)map[root].get(i);
                              for (j=0; j<map[parentLevel2].size(); j++){
                                        parentLevel3=(Integer)map[parentLevel2].get(j);
                                        position[parentLevel3][3] += x;
                                        position[parentLevel3][4] += y;
                                        if (map[parentLevel3] != null)
                                                  for(k=0; k<map[parentLevel3].size(); k++){
                                                  position[(Integer)map[parentLevel3].get(k)][3] += x;
                                                  position[(Integer)map[parentLevel3].get(k)][4] += y;
                                        }
                              }
                    }
          }
          
          private static double distance (int circle1, int circle2){
                    return Math.sqrt(Math.pow((position[circle1][3]-position[circle2][3]),2)+Math.pow((position[circle1][4]-position[circle2][4]),2));
          }
          
          private static double distance (int circle1, int circle2, double x, double y){
                    return Math.sqrt(Math.pow((position[circle1][3]-position[circle2][3]+x),2)+Math.pow((position[circle1][4]-position[circle2][4]+y),2));
          }
          
          private static boolean overlap(int parent, int indexOfChild){
                    for (int i =0; i< map[parent].size(); i++)
                              if (i!=indexOfChild && 
                                  distance((Integer)map[parent].get(indexOfChild), (Integer)map[parent].get(i))
                                            <(position[(Integer)map[parent].get(indexOfChild)][2]+position[(Integer)map[parent].get(i)][2]))
                              return true;
                    return false;
          }
          
          private static boolean overlap(int parent, int indexOfChild, double x, double y){
                    int p1=(Integer)map[parent].get(indexOfChild);
                    int p2,i,j,k;
                    
                    for (i =0; i<map[parent].size(); i++){
                              if ( i != indexOfChild){
                                        p2=(Integer)map[parent].get(i);
                                        
                                        if (map[p1]!=null && map[p2]!=null){
                                                  for (j=0; j<map[p1].size(); j++)
                                                            for (k=0; k<map[p2].size(); k++)
                                                            if( distance((Integer)map[p1].get(j),(Integer)map[p2].get(k),x,y) 
                                                                         < ( position[(Integer)map[p1].get(j)][2] + position[(Integer)map[p2].get(k)][2] ))
                                                            return true;
                                        }
                              }
                    }
                    return false;
          }
          
          private static boolean overlap(int parent, int indexOfChild, double x, double y, String r){
                    for (int i=0; i< map[parent].size(); i++){
                              if (i != indexOfChild){
                                        if (!(rectangular[indexOfChild][2]<rectangular[i][0] || rectangular[indexOfChild][0]>rectangular[i][2] || 
                                              rectangular[indexOfChild][3]<rectangular[i][1] || rectangular[indexOfChild][1]>rectangular[i][3]))
                                                  return true;
                              }
                    }
                    return false;
          }
          
          private static void normalize(double[] vector){
                    double length= Math.sqrt(vector[0]*vector[0]+vector[1]*vector[1]);
                    vector[0] /= length;
                    vector[1] /= length;
          }
          
          private static void shrinkChildrenOf (int parent){
                    int i,j ;
                    double vector[]=new double[2];
                    double original[]=new double[2];
                    boolean moving=true;
                    
                    if (position[parent][1]==3){
                              while (moving){
                                        moving=false;
                                        
                                        for (i=0;i<map[parent].size();i++){
                                                  vector[0]=position[parent][3]-position[(Integer)map[parent].get(i)][3];
                                                  vector[1]=position[parent][4]-position[(Integer)map[parent].get(i)][4];
                                                  
                                                  if (Math.abs(vector[0])>1 || Math.abs(vector[1])>1){
                                                            normalize(vector);
                                                            
                                                            original[0]=position[(Integer)map[parent].get(i)][3];
                                                            original[1]=position[(Integer)map[parent].get(i)][4];
                                                            
                                                            position[(Integer)map[parent].get(i)][3] +=vector[0];
                                                            position[(Integer)map[parent].get(i)][4] +=vector[1];
                                                            
                                                            if (overlap(parent,i)){
                                                                      position[(Integer)map[parent].get(i)][3]=original[0];
                                                                      position[(Integer)map[parent].get(i)][4]=original[1];
                                                            }
                                                            else
                                                                      moving=true;
                                                  }
                                        }
                              }
                    }
                    else if (position[parent][1]==2){
                              while(moving){
                                        moving=false;
                                        
                                        for (i=0;i<map[parent].size();i++){
                                                  vector[0]=position[parent][3]-position[(Integer)map[parent].get(i)][3];
                                                  vector[1]=position[parent][4]-position[(Integer)map[parent].get(i)][4];
                                                  
                                                  if (Math.abs(vector[0])>1 || Math.abs(vector[1])>1){
                                                            normalize(vector);
                                                            
                                                            if (!overlap(parent,i,vector[0],vector[1])){
                                                                      for (j=0;  j< map[(Integer)map[parent].get(i)].size(); j++){
                                                                                position[(Integer)map[(Integer)map[parent].get(i)].get(j)][3] +=vector[0];
                                                                                position[(Integer)map[(Integer)map[parent].get(i)].get(j)][4] +=vector[1];
                                                                      }
                                                                      position[(Integer)map[parent].get(i)][3]+=vector[0];
                                                                      position[(Integer)map[parent].get(i)][4]+=vector[1];
                                                                      moving=true;
                                                            }
                                                  }
                                        }
                              }
                    }
                    else{
                              iniRectangular(parent);
                              while (moving){
                                        moving=false;
                                        
                                        for (i=0;i<map[parent].size();i++){
                                                  vector[0]=position[parent][3]-position[(Integer)map[parent].get(i)][3];
                                                  vector[1]=position[parent][4]-position[(Integer)map[parent].get(i)][4];
                                                  
                                                  if (Math.abs(vector[0])>1 || Math.abs(vector[1])>1){
                                                            normalize(vector);
                                                            
                                                            if (!overlap(parent,i,vector[0],vector[1],"R") ){
                                                                      rectangular[i][0] += vector[0];
                                                                      rectangular[i][1] += vector[1];
                                                                      rectangular[i][2] += vector[0];
                                                                      rectangular[i][3] += vector[1];
                                                                      
                                                                      position[(Integer)map[parent].get(i)][3]+=vector[0];
                                                                      position[(Integer)map[parent].get(i)][4]+=vector[1];
                                                                      moving=true;
                                                            }
                                                  }
                                        }
                              }
                    }
          }
          
          public static void main (String[] args){ 
                    String line = null;
                    String[] strArray;
                    
                    int i,previousParent=-1;
                    
                    try{
                              BufferedReader reader = new BufferedReader (new FileReader("refindPointInfo.txt"));
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
                              
                              reader= new BufferedReader (new FileReader("edgeInfo.txt"));
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
                    
                    for (i=0;i<numberOfPoints;i++){
                              if (position[i][1]==3){
                                        shrinkChildrenOf(i);
                                        System.out.println("finish shrinking children(level 4) of node "+i);
                              }
                    }
                    
                    for (i=0;i<numberOfPoints;i++){
                              if (position[i][1]==2){
                                        shrinkChildrenOf(i);
                                        System.out.println("finish shrinking children(level 3) of node "+i);
                              }
                    }
                    
                    for (i=0; i<numberOfPoints;i++){
                              if (position[i][1]==1){
                                        rectangular=new double[map[i].size()][4];//top x, top y, buttom x, buttom y
                                        rectangular2=new double[map[i].size()][2];//top x, top y
                                        shrinkChildrenOf(i);
                                        moveChildrenOfRoot(i);
                                        System.out.println("finish shrinking children(level 2) of node "+i);
                                        break;
                              }
                    }
                    
                    relocatePoints();
                    
                    writeToFile();
                    
          }
          
}