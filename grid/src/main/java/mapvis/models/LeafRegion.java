package mapvis.models;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<Tile<T>> leafElements;

    List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw;

    Color color;
    Tuple2<double[], double[]> regionCoordinates;

    public LeafRegion(List<Tile<T>> prepare, List<Tuple2<Tile<T>, List<Dir>>> tileAndDirectionsToDraw) {
        super(Collections.<Region<T>>emptyList());
        this.leafElements = prepare;
        this.tileAndDirectionsToDraw = tileAndDirectionsToDraw;
        this.regionCoordinates = null;
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

    public Tuple2<double[], double[]> computeCoordinates(){
        if(regionCoordinates != null){
            return regionCoordinates;
        }

        Map<Point2D, Point2D> startToEnd = new HashMap<>();
        for (Tuple2<Tile<T>, List<Dir>> leafTile : tileAndDirectionsToDraw) {
            int x = leafTile.first.getX();
            int y = leafTile.first.getY();

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
            return new Tuple2<>(new double[0], new double[0]);
        }
        double[] xValues = new double[startToEnd.keySet().size() * 2];
        double[] yValues = new double[startToEnd.keySet().size() * 2];

        Point2D startPoint = null;
        for(int i = 0; i < startToEnd.keySet().size(); i++){
            if(i == 0){
                startPoint = startToEnd.keySet().iterator().next();
            }
            Point2D endPoint = startToEnd.get(startPoint);
            if(endPoint == null){
                System.out.println("problem corr point: " +  startPoint + " init point: "  );
                break;
            }else{
                xValues[i*2] = startPoint.getX();
                yValues[i*2] = startPoint.getY();
                xValues[i*2 + 1] = endPoint.getX();
                yValues[i*2 + 1] = endPoint.getY();
            }
            startPoint = endPoint;
        }
        regionCoordinates = new Tuple2(xValues, yValues);
        return regionCoordinates;
    }

}
