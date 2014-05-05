package mapvis.liquidvis;

import mapvis.liquidvis.gui.*;
import mapvis.liquidvis.gui.actions.*;
import mapvis.liquidvis.method.method3.Method3;
import mapvis.liquidvis.model.*;
import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.handler.CollectStatistics;
import mapvis.liquidvis.util.Node;
import mapvis.liquidvis.util.PatrickFormatLoader2;
import mapvis.vistools.colormap.ColorMap;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;

import static mapvis.vistools.Helper.interpolate;

public class DemoMethod3_5 {

    public static boolean convertToVisibleTree(DirectedGraph<Node, DefaultEdge> oldgraph,
                                        DirectedGraph<Node, DefaultEdge> newgraph,
                                        Node node) {
        if (oldgraph.outDegreeOf(node) == 0){
            if (node.figure > 0) {
                newgraph.addVertex(node);
                return true;
            }
            return false;
        }

        newgraph.addVertex(node);

        oldgraph.outgoingEdgesOf(node).stream()
                .map(e -> oldgraph.getEdgeTarget(e))
                .filter(v -> convertToVisibleTree(oldgraph, newgraph, v))
                .forEach(v -> {
                    newgraph.addEdge(node, v);
                });
        return true;
    }

    public static void main (String[] args) throws IOException, InterruptedException {
        PatrickFormatLoader2 loader = new PatrickFormatLoader2();
        try {

            loader.load(
                    "data/simple.txt",
                    "data/points.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        List<DefaultEdge> edges = new ArrayList<>( loader.graph.outgoingEdgesOf(loader.root));
//
//        Node geography = edges.stream()
//                .map(e -> loader.graph.getEdgeTarget(e))
//                .filter(n -> n.name.contains("Geography"))
//                .findFirst().get();
//
//        for (DefaultEdge edge : edges) {
//            if (loader.graph.getEdgeTarget(edge) != geography)
//                loader.graph.removeEdge(edge);
//        }
//
//        DirectedGraph<Node, DefaultEdge> ngraph = new DefaultDirectedGraph<>(DefaultEdge.class);
//        convertToVisibleTree(loader.graph, ngraph  ,  loader.root);
//        loader.graph = ngraph;
//        PatrickFormatLoader2.printNode(loader, "", loader.root, 9);


//
//        Node byCountry = Arrays.stream(geography.children)
//                .filter(n -> n.name.contains("Geography by country"))
//                .findFirst().get();

        //geography.children = new Node[] { byCountry };

        //loader.root.children = new Node[]{ geography};

        MapModel<Node> model = new MapModel<>(loader.graph, loader.root, new MapModel.ToInitialValue<Node>() {
            @Override
            public Point2D getPosition(Node node) {
                return new Point2D.Double(node.x, node.y);
            }

            @Override
            public double getMass(Node node) {
                return node.figure * 1.45;
            }
        });
        Method3 method = new Method3(model);

        CollectStatistics collectStatistics = new CollectStatistics(model, 100);
        model.listeners.add(collectStatistics);

        BufferedImage image = new BufferedImage(loader.width, loader.height, BufferedImage.TYPE_INT_RGB);

        Visualization observer = new Visualization(image, model);
        observer.backgroundColor = Color.decode("#aae8ff");

        int[] nlevels = {0,1,4,16,64,256,1024};
        String[] ncolors = {"#ffffff","#aae8ff", "#ffff33", "#ffcc00", "#ff9900", "#ff6600", "#cc3300", "#990000"};
        Color[] colors = new Color[ncolors.length];
        for (int i = 0; i < ncolors.length; i++) {
            colors[i] = Color.decode(ncolors[i]);
        }

        final Function<Node, Color> colorMap1 = c -> {
            Polygon polygon = model.getPolygon(c);
            double v = interpolate(((polygon.mass - polygon.area)/polygon.mass), 0.0, 0.5, 1.0, 1.0);
            return ColorMap.JET.getColor(v);
        };

        final Function<Node, Color> colorMap2 = c -> {
            double v = (c).figure2;
            if (v <= nlevels[1])
                return colors[1];
            else if (v <= nlevels[2])
                return colors[2];
            else if (v <= nlevels[3])
                return colors[3];
            else if (v <= nlevels[4])
                return colors[4];
            else if (v <= nlevels[5])
                return colors[5];
            else if (v <= nlevels[6])
                return colors[6];
            else
                return colors[7];
        };

        model.actions.add(new LevelEncoder<>(model));
        model.actions.add(new EncodeLabelText<>(model, n->n.name));
        model.actions.add(new CreateAreas<>(model));

        model.actions.add(new FillNode<>(model, colorMap2));
        model.actions.add(new LabelRender<>(model));
        model.actions.add(new RenderBoundary<>(model));
        //model.actions.add(new RenderOriginCentroid<>(model));

        observer.Start();

        method.IterateUntilStable(100000);

        System.out.println("finished!");

        method.growPolygons();

        System.gc();

        for (CollectStatistics.Statistics l : collectStatistics.ls) {
            System.out.printf("%d\t%f\n", l.iteration, l.error);
        }


    }
}

