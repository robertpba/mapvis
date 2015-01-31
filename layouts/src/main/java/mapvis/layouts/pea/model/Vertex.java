package mapvis.layouts.pea.model;

import java.awt.geom.Point2D;

public class Vertex {
    public Polygon polygon;
    public int indexOfVertex;

    public Point2D pos;

    public int moveCount;

    public int momentum = 0;

    public Vertex(double x, double y) {
        this.pos = new Point2D.Double(x,y);
    }

    public Vertex(Polygon polygon, int indexOfVertex) {
        this.polygon = polygon;
        this.indexOfVertex = indexOfVertex;
    }

    public Point2D getPoint() {
        return pos;
    }

    public void setPoint(Point2D point) {
        this.pos = point;
    }
}
