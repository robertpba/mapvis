package layout.connectedCircle;
import java.io.*;

public class makePointPositive 
{
          public static void main (String[] args) 
          { 
                    String line = null;
                    String[] strArray;
                    double[][] position = new double[1098][5];
                    int i;
                    double minX=0,minY=0,x,y;
                    
                    try{
                              BufferedReader reader = new BufferedReader (new FileReader("../ForceDirectedLayout/pointInfo.txt"));
                              for (i=0;i<1098;i++){
                                        line=reader.readLine();
                                        strArray = line.split(" ");
                                        x=Double.parseDouble(strArray[3]);
                                        y=Double.parseDouble(strArray[4]);
                                        position[i][0]=Double.parseDouble(strArray[0]);
                                        position[i][1]=Double.parseDouble(strArray[1]);
                                        position[i][2]=Double.parseDouble(strArray[2]);
                                        position[i][3]=x;
                                        position[i][4]=y;
                                        if (minX>x) minX=x;
                                        if (minY>y) minY=y;
                              }
                              reader.close();
                              
                              FileWriter outFile = new FileWriter("pointInfo.txt");
                              PrintWriter out = new PrintWriter(outFile);
                              minX = minX*-1 ;
                              minY = minY*-1 ;
                              for (i=0; i<1098; i++)
                                        out.println((int)position[i][0]+" "+(int)position[i][1]+" "+(int)position[i][2]+" "+(position[i][3]+minX)+" "+(position[i][4]+minY));
                              out.close();
                    } catch(Exception ex) { 
                              ex.printStackTrace(); 
                    }
                    
          }
          
}