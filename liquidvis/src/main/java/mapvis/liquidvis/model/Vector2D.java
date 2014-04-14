package mapvis.liquidvis.model;

public class Vector2D {
    public double x;
    public double y;

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public double distance(Vector2D vertex)
    {
        return distance(this, vertex);
    }
    
    public static double distance(Vector2D v1, Vector2D v2)
    {
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    public static Vector2D add(Vector2D v1, Vector2D v2)
    {
        double x = v1.x + v2.x;
        double y = v1.y + v2.y;
        return new Vector2D(x,y);
    }

    public static Vector2D subtract(Vector2D v1, Vector2D v2)
    {
        double x = v1.x - v2.x;
        double y = v1.y - v2.y;
        return new Vector2D(x,y);
    }
    
    public static Vector2D divide(Vector2D v, double divisor)
    {
        double x = v.x / divisor;
        double y = v.y / divisor;
        return new Vector2D(x,y);
    }
    
    public static Vector2D average(Vector2D v1, Vector2D v2)
    {
        double x = (v1.x + v2.x) / 2;
        double y = (v1.y + v2.y) / 2;
        return new Vector2D(x,y);
    }
    
    public double norm()
    {
        return Math.sqrt(x*x + y*y);
    }
    
    public Vector2D unit()
    {
        double norm = this.norm();
        double x = this.x / norm;
        double y = this.y / norm;
        return new Vector2D(x,y);
    }

    private double includedAngle(Vector2D previous, Vector2D middle, Vector2D next) {
        // > 0 clockwise
        return Math.toDegrees(Math.atan2(next.x - middle.x, next.y - middle.y) -
                Math.atan2(previous.x - middle.x, previous.y - middle.y));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Vector2D))
            return false;

        Vector2D rhs = (Vector2D)obj;

        return !(rhs.x != this.x || rhs.y != this.y);

    }

    @Override
    public int hashCode()
    {
        long bits = Double.doubleToLongBits(x);
        bits ^= Double.doubleToLongBits(x) * 31;
        return (((int) bits) ^ ((int) (bits >> 32)));
    }
}
