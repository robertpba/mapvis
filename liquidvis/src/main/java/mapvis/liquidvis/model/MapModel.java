package mapvis.liquidvis.model;

import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.event.*;
import mapvis.liquidvis.model.handler.*;
import org.jgrapht.DirectedGraph;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MapModel<V> {
    public int iteration = 0;

    SpatialIndex<V> index;

    public DirectedGraph<V, Object> tree;
    private Collection<V> leaves;
    private V root;

    public Map<V, Map<String, Object>> data = new HashMap<>();
    public Object getValue(V vertex, String key ){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            return null;

        return map.get(key);
    }
    public Object setValue(V vertex, String key, Object value){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            data.put(vertex , map = new HashMap<>());

        return map.put(key, value);
    }

    public V getRoot() {
        return root;
    }
    public Polygon getPolygon(V vertex){
        return (Polygon) getValue(vertex, "polygon");
    }
    public Collection<V> getLeaves(){
        return Collections.unmodifiableCollection(leaves);
    }
    public Collection<V> getChildren(V vertex){
        return tree.outgoingEdgesOf(vertex).stream()
                .map(v -> tree.getEdgeTarget(v))
                .collect(Collectors.toList());
    }
    public Set<V> getAllNodes(){
        return tree.vertexSet();
    }

    public interface ToInitialValue<V> {
        Point2D getPosition(V v);
        double getMass(V v);
    }

    public MapModel(DirectedGraph<V,?> tree, V root, ToInitialValue<V> toInitialValue){
        this.tree = (DirectedGraph<V, Object>) tree;
        this.root = root;

        leaves = tree.vertexSet()
                .stream()
                .filter(v -> tree.outDegreeOf(v) == 0)
                .collect(Collectors.toList());

        double maxX = 0;
        double maxY = 0;

        for (V n : leaves) {
            setValue(n, "polygon" ,new Polygon(
                    n,
                    toInitialValue.getPosition(n).getX(),
                    toInitialValue.getPosition(n).getY(),
                    toInitialValue.getMass(n)));

            maxX = Math.max(maxX, toInitialValue.getPosition(n).getX());
            maxY = Math.max(maxY, toInitialValue.getPosition(n).getY());
        }

        listeners.add(new UpdatePolygonSizeWhenVertexMoved());

        double c = Math.min(maxX, maxY) / Math.sqrt(leaves.size());
        index = new SpatialIndex<V>( (int)(maxY / c) ,(int)(maxY / c) , c);
    }

    public Polygon findSurroundingRegion(Vector2D point, V exclude) {
        for (V leaf : index.neighbours(new Point2D.Double(point.x, point.y))) {
            Polygon polygon = (Polygon) getValue(leaf, "polygon");
            if (polygon.node == exclude)
                continue;
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

    private void applyEvent(ModelEvent event) {
        if (event instanceof VertexMoved)
            applyEvent((VertexMoved)event);
        else if (event instanceof IterationFinished)
            applyEvent((IterationFinished)event);
        else if (event instanceof PolygonMoved)
            applyEvent((PolygonMoved)event);
        else if (event instanceof CriticalPointArrived)
            applyEvent((CriticalPointArrived)event);

    }
    private void applyEvent(VertexMoved event){
        event.polygon.setVertex(event.vertex.indexOfVertex, event.destination);

        index.update((V) event.polygon.node, new Rectangle2D.Double(
                event.polygon.minX, event.polygon.minY,
                event.polygon.maxX - event.polygon.minX,
                event.polygon.maxY - event.polygon.minY
        ));
    }
    private void applyEvent(IterationFinished event){
        iteration++;
    }
    private void applyEvent(PolygonMoved event){
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
    private void applyEvent(CriticalPointArrived event){}



    public List<RenderAction> actions = new ArrayList<>();

    public void draw(Graphics2D g) {
        //Graphics2D g = image.createGraphics();
        //g.setBackground(Color.WHITE);
        //g.clearRect(0,0, image.getWidth(), image.getHeight());

        for (RenderAction action : actions) {
            action.update();
        }

        for (RenderAction action : actions) {
            action.draw(g);
        }
    }

}
