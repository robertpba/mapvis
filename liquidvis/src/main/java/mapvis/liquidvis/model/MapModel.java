package mapvis.liquidvis.model;

import mapvis.liquidvis.model.event.IterationFinished;
import mapvis.liquidvis.model.event.ModelEvent;
import mapvis.liquidvis.model.event.PolygonMoved;
import mapvis.liquidvis.model.event.VertexMoved;
import mapvis.liquidvis.model.handler.*;

import java.util.*;
import java.util.function.Function;

public class MapModel {
    private HashMap<Node, Polygon> polygons = new HashMap<>();
    public Node root;
    public int iteration = 0;

    public MapModel(Node root) {
        this.root = root;

        listeners.add(new UpdatePolygonSizeWhenVertexMoved());

        createPolygon(root, n->n.figure * 300.0);
    }

    public MapModel(Node root, Function<Node, Double> scale) {
        this.root = root;

        listeners.add(new UpdatePolygonSizeWhenVertexMoved());

        createPolygon(root, scale);
    }


    private void createPolygon(Node node, Function<Node, Double> scale) {
        if (node.children == null || node.children.length == 0)
            polygons.put(node, new Polygon(node, scale));
        else for (Node child : node.children)
            createPolygon(child, scale);
    }

    // getters and setters
    public HashMap<Node, Polygon> getPolygons() {
        return polygons;
    }


    public Polygon findSurroundingRegion(Vector2D point, Node exclude) {
        return findSurroundingRegion(point, root, exclude);
    }
    private Polygon findSurroundingRegion(Vector2D point, Node root, Node exclude) {
        for (Polygon polygon : polygons.values()) {
            if (polygon.contains(point.x, point.y))
                return polygon;
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
        else if (event instanceof PolygonMoved)
            applyEvent((PolygonMoved)event);

    }

    public void applyEvent(VertexMoved event){
        event.polygon.setVertex(event.vertex.indexOfVertex, event.destination);
    }
    public void applyEvent(IterationFinished event){
        iteration++;
    }

    public void applyEvent(PolygonMoved event){
        Vector2D d = event.distance;
        Polygon polygon = event.polygon;
        Vector2D oldPivot = event.polygon.getOrigin();
        double norm = d.norm();

        for (Vertex vertex : event.polygon.vertices) {
            polygon.moveBackward(vertex, (int)norm);
            Vector2D pos = vertex.getPoint();
            polygon.setVertex(vertex, Vector2D.add(d, pos));
        }

        Vector2D pivot = Vector2D.add(event.polygon.getOrigin(), d);
        polygon.originX = pivot.x;
        polygon.originY = pivot.y;
    }
}
