package layout.forceDirectedLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.visual.VisualItem;
import prefuse.visual.EdgeItem;
import java.io.*;

public class MyForceDirectedLayout extends ForceDirectedLayout{
          
          boolean startToGetXY=false, start=false,startToWrite=false;
          FileWriter outFile;
          PrintWriter out;
          
          public MyForceDirectedLayout(String graph){
                    super(graph);
          }
          
          public MyForceDirectedLayout(String graph, boolean enforceBounds){
                    super(graph,enforceBounds);
          }
          
          public MyForceDirectedLayout(String graph, boolean enforceBounds, boolean runonce){
                    super(graph, enforceBounds, runonce);
          }
          
          protected float getMassValue(VisualItem n){
                    
                    if (start && n.get("name").equals("5066")){
                              out.close();
                              System.exit(1);
                    }
                    
                    if (startToGetXY && n.get("name").equals("5066")){
                              start=true;
                    }
                    
                    if (start && !startToWrite){
                              try {
                                        outFile = new FileWriter("pointInfo.txt");
                                        out = new PrintWriter(outFile);
                              }catch (IOException e){
                                        e.printStackTrace();
                              }
                              startToWrite=true;
                    }
                    
                    if (startToWrite){
                              out.println(n.get("name")+" "+n.get("level")+" "+(Integer)n.get("size")/2+" "+
                                          ((double)(n.getX())+(Integer)n.get("size")/2)+" "+((double)(n.getY())+(Integer)n.get("size")/2));
                    }
                    //return 1;
                    return (Float) n.get("mass");
          }
          
          protected float getSpringLength(EdgeItem e){
                    //return (float) 0.01;
                    return -1;
          }
          
          protected float getSpringCoefficient(EdgeItem e){
                    //return (Float)e.get("spring");
                    //return (float) 0.00008;
                    return -1;
          }
          
          protected void getXY(){
                    startToGetXY=true;
          }
          
          
          
}