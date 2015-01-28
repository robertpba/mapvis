package mapvis.layouts.peap;

import edu.uci.ics.jung.algorithms.layout.SpringLayout3;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import mapvis.common.datatype.Node;

import java.util.*;

public final class PEAPreliminaryLayoutAlgorithm {
    Config config;
    Node root;
    private DirectedGraph<Node, Edge> graph;

    static class Edge{
        Node src;
        Node dst;

        public Node getSrc() {
            return src;
        }
        public Node getDst() {
            return dst;
        }

        public Edge(Node src, Node dst) {
            this.src = src;
            this.dst = dst;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Edge edge = (Edge) o;

            if (!dst.equals(edge.dst)) return false;
            if (!src.equals(edge.src)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = src.hashCode();
            result = 31 * result + dst.hashCode();
            return result;
        }
    }


    public PEAPreliminaryLayoutAlgorithm(Node root) {
        this.root = root;
    }

    int steps;

    void run(int steps){
        this.steps = steps;
        graph = convertToGraph();
        SpringLayout3<Node, Edge> springLayout = disperse(graph, steps);
        for (Node node : graph.getVertices()) {
            node.setVal("x", springLayout.getX(node));
            node.setVal("y", springLayout.getY(node));
        }
        compact(graph, steps);
    }

    DirectedGraph<Node,Edge> convertToGraph(){
        DirectedGraph<Node,Edge> graph = new DirectedSparseMultigraph<>();
        recAddNode(graph, root);
        return graph;
    }
    void recAddNode(DirectedGraph<Node, Edge> graph, Node node) {
        graph.addVertex(node);

        for (Node child : node.getChildren()) {
            recAddNode(graph, node);
            graph.addEdge(new Edge(node, child), node, child);
        }
    }

    SpringLayout3<Node, Edge> disperse(DirectedGraph<Node, Edge> graph, int steps){
        SpringLayout3<Node, Edge> alg = new SpringLayout3<>(graph, e->100, new Random(0));
        for (int i = 0; i < steps; i++) {
            alg.step();
        }
        return alg;
    }

    void compact(DirectedGraph<Node, Edge> graph, int steps){
        for (int i = 0; i < steps; i++) {
            recCompact(graph, root);
        }
    }

    void recCompact(DirectedGraph<Node, Edge> graph, Node node) {
        double x0 = (double) node.getVal("x");
        double y0 = (double) node.getVal("y");
        double r0 = (double) node.getVal("size");

        for (Node child : node.getChildren()) {
            double x1 = (double)child.getVal("x");
            double y1 = (double)child.getVal("y");
            double r1 = (double)child.getVal("size");

            if (distance(x0,y0,r0,x1,y1,r1)>0) {
                final double x1_ = x1 - (x1 - x0) / 10;
                final double y1_ = y1 - (y1 - y0) / 10;

                if (!graph.getVertices().stream()
                        .anyMatch(n -> {
                            double x2 = (double) n.getVal("x");
                            double y2 = (double) n.getVal("y");
                            double r2 = (double) n.getVal("size");
                            return distance(x1_,y1_,r1,x2,y2,r2) < 0;
                        })){
                    child.setVal("x", x1_);
                    child.setVal("y", y1_);
                }
            }
            recCompact(graph, child);
        }
    }

    double distance(double x0, double y0, double r0,
                     double x1, double y1, double r1){
        double d = Math.sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
        double r = Math.max(r0, r1);
        return d - r;
    }
}
