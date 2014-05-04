package mapvis.liquidvis.model.event;

import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vertex;

import java.awt.geom.Point2D;

public class VertexMoved extends ModelEvent {
    public Polygon  polygon;
    public Vertex   vertex;
    public Point2D origin;
    public Point2D destination;

    public VertexMoved(){}

    public VertexMoved(int iteration, Vertex vertex, Point2D origin, Point2D destination) {
        this.iteration = iteration;
        this.polygon = vertex.polygon;
        this.vertex = vertex;
        this.origin = origin;
        this.destination = destination;
    }

}
