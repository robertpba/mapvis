package mapvis.liquidvis.model;

import java.util.function.Function;

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

    public double figure;
    public double mass;
    public Function<Node, Double> scale;

    public int moveBackCount = 0;
    public int moveForwardCount = 0;


    public double polygonArea() {
        double area = 0;         // Accumulates area in the loop
        int j = npoints - 1;  // The last vertex is the 'previous' one to the first

        for (int i = 0; i < npoints; i++) {
            area = area + (vertices[j].x + vertices[i].x) * (vertices[i].y - vertices[j].y);
            j = i;  //j is previous vertex to i
        }
        return area / 2;
    }

    // formulae: http://en.wikipedia.org/wiki/Centroid#Centroid_of_polygon
    public Vector2D calcCentroid(){
        Vector2D centroid = new Vector2D(0, 0);
        double signedArea = 0.0;
        double x0 = 0.0; // Current vertex X
        double y0 = 0.0; // Current vertex Y
        double x1 = 0.0; // Next vertex X
        double y1 = 0.0; // Next vertex Y
        double a = 0.0;  // Partial signed area

        // For all vertices except last
        int i=0;
        for (i=0; i<npoints-1; ++i)
        {
            x0 = vertices[i].x;
            y0 = vertices[i].y;
            x1 = vertices[i+1].x;
            y1 = vertices[i+1].y;
            a = x0*y1 - x1*y0;
            signedArea += a;
            centroid.x += (x0 + x1)*a;
            centroid.y += (y0 + y1)*a;
        }

        // Do last vertex
        x0 = vertices[i].x;
        y0 = vertices[i].y;
        x1 = vertices[0].x;
        y1 = vertices[0].y;
        a = x0*y1 - x1*y0;
        signedArea += a;
        centroid.x += (x0 + x1)*a;
        centroid.y += (y0 + y1)*a;

        signedArea *= 0.5;
        centroid.x /= (6.0*signedArea);
        centroid.y /= (6.0*signedArea);

        return centroid;
    }

    public Polygon(Node node, Function<Node, Double> scale){
        this.node = node;
        this.scale = scale;

        originX = node.x;
        originY = node.y;
        figure  = node.figure;

        mass    = scale.apply(node);

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
            if ( ((vertices[i].y>testy) != (vertices[j].y>testy)) &&
                    (testx < (vertices[j].x-vertices[i].x) * (testy-vertices[i].y) / (vertices[j].y-vertices[i].y) + vertices[i].x) )
                c = !c;
        }
        return c;
    }
    
    public Vector2D getVertexPosition(int index) {
        return new Vector2D(this.vertices[index].x, this.vertices[index].y);
    }
    public Vertex getVertex(int index)
    {
        return vertices[index];
    }
    
    public Vector2D getOrigin()
    {
        return new Vector2D(this.originX, this.originY);
    }

    public void setVertex(int index, Vector2D vertex) {
        this.vertices[index].x = vertex.x;
        this.vertices[index].y = vertex.y;
        
        updateBounds(vertex.x, vertex.y);
    }
    public void setVertex(Vertex vertex, Vector2D position) {
        if (vertex.polygon != this)
            throw new RuntimeException("can't set other polygon's vertex");

        vertex.x = position.x;
        vertex.y = position.y;

        updateBounds(position.x, position.y);
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
            double x = this.vertices[i].x;
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            double y = this.vertices[i].y;
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
    }
    
    public boolean contains(Vector2D pos) {
        return contains(pos.x, pos.y);
    }
}
