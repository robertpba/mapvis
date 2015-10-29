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

    private final List<Border<T>> borders;

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

    public LeafRegion(List<Tile<T>> prepare, List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw, T nodeItem) {
        super(Collections.<Region<T>>emptyList(), nodeItem);
        this.leafElements = prepare;
        this.tileAndDirectionsToDraw = tileAndDirectionsToDraw;
        this.boundaryShapes = null;
        this.borders = new ArrayList<>();
    }

    public void addNewBorder(Border borderToAdd){
        this.borders.add(borderToAdd);
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
    public List<BoundaryShape> orderBorders(){
        if(boundaryShapes != null){
            return new ArrayList<>(boundaryShapes);
        }

        Map<Point2D, Point2D> startToEnd = new HashMap<>();
        Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder = new HashMap<>();
        createStartPointToEndPointMapping(startToEnd, point2DToBorderAbstrBorder);

        if(startToEnd.size() == 0){
            return Collections.emptyList();
        }
        List<BoundaryShape> computedBoundaryShapes = new ArrayList<>();

        Point2D startPoint = null;
        Point2D initialPoint = null;
        Pos lastPos = null;
        List<BoundaryShape2> orderedBoundaryShapes = new ArrayList<>();
        BoundaryShape2 boundaryShape = new BoundaryShape2();
        int keySetSize = startToEnd.keySet().size();

        List<Border.BorderItem> abstrBoundaries = new ArrayList<>();
        List<Border<T>> borders = new ArrayList<>();
        for(int i = 0; i < keySetSize; i++){

            if(i == 0){
                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
            }
            Point2D endPoint = startToEnd.get(startPoint);
            if(startPoint.equals(initialPoint) && i != 0){
                System.out.println("Circle Detected!:" + i);
                System.out.println("new Shape!\n------------------------");

                Tuple2<Pos, Dir> abstrBorderItem = point2DToBorderAbstrBorder.get(initialPoint);

                boundaryShape.boundaryPoints.add(initialPoint);
                orderedBoundaryShapes.add(boundaryShape);

                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
                endPoint = startToEnd.get(startPoint);
            }
            if(endPoint == null){
                //no circular boundary => found stop
                System.out.println("detected end of boundary");
            }else{
                Tuple2<Pos, Dir> abstrBorderItem = point2DToBorderAbstrBorder.get(startPoint);
                if(abstrBorderItem.first.equals(lastPos)){
                    //append to previous list
                    Tuple2<Pos, List<Dir>> borderItem = abstrBoundaries.get(abstrBoundaries.size() - 1).borderItem;
                    borderItem.second.add(abstrBorderItem.second);
                }else{
                    List<Dir> dirList = new ArrayList<>();
                    dirList.add(abstrBorderItem.second);
                    abstrBoundaries.add(new Border.BorderItem(new Tuple2<>(abstrBorderItem.first, dirList)));
                }
                lastPos = abstrBorderItem.first;
            }

            //remove points to avoid circular points if two boundaries more than one circle
            startToEnd.remove(startPoint, endPoint);
            startPoint = endPoint;
        }
        System.out.println("Circle end!");

        return new ArrayList<>(boundaryShapes);
    }

    private void createStartPointToEndPointMapping(Map<Point2D, Point2D> startToEnd, Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder) {
        for (Tuple2<Tile<T>, List<Dir>> leafTile : tileAndDirectionsToDraw) {
            int x = leafTile.first.getX();
            int y = leafTile.first.getY();
            INode iNode = (INode) leafTile.first.getItem();

            Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);

            for (Dir direction : leafTile.second) {

                int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
                double xStart = POINTS[pointIndices[0]] + point2D.getX();
                double xEnd = POINTS[pointIndices[2]] + point2D.getX();
                double yStart = POINTS[pointIndices[1]] + point2D.getY();
                double yEnd = POINTS[pointIndices[3]] + point2D.getY();

                Point2D startPoint = roundToCoordinatesTo4Digits(new Point2D(xStart, yStart));
                Point2D endPoint = roundToCoordinatesTo4Digits(new Point2D(xEnd, yEnd));

                startToEnd.put(startPoint, endPoint);
                point2DToBorderAbstrBorder.put(startPoint, new Tuple2<>(leafTile.first.getPos(), direction));
            }
        }
    }
    public List<BoundaryShape> computeCoordinatesNew(){
        if(boundaryShapes != null){
            return new ArrayList<>(boundaryShapes);
        }
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        List<BoundaryShape> computedBoundaryShapes = new ArrayList<>();

        List<Border<T>> borders = BorderUtils.orderBorders(tileAndDirectionsToDraw);
        for (Border<T> border : borders) {
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
        if(computedBoundaryShapes.size() > 1){
            System.out.println("X");
            for (Double xValue : xValues) {
                System.out.println(xValue);
            }
            System.out.println("Y");
            for (Double yValue : yValues) {
                System.out.println(yValue);
            }
        }
        boundaryShapes = computedBoundaryShapes;
        return new ArrayList<>(boundaryShapes);
    }
    public List<BoundaryShape> computeCoordinates(){
        if(boundaryShapes != null){
            return new ArrayList<>(boundaryShapes);
        }

        Map<Point2D, Point2D> startToEnd = new HashMap<>();
        Map<Point2D, Tuple2<Pos, Dir>> point2DToBorderAbstrBorder = new HashMap<>();
        createStartPointToEndPointMapping(startToEnd, point2DToBorderAbstrBorder);

        if(startToEnd.size() == 0){
            return Collections.emptyList();
        }
        List<BoundaryShape> computedBoundaryShapes = new ArrayList<>();

        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        Point2D startPoint = null;
        Point2D initialPoint = null;
        List<BoundaryShape2> orderedBoundaryShapes = new ArrayList<>();
        BoundaryShape2 boundaryShape = new BoundaryShape2();
        int keySetSize = startToEnd.keySet().size();
        for(int i = 0; i < keySetSize; i++){
            if(i == 0){
                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
            }
            Point2D endPoint = startToEnd.get(startPoint);
            if(startPoint.equals(initialPoint) && i != 0){
                System.out.println("Circle Detected!:" + i);
                System.out.println("new Shape!\n------------------------");
                boundaryShape.boundaryPoints.add(initialPoint);
                orderedBoundaryShapes.add(boundaryShape);

                xValues.add(initialPoint.getX());
                yValues.add(initialPoint.getY());
                computedBoundaryShapes.add(new BoundaryShape(
                                xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                                yValues.stream().mapToDouble(Double::doubleValue).toArray())
                );
                xValues.clear();
                yValues.clear();
                startPoint = startToEnd.keySet().iterator().next();
                initialPoint = startPoint;
                endPoint = startToEnd.get(startPoint);
            }
            if(endPoint == null){
                //no circular boundary => found stop
                System.out.println("detected end of boundary");
            }else{
                boundaryShape.boundaryPoints.add(startPoint);
                xValues.add(startPoint.getX());
                yValues.add(startPoint.getY());
            }

            //remove points to avoid circular points if two boundaries more than one circle
            startToEnd.remove(startPoint, endPoint);
            startPoint = endPoint;
        }
        System.out.println("Circle end!");
        xValues.add(initialPoint.getX());
        yValues.add(initialPoint.getY());

        computedBoundaryShapes.add(new BoundaryShape(
                        xValues.stream().mapToDouble(Double::doubleValue).toArray(),
                        yValues.stream().mapToDouble(Double::doubleValue).toArray())
        );

        boundaryShapes = computedBoundaryShapes;
        return new ArrayList<>(boundaryShapes);
    }

    public T getNodeItem() {
        return nodeItem;
    }
}
