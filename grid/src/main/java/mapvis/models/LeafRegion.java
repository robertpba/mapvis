package mapvis.models;

import com.sun.deploy.util.ArrayUtil;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import jdk.nashorn.internal.ir.LiteralNode;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by dacc on 10/26/2015.
 */
public class LeafRegion<T> extends Region<T> {
    static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6, 7},
            new int[]{ 6,  7,  8,  9},
    };
    static final double COS30 = Math.cos(Math.toRadians(30));
    static final double sideLength = 10;
    static final double[] POINTS = new double[]{
        - sideLength/2, - sideLength*COS30, //  0 - 1
                sideLength/2, - sideLength*COS30,  // 5  c  2
                sideLength,     0.0,               //  4 - 3
                sideLength/2,   sideLength*COS30,
                - sideLength/2,   sideLength*COS30,
                - sideLength,     0.0
    };

    public static class BoundaryShape{
        public double[] xValues;
        public double[] yValues;

        public BoundaryShape(double[] xValues, double[] yValues) {
            this.xValues = xValues;
            this.yValues = yValues;
        }
    }

    List<Tile<T>> leafElements;

    List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw;

    Color color;
//    Tuple2<double[], double[]> regionCoordinates;
    List<BoundaryShape> boundaryShapes;
    public LeafRegion(List<Tile<T>> prepare, List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw) {
        super(Collections.<Region<T>>emptyList());
        this.leafElements = prepare;
        this.tileAndDirectionsToDraw = tileAndDirectionsToDraw;
        this.boundaryShapes = null;
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

    public List<BoundaryShape> computeCoordinates(){
        if(boundaryShapes != null){
            return new ArrayList<>(boundaryShapes);
        }

        Map<Point2D, Point2D> startToEnd = new HashMap<>();
        for (Tuple2<Tile<T>, List<Dir>> leafTile : tileAndDirectionsToDraw) {
            int x = leafTile.first.getX();
            int y = leafTile.first.getY();
            INode iNode = (INode) leafTile.first.getItem();
//            color = styler.getColor(x,y);

//            if (isTileVisibleOnScreen(leafTile.first, topleftBorder, bottomRightBorder)) {
                Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);

//                    if (!styler.isVisible(x,y))
//                        continue;

                for (Dir direction : leafTile.second) {
                    int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
                    double xStart = POINTS[pointIndices[0]] + point2D.getX();
                    double xEnd = POINTS[pointIndices[2]] + point2D.getX();
                    double yStart = POINTS[pointIndices[1]] + point2D.getY();
                    double yEnd = POINTS[pointIndices[3]] + point2D.getY();

                    Point2D startPoint = roundToCoordinatesTo4Digits(new Point2D(xStart, yStart));
                    Point2D endPoint = roundToCoordinatesTo4Digits(new Point2D(xEnd, yEnd));

                    startToEnd.put(startPoint, endPoint);
                }
//            }
        }

        if(startToEnd.size() == 0){
            return Collections.emptyList();
        }
        List<BoundaryShape> computedBoundaryShapes = new ArrayList<>();

        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        Point2D startPoint = null;
        Point2D initialPoint = null;

        int keySetSize = startToEnd.keySet().size();
        for(int i = 0; i < keySetSize; i++){
            if(i == 0){
                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
            }
            Point2D endPoint = startToEnd.get(startPoint);

            if(endPoint == null){

                xValues.add(initialPoint.getX());
                yValues.add(initialPoint.getY());
                System.out.println("problem corr point: " +  startPoint + " init point: "  );
                computedBoundaryShapes.add(new BoundaryShape(
                        xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                        yValues.stream().mapToDouble(Double::doubleValue).toArray())
                );
                xValues.clear();
                yValues.clear();
                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
                endPoint = startToEnd.get(startPoint);
            }else{
                xValues.add(startPoint.getX());
                yValues.add(startPoint.getY());
            }
            if(endPoint.equals(initialPoint)){
                System.out.println("circal detected");
//                break;
            }
            //remove points to avoid circular points if two boundaries more than one circle
            startToEnd.remove(startPoint, endPoint);
            startPoint = endPoint;
        }
        xValues.add(initialPoint.getX());
        yValues.add(initialPoint.getY());

        computedBoundaryShapes.add(new BoundaryShape(
                        xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                        yValues.stream().mapToDouble(Double::doubleValue).toArray())
        );

        boundaryShapes = computedBoundaryShapes;
        return new ArrayList<>(boundaryShapes);
    }

}
