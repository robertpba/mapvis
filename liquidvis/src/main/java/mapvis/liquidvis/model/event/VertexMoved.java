package mapvis.liquidvis.model.event;

import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vector2D;
import mapvis.liquidvis.model.Vertex;

public class VertexMoved extends ModelEvent {
    public Polygon  polygon;
    public Vertex   vertex;
    public Vector2D origin;
    public Vector2D destination;

    public VertexMoved(){}

    public VertexMoved(int iteration, Vertex vertex, Vector2D origin, Vector2D destination) {
        this.iteration = iteration;
        this.polygon = vertex.polygon;
        this.vertex = vertex;
        this.origin = origin;
        this.destination = destination;
    }

}
