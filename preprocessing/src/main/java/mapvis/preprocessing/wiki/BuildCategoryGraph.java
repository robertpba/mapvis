package mapvis.preprocessing.wiki;

import org.jgrapht.alg.CycleDetector;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class BuildCategoryGraph {

    public static Graph loadCategoryGraph(InputStream file) throws IOException {
        CsvListReader reader = new CsvListReader(
                new InputStreamReader(file), CsvPreference.TAB_PREFERENCE);
        reader.getHeader(true);

        List<String> read;

        DirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

        while ((read = reader.read()) != null){
            if (read.get(0).startsWith("#"))
                continue;

            String sub = read.get(1).replace('_', ' ');
            String sup = read.get(2).replace('_', ' ');
            graph.addVertex(sub);
            graph.addVertex(sup);
            graph.addEdge(sup, sub);
            //System.out.printf("%s %s\n", sub, sup);
        }

        return graph;
    }

    public static java.util.Set detectCycles(DirectedGraph graph){
        CycleDetector detector = new CycleDetector(graph);
        //return detector.detectCycles();
        //return detector.findCyclesContainingVertex("Articles");
        return detector.findCycles();
    }

    public static DirectedGraph makeTree(DirectedGraph graph, Object start)
    {
        DirectedGraph<Object, DefaultEdge> subGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

        Queue<Object> toCheck = new LinkedList<>();
        toCheck.add(start);
        subGraph.addVertex(start);

        Object checking;
        while ((checking = toCheck.poll()) != null){
            for (Object edge : graph.outgoingEdgesOf(checking)) {
                Object target = graph.getEdgeTarget(edge);
                if (subGraph.addVertex((String)target)) {
                    subGraph.addEdge(checking, (String)target);
                    toCheck.add((String)target);
                }
            }
        }

        return subGraph;
    }

    public static void depthFirstVisit(DirectedGraph graph, Object start, Visitor visitor)
    {
        depthFirstVisit(graph, start, visitor, 1);
    }

    private static void depthFirstVisit(DirectedGraph graph, Object start, Visitor visitor, int level)
    {
        visitor.visit(start, level);
        for (Object edge : graph.outgoingEdgesOf(start)) {
            depthFirstVisit(graph, graph.getEdgeTarget(edge), visitor,level+1);
        }
    }

    static interface Visitor{
        void visit(Object obj, int level);
    }


    public static void main(String[] args) throws IOException {
        DirectedGraph<String, DefaultEdge> graph = (DirectedGraph<String, DefaultEdge>) loadCategoryGraph(new FileInputStream("data/wikicategory.txt"));

        graph = makeTree(graph, "Articles");

        depthFirstVisit(graph, "Articles", (o, i) -> {
            while (--i>0)
                System.out.print("  ");
            System.out.println(o);
        });


        Set cycles = detectCycles(graph);
        if (cycles.size() > 0) {
            System.out.println("Cycles found");

            for (Object cycle : cycles) {
                System.out.println(cycle);

                for (DefaultEdge edge : graph.outgoingEdgesOf((String) cycle)) {
                    System.out.println("  "+graph.getEdgeTarget(edge));
                }
            }
        }
        else
            System.out.println("No cycles found");



        BreadthFirstIterator<String, DefaultEdge> iterator = new BreadthFirstIterator<>(graph, "Articles");

        while (iterator.hasNext()){
            String s = iterator.next();
            //System.out.println(s);
        }

    }
}
