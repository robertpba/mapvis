package mapvis.models;

import javafx.geometry.Point2D;
import mapvis.graphic.HexagonalTilingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 11/18/2015.
 * @TileBorder stores a segment of a @Border. A @TileBorder
 * stores the position in the grid and a List of Directions.
 * The directions define the edges along the hexagon as in an
 * abstract manner. Concrete screen coordinates of the edges
 * can be calculated using the @TileBorder#calcStartPointForBorderEdge
 * and @TileBorder#calcEndPointForBorderEdge methods.
 */
public class TileBorder {

    public static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6,  7},
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
    private Pos tilePos;
    private List<Dir> directions;

    public TileBorder(Pos tilePos, List<Dir> directions) {
        this.tilePos = tilePos;
        this.directions = directions;
    }

    public TileBorder(Pos tilePos){
        this(tilePos, new ArrayList<>());
    }

    public Pos getTilePos() {
        return tilePos;
    }

    public void setTilePos(Pos tilePos) {
        this.tilePos = tilePos;
    }

    public List<Dir> getDirections() {
        return directions;
    }

    public void setDirections(List<Dir> directions) {
        this.directions = directions;
    }

    public void addDirection(Dir dirToAdd){
        this.directions.add(dirToAdd);
    }

    public static Point2D calcStartPointForBorderEdge(Pos tilePos, Dir direction){
        int x = tilePos.getX();
        int y = tilePos.getY();

        Point2D tilePosInPlain = HexagonalTilingView.hexagonalToPlain(x, y);

        int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
        double xStart = POINTS[pointIndices[0]] + tilePosInPlain.getX();
        double yStart = POINTS[pointIndices[1]] + tilePosInPlain.getY();
        return new Point2D(xStart, yStart);
    }

    public static Point2D calcEndPointForBorderEdge(Pos tilePos, Dir direction){
        Point2D tilePosInPlain = HexagonalTilingView.hexagonalToPlain(tilePos.getX(), tilePos.getY());

        int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
        double xEnd = POINTS[pointIndices[2]] + tilePosInPlain.getX();
        double yEnd = POINTS[pointIndices[3]] + tilePosInPlain.getY();

        return new Point2D(xEnd, yEnd);
    }

}
