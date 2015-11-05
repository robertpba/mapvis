package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tuple2;

import java.util.*;

/**
 * Created by dacc on 10/26/2015.
 */
public class LeafRegion<T> extends Region<T> {
    public static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6, 7},
            new int[]{ 6,  7,  8,  9},
    };
    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double sideLength = 10;
    public static final double[] POINTS = new double[]{
        - sideLength/2, - sideLength*COS30, //  0 - 1
                sideLength/2, - sideLength*COS30,  // 5  c  2
                sideLength,     0.0,               //  4 - 3
                sideLength/2,   sideLength*COS30,
                - sideLength/2,   sideLength*COS30,
                - sideLength,     0.0
    };


    private Set<Border<T>> borders;
    private List<Tile<T>> leafElements;
    private List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw;
    private Color color;
    private List<BoundaryShape> boundaryShapes;


    public static class BoundaryShape2{
        public List<Point2D> boundaryPoints;

        public BoundaryShape2() {
            boundaryPoints = new ArrayList<>();
        }
    }

    public static class BoundaryShape{
        public double[] xValues;
        public double[] yValues;
        public List<String> text;
        public int level;

        public BoundaryShape(double[] xValues, double[] yValues) {
            this.xValues = xValues;
            this.yValues = yValues;
            this.text = new ArrayList<>();
        }
    }

    public LeafRegion(T treeItem) {
        super(Collections.<Region<T>>emptyList(), treeItem);
        this.borders = new HashSet<>();
    }

    public Set<Border<T>> getBorders() {
        return borders;
    }

    public void addBorder(Border<T> border) {
        this.borders.add(border);
    }

    public void addBorders(List<Border<T>> borders) {
        this.borders.addAll(borders);
    }

    @Override
    public boolean isLeaf(){
        return true;
    }

    public List<Tile<T>> getLeafElements() {
        return leafElements;
    }

    public List<Tuple2<Tile<T>, List<Dir>>> getTileAndDirectionsToDraw() {
        return tileAndDirectionsToDraw;
    }

    public static double roundTo4Digits(double val){
        return Math.round(100.0 * val) / 100.0;
    }

    public static Point2D roundToCoordinatesTo4Digits(Point2D point2D){
        return new Point2D(roundTo4Digits(point2D.getX()), roundTo4Digits(point2D.getY()));
    }

    public T getNodeItem() {
        return nodeItem;
    }
}
