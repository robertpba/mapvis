package mapvis.liquidvis.model;

import mapvis.commons.SpatialIndex;
import mapvis.liquidvis.gui.RenderAction;
import mapvis.liquidvis.model.event.*;
import mapvis.liquidvis.model.handler.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static mapvis.commons.PointExtension.*;

public class MapModel<V> {
    public int iteration = 0;

    SpatialIndex<V> index;

    Tree<V> tree;
    Set<V> leaves;

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
        return tree.getRoot();
    }
    public Polygon getPolygon(V vertex){
        return (Polygon) getValue(vertex, "polygon");
    }
    public Collection<V> getLeaves(){
        return leaves;
    }

    public Collection<V> getChildren(V vertex){
        return tree.getChildren(vertex);
    }
    public Set<V> getAllNodes(){
        return tree.getNodes();
    }

    public interface ToInitialValue<V> {
        Point2D getPosition(V v);
        double getMass(V v);
    }

    public MapModel(Tree<V> tree, ToInitialValue<V> toInitialValue){
        this.tree = tree;

        double maxX = 0;
        double maxY = 0;

        leaves =  tree.getNodes().stream()
                .filter(n->tree.getChildren(n).size() == 0)
                .collect(Collectors.toSet());

        for (V n : getLeaves()) {
            setValue(n, "polygon" ,new Polygon(
                    n,
                    toInitialValue.getPosition(n).getX(),
                    toInitialValue.getPosition(n).getY(),
                    toInitialValue.getMass(n)));

            maxX = Math.max(maxX, toInitialValue.getPosition(n).getX());
            maxY = Math.max(maxY, toInitialValue.getPosition(n).getY());
        }

        listeners.add(new UpdatePolygonSizeWhenVertexMoved());

        double c = Math.min(maxX, maxY) / Math.sqrt(getLeaves().size());
        index = new SpatialIndex<>( (int)(maxY / c) ,(int)(maxY / c) , c);
    }

    public Polygon findSurroundingRegion(Point2D point, V exclude) {
        for (V leaf : index.neighbours(point)) {
            Polygon polygon = (Polygon) getValue(leaf, "polygon");
            if (polygon.node == exclude)
                continue;
            if (polygon.contains(point.getX(), point.getY()))
                return polygon;
        }
        return null;
    }

    public Vertex findNearestVertex(Point2D srcPos, Polygon dstRegion) {
        double minDistance = srcPos.distance(dstRegion.getVertex(0).getPoint());
        int minPosition = 0;

        for (int f = 1; f < dstRegion.npoints; f++) {
            double d = srcPos.distance(dstRegion.getVertex(f).getPoint());
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

    @SuppressWarnings("unchecked")
    private void applyEvent(VertexMoved event){
        event.polygon.setVertex(event.vertex.indexOfVertex, event.destination);


        index.update((V)event.polygon.node, new Rectangle2D.Double(
                event.polygon.minX, event.polygon.minY,
                event.polygon.maxX - event.polygon.minX,
                event.polygon.maxY - event.polygon.minY
        ));
    }
    @SuppressWarnings("UnusedParameters")
    private void applyEvent(IterationFinished event){
        iteration++;
    }
    private void applyEvent(PolygonMoved event){
        Point2D d = event.distance;
        Polygon polygon = event.polygon;
        double norm = length(d);

        for (Vertex vertex : event.polygon.vertices) {
            polygon.moveBackward(vertex, (int)norm);
            Point2D pos = vertex.getPoint();
            polygon.setVertex(vertex, add(d, pos));
        }

        Point2D pivot = add(event.polygon.getPivot(), d);
        polygon.originX = pivot.getX();
        polygon.originY = pivot.getY();
    }
    @SuppressWarnings("UnusedParameters")
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
