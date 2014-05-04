package mapvis.liquidvis.model.event;

import mapvis.liquidvis.model.Polygon;

import java.awt.geom.Point2D;

public class PolygonMoved extends ModelEvent {

    public final Polygon polygon;
    public final Point2D distance;

    public PolygonMoved(int iteration, Polygon polygon, Point2D distance) {
        this.distance = distance;
        this.iteration = iteration;
        this.polygon = polygon;
    }
}
