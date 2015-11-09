package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.BorderCoordinatesCalcImpl;
import mapvis.graphic.RegionRenderer;

import java.util.*;

import static mapvis.graphic.BorderCoordinatesCalcImpl.getRoundedPoint2DPointForBorderHexLocation;

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
    private Color color;
    private List<BoundaryShape> boundaryShapes;

    public LeafRegion(T o, int level) {
        super(Collections.emptyList(), o, level);
        this.borders = new HashSet<>();
    }

    public static class BoundaryShape2{
        public List<Point2D> boundaryPoints;

        public BoundaryShape2() {
            boundaryPoints = new ArrayList<>();
        }
    }

    public static class BoundaryShape<T>{
        public Border<T> border;
        public double[] xValues;
        public double[] yValues;
        public List<String> text;
        public int level;
        public boolean renderColored;
        public Color color;
        public boolean coorinateNeedToBeReversed;

        public BoundaryShape(double[] xValues, double[] yValues, Border<T> border) {
            this.xValues = xValues;
            this.yValues = yValues;
            this.text = new ArrayList<>();
            this.border = border;
            this.renderColored = false;
            this.color = Color.TRANSPARENT;
            this.coorinateNeedToBeReversed = false;
        }
        public Point2D getStartPoint(){
            if(xValues.length > 0)
                return new Point2D(xValues[0], yValues[0]);
            return new Point2D(0, 0);
        }

        public Point2D getEndPoint(){
            if(xValues.length > 0)
                return new Point2D(xValues[xValues.length - 1], yValues[xValues.length - 1]);
            return new Point2D(0, 0);
        }

        @Override
        public String toString() {
            String result = "X" + "\n";
            for (double xValue : xValues) {
                result += xValue + "\n";
            }
            result += "Y" + "\n";
            for (double yValue : yValues) {
                result += yValue + "\n";
            }
            return result;
        }
    }

//    public LeafRegion(T treeItem) {
//        super(Collections.<Region<T>>emptyList(), treeItem);
//        this.borders = new HashSet<>();
//    }

    public Set<Border<T>> getBorders() {
        return borders;
    }

    public void addBorder(Border<T> border) {
        if(border.getBorderItems().size() == 0 || border == null)
            return;

        for (Border<T> tBorder : this.borders) {
            if(isSameBorder(border, tBorder)){
                return;
            }
        }
        this.borders.add(border);

    }

    public static boolean isSameBorder(Border newBorder, Border existingBorder) {
        if(newBorder == null || existingBorder == null)
            return true;

        if(newBorder.getNodeA() == null && existingBorder.getNodeA() != null)
            return false;

        if(newBorder.getNodeB() == null && existingBorder.getNodeB() != null)
            return false;

        if(newBorder.getNodeA() != null && !newBorder.getNodeA().equals(existingBorder.getNodeA()))
            return false;

        if(newBorder.getNodeB() != null && !newBorder.getNodeB().equals(existingBorder.getNodeB()))
            return false;

        Point2D newBorderStartPoint = getRoundedPoint2DPointForBorderHexLocation(newBorder.getStartPoint());
        Point2D existingBorderStartPoint = getRoundedPoint2DPointForBorderHexLocation(existingBorder.getStartPoint());

        Point2D newBorderLastPoint = getRoundedPoint2DPointForBorderHexLocation(newBorder.getLastPoint());
        Point2D existingBorderLastPoint = getRoundedPoint2DPointForBorderHexLocation(existingBorder.getLastPoint());
        if(newBorderStartPoint.equals(existingBorderStartPoint) && newBorderLastPoint.equals(existingBorderLastPoint)
                || newBorderStartPoint.equals(existingBorderLastPoint) && newBorderLastPoint.equals(existingBorderStartPoint)){
            if(newBorder.getNodeA() != null && !newBorder.getNodeA().equals(existingBorder.getNodeA()))
                return false;
            if(newBorder.getNodeB() != null && !newBorder.getNodeB().equals(existingBorder.getNodeB()))
                return false;
//            if( !newBorder.getNodeA().equals(existingBorder.getNodeA()) || !newBorder.getNodeB().equals(existingBorder.getNodeB()))
//                return false;

            return true;
        }

        return false;
    }

    public void addBorders(List<Border<T>> borders) {
        this.borders.addAll(borders);
    }

    @Override
    public boolean isLeaf(){
        return true;
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
