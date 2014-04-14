package mapwiki.preprocessor;


import javax.swing.JFrame;

        import prefuse.Constants;
        import prefuse.Display;
        import prefuse.Visualization;
        import prefuse.action.ActionList;
        import prefuse.action.RepaintAction;
        import prefuse.action.assignment.ColorAction;
        import prefuse.action.assignment.DataColorAction;
        import prefuse.action.layout.graph.ForceDirectedLayout;
        import prefuse.activity.Activity;
        import prefuse.controls.DragControl;
        import prefuse.controls.PanControl;
        import prefuse.controls.ZoomControl;
        import prefuse.data.Edge;
        import prefuse.data.Graph;
        import prefuse.data.Node;
        import prefuse.data.Table;
        import prefuse.render.DefaultRendererFactory;
        import prefuse.render.EdgeRenderer;
        import prefuse.render.Renderer;
        import prefuse.render.ShapeRenderer;
        import prefuse.util.ColorLib;
        import prefuse.visual.VisualItem;

public class TestGraph extends Display {

    private Graph makeGraph() {

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

    public TestGraph() {

        super(new Visualization());

        Graph graph = makeGraph();

        m_vis.addGraph("graph", graph);
        m_vis.setInteractive("graph.edges", null, false);
        m_vis.setValue("graph.nodes", null, VisualItem.SHAPE,
                new Integer(Constants.SHAPE_ELLIPSE));

        Renderer nodeR = new ShapeRenderer(20);
        EdgeRenderer edgeR = new EdgeRenderer(prefuse.Constants.EDGE_TYPE_CURVE, prefuse.Constants.EDGE_ARROW_FORWARD);

        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeR);
        drf.setDefaultEdgeRenderer(edgeR);
        m_vis.setRendererFactory(drf);

        int[] palette = new int[] {
                ColorLib.rgb(255,180,180), ColorLib.rgb(190,190,255)
        };
        ColorAction nStroke = new ColorAction("graph.nodes", VisualItem.STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));

        DataColorAction nFill = new DataColorAction("graph.nodes", "flag",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        ColorAction edges = new ColorAction("graph.edges",
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        ColorAction arrow = new ColorAction("graph.edges",
                VisualItem.FILLCOLOR, ColorLib.gray(200));
        ActionList color = new ActionList();
        color.add(nStroke);
        //color.add(nFill);
        color.add(edges);
        color.add(arrow);

        ActionList layout = new ActionList(Activity.INFINITY);
        layout.add(new ForceDirectedLayout("graph"));
        layout.add(new RepaintAction());

        m_vis.putAction("color", color);
        m_vis.putAction("layout", layout);

        setSize(720, 500); // set display size
        pan(360, 250);
        setHighQuality(true);
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());

        m_vis.run("color");
        m_vis.run("layout");

    }

    public static void main(String[] argv) {
        TestGraph ex = new TestGraph();
        JFrame frame = new JFrame("prefuse example");
        frame.getContentPane().add(ex);
        frame.pack();           // layout components in window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); // show the window
    }

}