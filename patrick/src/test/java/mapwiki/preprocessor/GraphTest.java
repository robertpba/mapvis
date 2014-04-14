package mapwiki.preprocessor;



import mapwiki.common.Database;
import org.testng.annotations.Test;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.*;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static prefuse.Constants.*;
import static prefuse.visual.VisualItem.*;

@Test(enabled = false)
public class GraphTest {

    @Test
    public void visualize () throws Exception {

        // -- 1. load the data ------------------------------------------------

        // load the socialnet.xml file. it is assumed that the file can be
        // found at the root of the java classpath
        Graph graph = makeGraph();

        Visualization m_vis = new Visualization();

        m_vis.addGraph("graph", graph);
        m_vis.setInteractive("graph.edges", null, false);
        m_vis.setValue("graph.nodes", null, SHAPE,
                new Integer(SHAPE_ELLIPSE));

        //Renderer nodeR = new ShapeRenderer(20);
        LabelRenderer nodeR = new LabelRenderer("name");
        EdgeRenderer edgeR = new EdgeRenderer(EDGE_TYPE_CURVE,EDGE_ARROW_FORWARD);

        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeR);
        drf.setDefaultEdgeRenderer(edgeR);
        m_vis.setRendererFactory(drf);


        ActionList color = new ActionList();

        int[] palette = new int[] {
                ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)
        };
        ColorAction nStroke = new ColorAction("graph.nodes", STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));

