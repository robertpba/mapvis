package mapvis.layouts.dac;

import edu.uci.ics.jung.algorithms.layout.SpringLayout3;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

import java.awt.*;
import java.util.Random;

public class Disperse {
    static <T> DirectedGraph<Entry<T>, Edge<Entry<T>>>
    createGraph(Entry<T> root){
        DirectedGraph<Entry<T>,Edge<Entry<T>>> graph =
                new DirectedSparseMultigraph<>();
        recAddNode(graph, root);
        return graph;
    }
    static <T> void recAddNode(DirectedGraph<Entry<T>, Edge<Entry<T>>> graph, Entry<T> entry) {
        graph.addVertex(entry);

        for (Entry<T> child : entry.children) {
            recAddNode(graph, child);
            graph.addEdge(new Edge<>(entry, child), entry, child);
        }
    }
    static <T> void updatePosition(SpringLayout3<Entry<T>,Edge<Entry<T>>> layout, Entry<T> node){
        node.x = layout.getX(node);
        node.y = layout.getY(node);
        node.children.forEach(c->updatePosition(layout,c));
    }

    public static <T> void
    disperse(Entry<T> root, int steps){
        DirectedGraph<Entry<T>, Edge<Entry<T>>> graph = createGraph(root);
        Random rand = new Random();
        rand.setSeed(0);
        SpringLayout3<Entry<T>, Edge<Entry<T>>> alg =
                new SpringLayout3<>(graph, e->100, rand);
        alg.setSize(new Dimension(1000,1000));
        for (int i = 0; i < steps; i++) {
            alg.step();
        }

        updatePosition(alg, root);
    }
}
