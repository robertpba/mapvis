package mapvis.graphic;

import com.sun.deploy.util.ArrayUtil;
import com.sun.scenario.effect.Effect;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.models.Dir;
import mapvis.models.LeafRegion;
import mapvis.models.Region;
import mapvis.models.Tile;


import java.util.*;

/**
 * Created by dacc on 10/26/2015.
 */
public class RegionRenderer {
    static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6, 7},
            new int[]{ 6,  7,  8,  9},
    };

    private final Canvas canvas;

    HexagonalTilingView view;

    final double sideLength;

    final double COS30 = Math.cos(Math.toRadians(30));
    final double[] points;
    final double[] x;
    final double[] y;
    int drawIndex;
    int maxDrawIndex;
    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        super();
        drawIndex = 0;
        maxDrawIndex = 1;
        System.out.println("Creating: " + this.getClass().getName());
        this.sideLength = view.SideLength;
        this.view = view;
        this.canvas = canvas;

        points = new double[]{
                - sideLength/2, - sideLength*COS30, //  0 - 1
                sideLength/2, - sideLength*COS30,  // 5  c  2
                sideLength,     0.0,               //  4 - 3
                sideLength/2,   sideLength*COS30,
                - sideLength/2,   sideLength*COS30,
                - sideLength,     0.0
        };
        x = new double[]{
                - sideLength/2,
                sideLength/2,
                sideLength,
                sideLength/2,
                - sideLength/2,
                - sideLength
        };
        y = new double[]{
                - sideLength*COS30,
                - sideLength*COS30,
                0.0,
                sideLength*COS30,
                sideLength*COS30,
                0.0
        };
    }

    private boolean isTileVisibleOnScreen(Tile<INode> tile, Point2D topleftBorder, Point2D bottomRightBorder)
    {
        return tile.getX() > topleftBorder.getX()
                && tile.getX() < bottomRightBorder.getX()
                && tile.getY() > topleftBorder.getY()
                && tile.getY() < bottomRightBorder.getY();
    }


    private Tuple2<List<Double>, List<Double>> drawHexagonBorders(int x, int y, List<Dir> directions, GraphicsContext g) {
        g.save();
        Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);
        g.translate(point2D.getX(), point2D.getY());

        TileStyler<INode> styler = view.getStyler();

        List<Double> xValues = new ArrayList();
        List<Double> yValues = new ArrayList();
        if (!styler.isVisible(x,y))
            return new Tuple2<>(xValues, yValues);

//        Color col = styler.getColor(x,y);
//        g.setFill(col);
//
//        g.fillPolygon(this.x, this.y, this.x.length);


        g.setLineCap(StrokeLineCap.ROUND);
        Collections.reverse(directions);
        for (Dir direction : directions) {
            g.setLineWidth(styler.getBorderWidth(x, y, direction));
            g.setStroke(styler.getBorderColor(x, y, direction));
            int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
            g.strokeLine(points[pointIndices[0]], points[pointIndices[1]], points[pointIndices[2]], points[pointIndices[3]]);
            xValues.add(points[pointIndices[0]]);
            xValues.add(points[pointIndices[2]]);

            yValues.add(points[pointIndices[1]]);
            yValues.add(points[pointIndices[3]]);
        }

        g.restore();
        return new Tuple2<>(xValues, yValues);
    }

    public void drawRegionHelper(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder)
    {
        drawIndex = 0;
        drawRegion(regionToDraw, topleftBorder, bottomRightBorder);
    }
    public static double roundTo4Digits(double val){
        return Math.round(100.0 * val) / 100.0;
    }

    public static Point2D roundToCoordinatesTo4Digits(Point2D point2D){
        return new Point2D(roundTo4Digits(point2D.getX()), roundTo4Digits(point2D.getY()));
    }
    public void drawRegion(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder){
        TileStyler<INode> styler = view.getStyler();
        GraphicsContext g = canvas.getGraphicsContext2D();
        if(regionToDraw.isLeaf()) {
            g.save();
//            g.setLineWidth(2);
            LeafRegion<INode> leafRegion = (LeafRegion<INode>) regionToDraw;

            if(leafRegion.getLeafElements().size() == 0){
                g.restore();
                return;
            }
            INode nodeItem = leafRegion.getNodeItem();
//            Tile<INode> iNodeTile = leafRegion.getLeafElements().get(0);
//            if(!iNodeTile.getItem().getLabel().equals("#5") && !iNodeTile.getItem().getLabel().equals("#4")){
//                g.restore();
//                return;
//            }
            List<LeafRegion.BoundaryShape> boundaryShapes = leafRegion.computeCoordinatesNew();

            Color regionColor = view.getStyler().getColorByValue(nodeItem);
            g.setFillRule(FillRule.NON_ZERO);
            g.beginPath();
            g.setFill(regionColor);
            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
                for (int i = 0; i < boundaryShape.xValues.length; i++) {
                    double x = boundaryShape.xValues[i];
                    double y = boundaryShape.yValues[i];
                    if(i == 0){
                        g.moveTo(x, y);
                    }else{
                        g.lineTo(x, y);
                    }
                }
            }
            g.closePath();
            g.fill();
            g.stroke();
            g.restore();
        }else{
            for (Object iNodeRegion : regionToDraw.getChildRegions()) {
                drawRegion((Region) iNodeRegion, topleftBorder, bottomRightBorder);
            }
        }
    }

}