//        DataColorAction nFill = new DataColorAction("graph.nodes", "flag",
//                NOMINAL, VisualItem.FILLCOLOR, palette);
        //DataColorAction nFill = new DataColorAction("graph.nodes", "flag", NOMINAL, FILLCOLOR, palette);
        //color.add(nFill);

        ColorAction nodeText = new ColorAction("graph.nodes", TEXTCOLOR);
        nodeText.setDefaultColor(ColorLib.gray(0));
        color.add(nodeText);

        ColorAction nodeStroke = new ColorAction("graph.nodes", STROKECOLOR);
        nodeStroke.setDefaultColor(ColorLib.gray(100));
        color.add(nodeStroke);

        ColorAction nodeFill = new ColorAction("graph.nodes", FILLCOLOR);
        nodeFill.setDefaultColor(ColorLib.gray(255));
        color.add(nodeFill);


        ColorAction edges = new ColorAction("graph.edges", STROKECOLOR, ColorLib.gray(200));
        ColorAction arrow = new ColorAction("graph.edges", FILLCOLOR, ColorLib.gray(200));
        color.add(nStroke);

        color.add(edges);
        color.add(arrow);

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph"));
        //layout.add(new RandomLayout("graph"));
        layout.add(new RepaintAction());

        m_vis.putAction("color", color);
        m_vis.putAction("layout", layout);

        Display display = new Display(m_vis);

        display.setSize(720, 500); // set display size
        display.pan(360, 250);
        display.setHighQuality(true);
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());

        m_vis.run("color");
        m_vis.run("layout");


        JFrame frame = new JFrame("prefuse example");
        // ensure application exits when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(display);
        frame.pack();           // layout components in window
        frame.setVisible(true); // show the window

        Thread.sleep(1000000);
    }

    private Graph makeGraph2() {

        // Create tables for node and edge data, and configure their columns.
        Table nodeData = new Table();
        Table edgeData = new Table(0,1);
        nodeData.addColumn("flag", boolean.class);
        edgeData.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class);
        edgeData.addColumn(Graph.DEFAULT_TARGET_KEY, int.class);
        edgeData.addColumn("label", String.class);
        // Need more data in your nodes or edges?  Just add more
        // columns.

        // Create Graph backed by those tables.  Note that I'm
        // creating a directed graph here also.
        Graph g = new Graph(nodeData, edgeData, true);

        // Create some nodes and edges, each carrying some data.
        // There are surely prettier ways to do this, but for the
        // example it gets the job done.
        for ( int i=0; i<3; ++i ) {
            Node n1 = g.addNode();
            Node n2 = g.addNode();
            Node n3 = g.addNode();
            n1.setBoolean("flag", false);
            n2.setBoolean("flag", true);
            n3.setBoolean("flag", true);
            Edge e1 = g.addEdge(n1, n2);
            Edge e2 = g.addEdge(n1, n3);
            Edge e3 = g.addEdge(n2, n3);
            e1.setString("label", "a");
            e2.setString("label", "a");
            e3.setString("label", "a");
        }
        Edge e4 = g.getEdge(g.addEdge(0, 3));
        Edge e5 = g.getEdge(g.addEdge(3, 6));
        Edge e6 = g.getEdge(g.addEdge(6, 0));
        e4.setString("label", "b");
        e5.setString("label", "b");
        e6.setString("label", "b");
        return g;
    }


    @Test
    public void makeGraphTest() throws Exception
    {
        makeGraph();
    }

    public Graph makeGraph() throws Exception{

        Database db = new Database("jdbc:mysql://localhost:3306/simplewiki","root","root");

        Connection connection = db.getConnection();

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT cl_from, page_title, cl_to " +
                "FROM categorylinks, page " +
                "WHERE cl_from = page_id AND " +
                "page_namespace = 14 ORDER BY page_id ");

        // Create tables for node and edge data, and configure their columns.
        Table nodeData = new Table();
        Table edgeData = new Table(0,1);
        nodeData.addColumn("name", String.class);
        nodeData.addColumn("flag", boolean.class);
        edgeData.addColumn(Graph.DEFAULT_SOURCE_KEY, int.class);
        edgeData.addColumn(Graph.DEFAULT_TARGET_KEY, int.class);
        edgeData.addColumn("label", String.class);
        // Need more data in your nodes or edges?  Just add more
        // columns.

        // Create Graph backed by those tables.  Note that I'm
        // creating a directed graph here also.
        Graph g = new Graph(nodeData, edgeData, true);

        // Create some nodes and edges, each carrying some data.
        // There are surely prettier ways to do this, but for the
        // example it gets the job done.

        Map<String, Node> map = new HashMap<>();
        ResultSet resultSet = preparedStatement.executeQuery();
        int c = 100000;
        int node=0;
        int edge=0;

        ArrayList<String> blacklist = new ArrayList<String>();
        blacklist.add("stubs");
        blacklist.add("by year");

        Set<String> killedList = new HashSet<>();

        omit:
        while (resultSet.next() && c-- > 0) {
            String from = new String(resultSet.getBytes("page_title"), "utf8").replace('_', ' ');
            String to = new String(resultSet.getBytes("cl_to"), "utf8").replace('_', ' ');

            for (String s : blacklist) {
                if (from.contains(s)) {
                    killedList.add(from);
                    continue omit;
                }
                if (to.contains(s)) {
                    killedList.add(to);
                    killedList.add(from);
                    continue omit;
                }
            }
            if (killedList.contains(to)) {
                killedList.add(from);
                killedList.add(to);
                continue omit;
            }


            Node fromNode = map.get(from);
            Node toNode = map.get(to);

            if (fromNode == null) {
                fromNode = g.addNode();
                fromNode.setString("name", from);
                fromNode.setBoolean("flag", true);
                map.put(from, fromNode);
                System.out.println("add node: " + from);
                node++;
            }
            if (toNode == null){
                toNode = g.addNode();
                toNode.setString("name", to);
                toNode.setBoolean("flag", true);
                map.put(to, toNode);
                System.out.println("add node: "+to);
                node++;
            }

            Edge e1 = g.addEdge(fromNode, toNode);
            edge ++;
            System.out.println("add edge: "+from +" -> "+to);
        }
        System.out.printf("nodes: %d  edges: %d", node, edge);
        resultSet.close();
        return g;
    }
}