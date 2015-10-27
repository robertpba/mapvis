package mapvis.layouts.pea.model;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.NodeUtils;
import mapvis.layouts.pea.gui.RenderAction;
import mapvis.layouts.pea.model.event.*;
import mapvis.layouts.pea.model.handler.*;
import mapvis.utils.SpatialIndex;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

import static mapvis.utils.PointExtension.add;
import static mapvis.utils.PointExtension.length;


public class MapModel {
    public int iteration = 0;

    SpatialIndex<INode> index;

    Collection<INode> leaves;
    Collection<INode> nodes;
    Node root;

    public Map<INode, Map<String, Object>> data = new HashMap<>();
    public Object getValue(INode vertex, String key ){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            return null;

        return map.get(key);
    }
    public Object setValue(INode vertex, String key, Object value){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            data.put(vertex , map = new HashMap<>());

        return map.put(key, value);
    }


    public Node getRoot() {
        return root;
    }
    public Polygon getPolygon(INode vertex){
        return (Polygon) getValue(vertex, "polygon");
    }
    public Collection<INode> getLeaves(){
        return leaves;
    }

    public Collection<INode> getChildren(Node vertex){
        return vertex.getChildren();
    }
    public Collection<INode> getAllNodes(){
        return nodes;
    }

    public interface Initializer {
        default Point2D getPosition(INode n) {
            return new Point2D.Double((double)n.getVal("x"), (double)n.getVal("y"));
        }
        default double getMass(INode n) {
            return (double)n.getVal("size");
        }
    }

    public MapModel(Node root, Initializer initializer){
        double maxX = 0;
        double maxY = 0;

        this.root = root;
        leaves = NodeUtils.getLeaves(root);
        nodes = NodeUtils.getDecedents(root);
        nodes.add(root);


        for (INode n : getLeaves()) {
            setValue(n, "polygon" ,new Polygon(
                    n,
                    initializer.getPosition(n).getX(),
                    initializer.getPosition(n).getY(),
                    initializer.getMass(n)));

            maxX = Math.max(maxX, initializer.getPosition(n).getX());
            maxY = Math.max(maxY, initializer.getPosition(n).getY());
        }

        listeners.add(new UpdatePolygonSizeWhenVertexMoved());

        double c = Math.min(maxX, maxY) / Math.sqrt(getLeaves().size());
        index = new SpatialIndex<>( (int)(maxY / c) ,(int)(maxY / c) , c);
    }

    public Polygon findSurroundingRegion(Point2D point, INode exclude) {
        for (INode leaf : index.neighbours(point)) {
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


        index.update(event.polygon.node, new Rectangle2D.Double(
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
