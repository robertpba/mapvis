package mapvis.layouts.pea.model;

import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.event.*;
import mapvis.layouts.pea.model.handler.*;
import mapvis.utils.SpatialIndex;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static mapvis.utils.PointExtension.add;
import static mapvis.utils.PointExtension.length;


public class MapModel {
    public int iteration = 0;

    SpatialIndex<Node> index;

    Tree<Node> tree;
    Set<Node> leaves;

    public Map<Node, Map<String, Object>> data = new HashMap<>();
    public Object getValue(Node vertex, String key ){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            return null;

        return map.get(key);
    }
    public Object setValue(Node vertex, String key, Object value){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            data.put(vertex , map = new HashMap<>());

        return map.put(key, value);
    }


    public Node getRoot() {
        return tree.getRoot();
    }
    public Polygon getPolygon(Node vertex){
        return (Polygon) getValue(vertex, "polygon");
    }
    public Collection<Node> getLeaves(){
        return leaves;
    }

    public Collection<Node> getChildren(Node vertex){
        return tree.getChildren(vertex);
    }
    public Set<Node> getAllNodes(){
        return tree.getNodes();
    }

    public interface ToInitialValue<V> {
        Point2D getPosition(V v);
        double getMass(V v);
    }

    public MapModel(Tree<Node> tree, ToInitialValue toInitialValue){
        this.tree = tree;

        double maxX = 0;
        double maxY = 0;

        leaves =  tree.getNodes().stream()
                .filter(n->tree.getChildren(n).size() == 0)
                .collect(Collectors.toSet());

        for (Node n : getLeaves()) {
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

    public Polygon findSurroundingRegion(Point2D point, Node exclude) {
        for (Node leaf : index.neighbours(point)) {
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


        index.update((Node)event.polygon.node, new Rectangle2D.Double(
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
