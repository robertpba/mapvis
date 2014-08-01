package mapvis.liquidvis.util;

import mapvis.commons.algorithm.CircleOverlapRemoval;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PatrickFormatLoader2 {

    public int height;
    public int width;

    public Node root;
    public DirectedGraph<Node, DefaultEdge> graph;

    public Node load(String treefile, String pointfile) throws IOException {
        graph = loadCategoryName(treefile);
        loadPoints(pointfile);
        scaleAttribute(root);
        filterUnpositionedNodes();
        filterZeroNodes();
        filterNoiseNodes();
        convertToVisibleTree();
        //refinePoints();

        return root;
    }

    public Map<Integer, Node> nodemap = new HashMap<>();

    public List<Node> visbl = new ArrayList<>();


    protected DirectedGraph<Node, DefaultEdge> loadCategoryName(String categoryNameFilePath) throws FileNotFoundException {
        List<Node> nodes = new ArrayList<>();
        Map<Integer, ArrayList<Integer>> edges = new HashMap<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileReader(categoryNameFilePath));

            while (scanner.hasNext()) {
                Node node = new Node();
                node.pageId = scanner.nextInt();
                node.name = scanner.next().replace('_', ' ');
                node.level = scanner.nextInt();

                int parentId = scanner.nextInt();

                nodemap.put(node.pageId, node);
                nodes.add(node);

                if (parentId == 0) {
                    root = node;
                    scanner.next();
                    scanner.next();
                    scanner.next();
                    continue;
                }

                scanner.next();
                node.figure = scanner.nextInt();
                node.figure2 = node.figure;
                scanner.next();

                ArrayList<Integer> children = edges.get(parentId);
                if (children == null)
                    edges.put(parentId, children = new ArrayList<>());
                children.add(node.pageId);

            }

            for (Map.Entry<Integer, ArrayList<Integer>> entry : edges.entrySet()) {
                Node parent = nodemap.get(entry.getKey());
                parent.children = entry.getValue().stream()
                        .map(nodemap::get)
                        .toArray(Node[]::new);
                for (Node child : parent.children) {
                    child.parent = parent;
                }
            }


            DirectedGraph<Node, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

            nodes.forEach(graph::addVertex);

            for (Map.Entry<Integer, ArrayList<Integer>> entry : edges.entrySet()) {
                Node parent = nodemap.get(entry.getKey());
                entry.getValue().stream()
                        .map(nodemap::get)
                        .forEach(child ->
                                        graph.addEdge(parent, child)
                        );
            }

            return graph;
        }finally{
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    protected void loadPoints(String filePath) throws IOException {
        Scanner scanner = null;

        double xmax = 0;
        double ymax = 0;

        try {
            FileInputStream stream = new FileInputStream(filePath);
            stream.skip(3); // skip BOM 65279 / EF BB BF
            scanner = new Scanner(new InputStreamReader(stream));

            while (scanner.hasNext()) {
                int id = scanner.nextInt();
                scanner.next();
                scanner.next();

                Node node = nodemap.get(id);
                node.x = scanner.nextDouble();
                node.y = scanner.nextDouble();

                xmax = Math.max(node.x, xmax);
                ymax = Math.max(node.y, ymax);

                visbl.add(node);
            }

            width  = (int)xmax;
            height = (int)ymax;
        }finally{
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    protected void scaleAttribute(Node root){
        root.figure = root.figure == 0.0? 0.0
                : Math.log10(root.figure) / Math.log10(1.1) * 10;
        graph.outgoingEdgesOf(root)
                .forEach(e -> scaleAttribute(graph.getEdgeTarget(e)));
    }

    protected boolean filterUnpositionedNodes(){
        List<Node> collect = graph.vertexSet().stream()
                .filter(v -> !visbl.contains(v) && v != root)
                .collect(Collectors.toList());
        graph.removeAllVertices(collect);
        return true;
    }

    protected void filterZeroNodes() {
        List<Node> collect = graph.vertexSet().stream()
                .filter(v -> graph.outDegreeOf(v) == 0 && v.figure == 0)
                .collect(Collectors.toList());
        graph.removeAllVertices(collect);
    }

    protected void filterNoiseNodes() {

        List bl = Arrays.asList(
                "Former very good articles",
                "Former good articles");

        List<Node> collect = graph.vertexSet().stream()
                .filter(v -> bl.contains(v.name))
                .collect(Collectors.toList());
        graph.removeAllVertices(collect);
    }


    protected void updateCache(Node root, Collection<Node> nodes, List<Node> leaves){
        nodes.add(root);
        if (root.children.length == 0) {
            leaves.add(root);
        }
        for (Node child : root.children) {
            updateCache(child, nodes, leaves);
        }
    }

    protected void convertToVisibleTree() {
        DirectedGraph<Node, DefaultEdge> ngraph = new DefaultDirectedGraph<>(DefaultEdge.class);
        convertToVisibleTree(graph, ngraph  ,  root);
        graph = ngraph;

    }

    private boolean convertToVisibleTree(DirectedGraph<Node, DefaultEdge> oldgraph,
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

        if (node.figure > 0) {
            Node node1 = new Node();
            node1.figure = node.figure;
            node1.figure2 = node.figure2;
            node1.name = node.name;
            node1.pageId = node.pageId;
            node1.level = node.level + 1;
            node1.x  = node.x;
            node1.y  = node.y;

            newgraph.addVertex(node1);
            newgraph.addEdge(node, node1);
        }

        oldgraph.outgoingEdgesOf(node).stream()
                .map(e -> oldgraph.getEdgeTarget(e))
                .filter(v -> convertToVisibleTree(oldgraph, newgraph, v))
                .forEach(v -> {
                    newgraph.addEdge(node, v);
                });

        return true;
    }

    public void refinePoints(double scale, double tolerance) {
        //int scale = 8;

        List<Node> leaves = graph.vertexSet().stream()
                .filter(v -> graph.outDegreeOf(v) == 0)
                .collect(Collectors.toList());

        leaves.forEach(node -> {
            node.x = (node.x + 50) * scale;
            node.y = (node.y + 50) * scale;
        });

        graph.vertexSet().stream()
                .forEach(node-> node.figure = node.figure * scale * scale);

        width  = (int)((width + 100)* scale);
        height = (int)((height + 100)* scale);


        CircleOverlapRemoval<Node> removal = new CircleOverlapRemoval<>(leaves,
                n -> new Point2D.Double(n.x, n.y),
                n -> Math.sqrt(n.figure * tolerance / Math.PI));
        removal.run(1000);

        for (Node node : leaves) {
            Point2D position = removal.getPosition(node);
            node.x = position.getX();
            node.y = position.getY();
        }
    }

    public static void main (String[] args) throws IOException {
        PatrickFormatLoader2 loader = new PatrickFormatLoader2();

        loader.graph = loader.loadCategoryName("data/simple.txt");
        loader.loadPoints("data/points.txt");
        loader.scaleAttribute(loader.root);
        loader.filterUnpositionedNodes();
        loader.filterZeroNodes();
        loader.filterNoiseNodes();
        loader.convertToVisibleTree();
        //loader.refinePoints();

        printNode(loader, "", loader.root, 6);
    }


    public static void printNode(PatrickFormatLoader2 loader, String indent, Node node, int maxlevel)
    {
        if (node.level >= maxlevel)
            return;

        System.out.printf("%d%s %s%-5d %s %f (%5d, %5d)\n",
                node.level, loader.graph.outDegreeOf(node)==0?"+":" "  , indent, node.pageId, node.name, node.figure, (int)node.x, (int)node.y);

        loader.graph.outgoingEdgesOf(node).stream()
                .map(e -> loader.graph.getEdgeTarget(e))
                .forEach(n -> printNode(loader, indent + "  ", n, maxlevel));
    }


}
