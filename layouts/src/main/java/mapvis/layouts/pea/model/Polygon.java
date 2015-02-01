package mapvis.layouts.pea.model;

import mapvis.common.datatype.Node;

import java.awt.geom.Point2D;

import static mapvis.utils.PointExtension.*;


public class Polygon {
    public Node node;

    public int npoints;
    public Vertex[] vertices;

    public double minX;
    public double maxX;
    public double minY;
    public double maxY;

    public double originX, originY;

    public double area;
    public double mass;

    public int moveBackCount = 0;
    public int moveForwardCount = 0;

    public double polygonArea() {
        double area = 0;         // Accumulates area in the loop
        int j = npoints - 1;  // The last vertex is the 'previous' one to the first

        for (int i = 0; i < npoints; i++) {
            Point2D pj = vertices[j].getPoint();
            Point2D pi = vertices[i].getPoint();

            area = area + (pj.getX() + pi.getX()) * (pi.getY() - pj.getY());
            j = i;  //j is previous vertex to i
        }
        return area / 2;
    }

    // formulae: http://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon
    public Point2D calcCentroid(){
        double x=0.0, y=0.0;

        double signedArea = 0.0;
        double x0, y0; // Current vertex
        double x1, y1; // Next vertex
        double a;  // Partial signed area

        // For all vertices except last
        int i;
        for (i=0; i<npoints-1; ++i)
        {
            Point2D p0 = vertices[i].getPoint();
            Point2D p1 = vertices[i+1].getPoint();

            x0 = p0.getX();
            y0 = p0.getY();
            x1 = p1.getX();
            y1 = p1.getY();
            a = x0*y1 - x1*y0;
            signedArea += a;
            x += (x0 + x1)*a;
            y += (y0 + y1)*a;
        }

        // Do last vertex
        x0 = vertices[i].getPoint().getX();
        y0 = vertices[i].getPoint().getY();
        x1 = vertices[0].getPoint().getX();
        y1 = vertices[0].getPoint().getY();
        a = x0*y1 - x1*y0;
        signedArea += a;
        x += (x0 + x1)*a;
        y += (y0 + y1)*a;

        signedArea *= 0.5;
        x /= (6.0*signedArea);
        y /= (6.0*signedArea);

        return new Point2D.Double(x,y);
    }

    public Polygon(Node node, double x, double y, double mass) {
        this.node = node;

        originX = x;
        originY = y;


        this.mass    = mass;

        npoints = 100;
        vertices = new Vertex[npoints];
        final double unitRadians= Math.PI * 2 / npoints;
        for(int i=0;i<npoints;i++){
            double radians = unitRadians * i;
            vertices[i] = new Vertex(Math.cos(radians)+ originX, Math.sin(radians)+ originY);
            vertices[i].indexOfVertex = i;
            vertices[i].polygon = this;
            vertices[i].moveCount = 1;
        }
        calculateBounds();
    }

    // adapted from the PNPOLY algorithm by W. Randolph Franklin
    public boolean contains(double testx, double testy) {
        if (testx < minX || testx > maxX || testy < minY || testy > maxY)
            return false;

        boolean c = false;
        for (int i = 0, j = npoints-1; i < npoints; j = i++) {
            Point2D pi = vertices[i].getPoint();
            Point2D pj = vertices[j].getPoint();

            if ( ((pi.getY()>testy) != (pj.getY()>testy)) &&
                    (testx < (pj.getX()- pi.getX()) * (testy- pi.getY())
                            / (pj.getY()- pi.getY()) + pi.getX()) )
                c = !c;
        }
        return c;
    }
    
    public Vertex getVertex(int index)
    {
        return vertices[index];
    }
    
    public Point2D getPivot(){
        return new Point2D.Double(this.originX, this.originY);
    }

    public void setVertex(int index, Point2D position) {
        this.vertices[index].setPoint(position);

        updateBounds(position.getX(), position.getY());
    }
    public void setVertex(Vertex vertex, Point2D position) {
        if (vertex.polygon != this)
            throw new RuntimeException("can't set other polygon's vertex");

        vertex.setPoint(position);
        updateBounds(position.getX(), position.getY());
    }


    public void updateBounds(double x, double y) {
        if (x < minX)
            minX = x;
        else if (x > maxX)
            maxX = x;

        if (y < minY)
            minY = y;
        else if (y > maxY)
            maxY = y;

    }
    public void calculateBounds() {
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;

        for (int i = 0; i < npoints; i++) {
            double x = this.vertices[i].getPoint().getX();
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            double y = this.vertices[i].getPoint().getY();
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
    }

    public void moveBackward(Vertex vertex, int unit){
        unit = Math.min (vertex.moveCount, unit);

        if (unit <= 0)
            return;
        Point2D d = subtract(getPivot(), vertex.getPoint());
        Point2D u = divide(d, length(d) / unit);
        vertex.setPoint(subtract(vertex.getPoint(), u ));
        vertex.moveCount -= unit ;
    }

    public boolean contains(Point2D pos) {
        return contains(pos.getX(), pos.getY());
    }
}
