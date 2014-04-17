package mapvis.liquidvis.model;

import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.event.VertexMoved;
import mapvis.liquidvis.model.handler.*;

import java.util.*;

public class MapModel {
    private HashMap<Node, Polygon> polygons = new HashMap<>();
    public Node root;
    public int iteration = 0;

    public MapModel(Node root) {
        this.root = root;

        listeners.add(new UpdatePolygonSizeWhenVertexMoved());

        createPolygon(root, 300);
    }

    private void createPolygon(Node node, int scale) {
        if (node.children == null || node.children.length == 0)
            polygons.put(node, new Polygon(node, scale));
        else for (Node child : node.children)
            createPolygon(child, scale);
    }

    // getters and setters
    public HashMap<Node, Polygon> getPolygons() {
        return polygons;
    }


    public Polygon findSurroundingRegion(Vector2D point, Node level2, Node exclude) {
        //for (Node level2 : PolygonModeling.root.children)
        //for (Node level3 : level2.children)
        for (Node level3 : level2.children) {
            for (Node level4 : level3.children) {
                // forth level
                if (level4 == exclude)
                    continue;
                Polygon polygon = polygons.get(level4);
                if (polygon.contains(point.x, point.y))
                    return polygon;
            }
        }
        return null;
    }

    public Vertex findNearestVertex(Vector2D srcPos, Polygon dstRegion) {
        double minDistance = srcPos.distance(dstRegion.getVertexPosition(0));
        int minPosition = 0;

        for (int f = 1; f < dstRegion.npoints; f++) {
            double d = srcPos.distance(dstRegion.getVertexPosition(f));
            if (d < minDistance) {
                minDistance = d;
                minPosition = f;
            }
        }
        return dstRegion.getVertex(minPosition);
    }


    public List<ModelEventListener> listeners = new ArrayList<>();

    public void fireModelEvent(ModelEvent event) {
        applyEvent(event);
        for (ModelEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public void applyEvent(ModelEvent event) {
        if (event instanceof VertexMoved)
            applyEvent((VertexMoved)event);
        else if (event instanceof IterationFinished)
            applyEvent((IterationFinished)event);
    }

    public void applyEvent(VertexMoved event){
        event.polygon.setVertex(event.vertex.indexOfVertex, event.destination);
    }
    public void applyEvent(IterationFinished event){
        iteration++;
    }


}