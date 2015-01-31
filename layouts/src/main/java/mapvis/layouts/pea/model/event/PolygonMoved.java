package mapvis.layouts.pea.model.event;

import mapvis.layouts.pea.model.Polygon;

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
