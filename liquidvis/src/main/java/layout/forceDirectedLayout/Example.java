package layout.forceDirectedLayout;

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

 

public class Example{
  
  
 public static void main(String argv[])
 {
  Graph graph=null;
  try{
   graph=new GraphMLReader().readGraph("singleParentCategoryTree.xml");
  }catch(DataIOException e){
   e.printStackTrace();
   System.err.println("Erro");
   System.exit(1);
  }


  Visualization vis=new Visualization();
  vis.add("graph",graph);

  //LabelRenderer r1=new LabelRenderer("name");
  FinalRenderer r = new FinalRenderer();
  DefaultRendererFactory drf = new DefaultRendererFactory(r);
  //r.setRoundedCorner(8,8);
  drf.add(new InGroupPredicate("nodedec"), new LabelRenderer("name"));
  
  vis.setRendererFactory(drf);

  final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
  DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
  DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR,ColorLib.rgb(0, 0, 0));
  DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Arial",12));
  vis.addDecorators("nodedec", "graph.nodes", DECORATOR_SCHEMA);
  
  
  int[] palette= new int[]{
    ColorLib.rgb(0,139,69),ColorLib.rgb(238,230,133),ColorLib.rgb(209,146,117),ColorLib.rgb(143,188,143)
  };
  DataColorAction fill=new DataColorAction("graph.nodes","level",Constants.NOMINAL,VisualItem.FILLCOLOR,palette);
  
  ColorAction text=new ColorAction("graph.nodes",VisualItem.TEXTCOLOR,ColorLib.gray(0));
  
  ColorAction edges=new ColorAction("graph.edges",VisualItem.STROKECOLOR,ColorLib.gray(200));
  
  ActionList color=new ActionList();
  color.add(fill);
  color.add(text);
  color.add(edges);
 
  ActionList layout=new ActionList(Activity.INFINITY);
  MyForceDirectedLayout mfdl = new MyForceDirectedLayout("graph",false,false);
  layout.add(mfdl);
  layout.add(new FinalDecoratorLayout("nodedec"));
  layout.add(new RepaintAction());
  vis.putAction("color",color);
  vis.putAction("layout",layout);
  
  
  Display display=new Display(vis);
  display.setSize(720,500);
  display.addControlListener(new DragControl());
  display.addControlListener(new PanControl());
  display.addControlListener(new ZoomControl());


  JFrame frame=new JFrame("prefuse example");
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  frame.add(display);
  frame.pack();
  frame.setVisible(true);
  vis.run("color");
  vis.run("layout");
  
  try{
    Thread.sleep(1000*60*5);
  }
  catch (Exception e){
   System.out.println("error");
  }
  System.out.println("done");
  mfdl.getXY();
 }
}
