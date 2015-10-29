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

    private List<Border<T>> borders;



    public static class BoundaryShape2{
        public List<Point2D> boundaryPoints;

        public BoundaryShape2() {
            boundaryPoints = new ArrayList<>();
        }
    }

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
    List<BoundaryShape> boundaryShapes;

    public LeafRegion(List<Border<T>> borders, T treeItem) {
        super(Collections.<Region<T>>emptyList(), treeItem);
        this.borders = borders;
    }

    public List<Border<T>> getBorders() {
        return borders;
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

        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        List<BoundaryShape> computedBoundaryShapes = new ArrayList<>();


        for (Border<T> border : borders) {
            if(border.getBorderItems().size() == 0){
                continue;
            }
            for (Border.BorderItem borderItem : border.getBorderItems()) {

                int x = borderItem.borderItem.first.getX();
                int y = borderItem.borderItem.first.getY();

                Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);

                for (Dir direction : borderItem.borderItem.second) {

                    int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
                    double xStart = POINTS[pointIndices[0]] + point2D.getX();
                    double yStart = POINTS[pointIndices[1]] + point2D.getY();

                    Point2D startPoint = roundToCoordinatesTo4Digits(new Point2D(xStart, yStart));

                    xValues.add(startPoint.getX());
                    yValues.add(startPoint.getY());
                }
            }
            computedBoundaryShapes.add(new BoundaryShape(
                            xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                            yValues.stream().mapToDouble(Double::doubleValue).toArray())
            );
            xValues.clear();
            yValues.clear();
        }
        return computedBoundaryShapes;
//        boundaryShapes = computedBoundaryShapes;
//        return new ArrayList<>(boundaryShapes);
    }

    public T getNodeItem() {
        return nodeItem;
    }
}
