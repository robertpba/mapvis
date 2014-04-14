package mapvis.liquidvis.model;

public class Vertex {
    public Polygon polygon;
    public int indexOfVertex;
    public double x;
    public double y;

    public int moveCount;

    public int momentum = 0;


    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vertex(Polygon polygon, int indexOfVertex) {
        this.polygon = polygon;
        this.indexOfVertex = indexOfVertex;
    }
    
    public Vector2D getPoint()
    {
        return new Vector2D(x,y);
    }
}
