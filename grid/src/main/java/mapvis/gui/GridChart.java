package mapvis.gui;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import mapvis.grid.Grid;
import mapvis.grid.Pos;
import utils.PanZoomPanel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class GridChart extends Parent {
    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double SideLength = 10;

    static protected Polygon standardHexagon;
    static {
        standardHexagon = new Polygon();
        standardHexagon.getPoints().addAll(
                -SideLength/2, -SideLength*COS30,
                SideLength/2, -SideLength*COS30,
                SideLength, 0.0,
                SideLength/2, SideLength*COS30,
                -SideLength/2, SideLength*COS30,
                -SideLength, 0.0);

        //    0 - 1
        //  5   c   2
        //    4 - 3
    }

    public Grid<?> grid;

    public Point2D toChartCoordinate(int x, int y){
        double cx = x * 3 * SideLength / 2;
        double cy;
        cy = 2 * COS30 * SideLength * y;

        if (x % 2 != 0) {
            cy += COS30 * SideLength;
        }

        return new Point2D(cx, cy);
    }
    public Point toGridCoordinate(double x, double y){
        double cx = x / 3 * 2 / SideLength;
        int nx = (int) Math.round(cx);
        int ny;

        if (nx%2 == 0) {
            ny = (int)Math.round(y / 2 / COS30 / SideLength);
        }else {
            ny = (int)Math.round((y - COS30 * SideLength) / 2 / COS30 / SideLength);
        }

        return new Point(nx, ny);
    }

    public GridChart(){
        super();
        updateHexagons();
    }

    Map<Pos, Shape> hexagons = new HashMap<>();

    public void updateHexagons(){
        getChildren().removeAll();

        if (grid == null)
            return;

        hexagons.clear();

        grid.foreach(t-> updateHexagon(t.getX(), t.getY()));

        getChildren().addAll(hexagons.values());
    }

    private void updateHexagon(int x, int y) {
        Object o = grid.get(x, y);
        Pos pos = new Pos(x, y);
        Shape shape = hexagons.get(pos);
        if (shape == null){
            Polygon polygon = new Polygon();
            polygon.getPoints().addAll(standardHexagon.getPoints());
            polygon.setFill(colorMap==null?Color.RED:colorMap.apply(o));
            shape = polygon;
            hexagons.put(pos, shape);
        }

        Point2D point2D = toChartCoordinate(x, y);
        shape.setTranslateX(point2D.getX());
        shape.setTranslateY(point2D.getY());
    }

    public Function<Object, Color> colorMap;
}
