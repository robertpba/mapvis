package mapvis.liquidvis.model.event;

import mapvis.liquidvis.model.Polygon;
import mapvis.liquidvis.model.Vector2D;

public class PolygonMoved extends ModelEvent {

    public final Polygon polygon;
    public final Vector2D distance;

    public PolygonMoved(int iteration, Polygon polygon, Vector2D distance) {
        this.distance = distance;
        this.iteration = iteration;
        this.polygon = polygon;
    }
}
