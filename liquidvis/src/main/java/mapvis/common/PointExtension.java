package mapvis.common;

import java.awt.geom.Point2D;

public class PointExtension {
    public static Point2D add(Point2D p1, Point2D p2){
        double x = p1.getX() + p2.getX();
        double y = p1.getY() + p2.getY();
        return new Point2D.Double(x,y);
    }

    public static Point2D subtract(Point2D p1, Point2D p2) {
        double x = p1.getX() - p2.getX();
        double y = p1.getY() - p2.getY();
        return new Point2D.Double(x,y);
    }

    public static Point2D divide(Point2D p, double divisor) {
        double x = p.getX() / divisor;
        double y = p.getY() / divisor;
        return new Point2D.Double(x,y);
    }

    public static Point2D midpoint(Point2D p1, Point2D p2) {
        double x = (p1.getX() + p2.getX()) / 2;
        double y = (p1.getY() + p2.getY()) / 2;
        return new Point2D.Double(x,y);
    }

    public static double length(Point2D p) {
        return Math.sqrt(p.getX()*p.getX() + p.getY()*p.getY());
    }


    public static Point2D unit(Point2D p) {
        double norm = PointExtension.length(p);
        double x = p.getX() / norm;
        double y = p.getY() / norm;
        return new Point2D.Double(x, y);
    }

    public static double includedAngle(Point2D previous, Point2D middle, Point2D next) {
        // > 0 clockwise
        return Math.toDegrees(Math.atan2(next.getX() - middle.getX(),
                next.getY() - middle.getY()) -
                Math.atan2(previous.getX() - middle.getX(),
                        previous.getY() - middle.getY()));
    }
}
