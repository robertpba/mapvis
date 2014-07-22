package mapvis.gui;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import mapvis.grid.Dir;
import mapvis.grid.Grid;
import mapvis.grid.Pos;
import mapvis.grid.Tile;
import mapvis.tree.MPTT;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class GridChart extends Parent {
    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double SideLength = 10;

    public Grid<Integer> grid;
    public MPTT<Integer> tree;

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

    Map<Pos, Hexagon> hexagons = new HashMap<>();

    public void updateHexagons(){
        getChildren().removeAll();

        if (grid == null)
            return;

        hexagons.clear();

        grid.foreach(t-> updateHexagon(t.getX(), t.getY()));

        getChildren().addAll(hexagons.values());
    }

    private void updateHexagon(int x, int y) {
        Integer o = grid.get(x, y);
        Pos pos = new Pos(x, y);
        Hexagon hexagon = hexagons.get(pos);
        if (hexagon == null){
            Hexagon polygon = new Hexagon(SideLength,x,y);
            applyStyle(o, polygon);
            hexagon = polygon;
            hexagons.put(pos, hexagon);
        }

        Point2D point2D = toChartCoordinate(x, y);
        hexagon.setTranslateX(point2D.getX());
        hexagon.setTranslateY(point2D.getY());
    }

    private void applyStyle(Integer o, Hexagon polygon) {
        polygon.fill.setFill(colorMap == null ? Color.RED : colorMap.apply(o));
        applyBorderStyle(o, polygon, Dir.N, polygon.n);
        applyBorderStyle(o, polygon, Dir.NE, polygon.ne);
        applyBorderStyle(o, polygon, Dir.NW, polygon.nw);
        applyBorderStyle(o, polygon, Dir.S, polygon.s);
        applyBorderStyle(o, polygon, Dir.SE, polygon.se);
        applyBorderStyle(o, polygon, Dir.SW, polygon.sw);
    }

    private void applyBorderStyle(Integer o, Hexagon polygon, Dir dir, Line border) {
        Tile<Integer> tn = grid.getNeighbour(polygon.x, polygon.y, dir);
        if (tn == null || tn.obj == null || o.equals(tn.obj)) {
            border.setVisible(false);
            return;
        }

        Integer lca = tree.getLCA(o, tn.obj);
        if (lca == null)
            border.setVisible(false);
        else {
            int d = tree.getDepth(lca);
            border.setStrokeWidth((4.0 - d)*(4.0 - d)/2);
            border.setStroke(Color.BLACK);
        }
    }


    public Function<Object, Color> colorMap;

    static class Hexagon extends Group {
        private final double sideLength;
        private final int x;
        private final int y;
        public Line n;
        public Line s;
        public Line nw;
        public Line ne;
        public Line se;
        public Line sw;
        public Polygon fill;

        public Hexagon(double sideLength, int x, int y)
        {
            super();
            this.sideLength = sideLength;
            this.x = x;
            this.y = y;

            final double COS30 = Math.cos(Math.toRadians(30));
            final double[] points = {
                    -sideLength/2, -sideLength*COS30, //  0 - 1
                    sideLength/2, -sideLength*COS30,  // 5  c  2
                    sideLength, 0.0,                  //  4 - 3
                    sideLength/2, sideLength*COS30,
                    -sideLength/2, sideLength*COS30,
                    -sideLength, 0.0
            };

            fill = new Polygon();
            for (double point : points) fill.getPoints().add(point);

            n  = new Line(points[0], points[1], points[2], points[3]);
            ne = new Line(points[2], points[3], points[4], points[5]);
            se = new Line(points[4], points[5], points[6], points[7]);
            s  = new Line(points[6], points[7], points[8], points[9]);
            sw = new Line(points[8], points[9], points[10], points[11]);
            nw = new Line(points[10], points[11], points[0], points[1]);

            getChildren().addAll(fill, n, ne, se, s, sw, nw);
        }
    }
}
