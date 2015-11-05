package mapvis.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.models.*;


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
    int maxBorderLevelToShow;
    Random rand = new Random(0);

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        super();
        drawIndex = 0;
        maxBorderLevelToShow = 1;
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

//
//    private Tuple2<List<Double>, List<Double>> drawHexagonBorders(int x, int y, List<Dir> directions, GraphicsContext g) {
//        g.save();
//        Point2D point2D = HexagonalTilingView.hexagonalToPlain(x, y);
//        g.translate(point2D.getX(), point2D.getY());
//
//        TileStyler<INode> styler = view.getStyler();
//
//        List<Double> xValues = new ArrayList();
//        List<Double> yValues = new ArrayList();
//        if (!styler.isVisible(x,y))
//            return new Tuple2<>(xValues, yValues);
//
////        Color col = styler.getColor(x,y);
////        g.setFill(col);
////
////        g.fillPolygon(this.x, this.y, this.x.length);
//
//
//        g.setLineCap(StrokeLineCap.ROUND);
//        Collections.reverse(directions);
//        for (Dir direction : directions) {
//            g.setLineWidth(styler.getBorderWidth(x, y, direction));
//            g.setStroke(styler.getBorderColor(x, y, direction));
//            int[] pointIndices = DIR_TO_POINTS[direction.ordinal()];
//            g.strokeLine(points[pointIndices[0]], points[pointIndices[1]], points[pointIndices[2]], points[pointIndices[3]]);
//            xValues.add(points[pointIndices[0]]);
//            xValues.add(points[pointIndices[2]]);
//
//            yValues.add(points[pointIndices[1]]);
//            yValues.add(points[pointIndices[3]]);
//        }
//
//        g.restore();
//        return new Tuple2<>(xValues, yValues);
//    }

    public void drawRegionHelper(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder)
    {
        drawIndex = 0;
        totalDrawnBorder = 0;
        drawRegion(regionToDraw, topleftBorder, bottomRightBorder);
    }

    public static double roundTo4Digits(double val){
        return Math.round(100.0 * val) / 100.0;
    }

    public static Point2D roundToCoordinatesTo4Digits(Point2D point2D){
        return new Point2D(roundTo4Digits(point2D.getX()), roundTo4Digits(point2D.getY()));
    }
    int totalDrawnBorder = 0;
    public void drawRegion(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder){
        TileStyler<INode> styler = view.getStyler();
        GraphicsContext g = canvas.getGraphicsContext2D();
        if(regionToDraw.isLeaf()) {
            g.save();
//            if(totalDrawnBorder != maxBorderLevelToShow){
//                totalDrawnBorder++;
//                g.restore();
//                return;
//            }
//            totalDrawnBorder++;
//            g.setLineWidth(2);
            LeafRegion<INode> leafRegion = (LeafRegion<INode>) regionToDraw;

//            if(leafRegion.getLeafElements().size() == 0){
//                g.restore();
//                return;
//            }
            INode nodeItem = leafRegion.getNodeItem();
//            Tile<INode> iNodeTile = leafRegion.getLeafElements().get(0);
//            if(!iNodeTile.getItem().getLabel().equals("#5") && !iNodeTile.getItem().getLabel().equals("#4")){
//                g.restore();
//                return;
//            }
//            List<LeafRegion.BoundaryShape> boundaryShapes = leafRegion.computeCoordinates();
            List<Tuple2<Border<INode>, LeafRegion.BoundaryShape>> boundaryShapes = leafRegion.computeCoordinates();
            if(boundaryShapes.size() == 0){
                g.restore();
                return;
            }

//            Color regionColor = view.getStyler().getColorByValue(nodeItem);
//            g.setFillRule(FillRule.NON_ZERO);
//            g.setLineWidth(2);

            int shapeIndex = 0;

//            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//            List<Tuple2<List<String>, LeafRegion.BoundaryShape>> shapeAndDescrp = new ArrayList<>();
//            for (Tuple2<Border<INode>, LeafRegion.BoundaryShape> boundaryShape : boundaryShapes) {
//                List<String> borderItemsDesc = new ArrayList<>();
//                for (Border.BorderItem borderItem : boundaryShape.first.getBorderItems()) {
//                    borderItemsDesc.addAll(borderItem.text);
//                }
//                shapeAndDescrp.add(new Tuple2<>(borderItemsDesc, boundaryShape.second));
//            }

            List<Tuple2<String, LeafRegion.BoundaryShape>> shapeAndDescrp = new ArrayList<>();
            for (Tuple2<Border<INode>, LeafRegion.BoundaryShape> boundaryShape : boundaryShapes) {
                List<String> borderItemsDesc = new ArrayList<>();
//                shapeAndDescrp.add(new Tuple2<>(Integer.toString(boundaryShape.first.getLevel()), boundaryShape.second));
//                String labelA = boundaryShape.first.getNodeA().getId();
                String labelA = boundaryShape.first.getNodeA()==  null ? "X" : boundaryShape.first.getNodeA().getId();
                String labelB = boundaryShape.first.getNodeB()==  null ? "X" : boundaryShape.first.getNodeB().getId();
                shapeAndDescrp.add(new Tuple2<>(labelA+ "/" + labelB, boundaryShape.second));
            }


            int borderItemIndex = 0;
//            for (Tuple2<List<String>, LeafRegion.BoundaryShape> boundaryShapeTuple : shapeAndDescrp){
            for (Tuple2<String, LeafRegion.BoundaryShape> boundaryShapeTuple : shapeAndDescrp){

                LeafRegion.BoundaryShape boundaryShape = boundaryShapeTuple.second;

                int borderLevel = boundaryShape.level;
                if(borderLevel > maxBorderLevelToShow)
                    continue;
                if(borderLevel == 0){
                    g.setStroke(Color.BLACK);
                }else if(borderLevel == 1){
                    g.setStroke(Color.BLUE);
                }else if(borderLevel == 2){
                    g.setStroke(Color.GREEN);
                }else if(borderLevel == 3){
                    g.setStroke(Color.YELLOW);
                }else{
                    g.setStroke(Color.GREY);
                }
//                g.setLineWidth( 4.0/ (boundaryShape.level + 1));
//                boundaryShape.level
//                List<String> border = boundaryShapeTuple.first;
//                g.beginPath();
//                g.setFill(regionColor);
                double lastX = 0;
                double lastY = 0;

                g.setLineWidth(2);
                g.strokePolyline(boundaryShape.xValues, boundaryShape.yValues, boundaryShape.xValues.length);

                for (int i = 0; i < boundaryShape.xValues.length; i++) {
                    double x = boundaryShape.xValues[i];
                    double y = boundaryShape.yValues[i];
                    if(i == 0 || i == boundaryShape.xValues.length - 1){
                        g.setLineWidth(1);
                        g.setFill(Color.GREEN);
                        g.fillOval(x, y, 4, 4);
                    }
                    lastX = x;
                    lastY = y;
//                    if(i == 0){
//                        g.moveTo(x, y);
//                    }else{
//                        g.lineTo(x, y);
//                    }

                    g.setFont(new Font(g.getFont().getName(), 5));
                    g.setLineWidth(0.3);
//                    g.strokeText(Integer.toString(boundaryShape.level), x, y);
//                    g.strokeText(Integer.toString(shapeIndex) + "/" + Integer.toString(i), x, y);

                }


                g.setLineWidth(1);
                g.setFont(new Font(g.getFont().getName(), 10));
//                g.strokeText(Integer.toString(shapeIndex), boundaryShape.xValues[boundaryShape.xValues.length/2], boundaryShape.yValues[boundaryShape.xValues.length/2]);

//                g.strokeText(boundaryShapeTuple.first, lastX, lastY);
                shapeIndex += 1;
//                g.closePath();
//                g.setStroke(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
//                g.stroke();
            }

//            for (Tuple2<Border<INode>, LeafRegion.BoundaryShape> boundaryShapeTuple : boundaryShapes){
//            for (Tuple2<List<String>, LeafRegion.BoundaryShape> boundaryShapeTuple : shapeAndDescrp){
//                LeafRegion.BoundaryShape boundaryShape = boundaryShapeTuple.second;
//                List<String> border = boundaryShapeTuple.first;
//                g.beginPath();
//                g.setFill(regionColor);
//                double lastX = 0;
//                double lastY = 0;
//
//
//                for (int i = 0; i < boundaryShape.xValues.length; i++) {
//
//                    double x = boundaryShape.xValues[i];
//                    double y = boundaryShape.yValues[i];
//                    lastX = x;
//                    lastY = y;
//                    if(i == 0){
//                        g.moveTo(x, y);
//                    }else{
//                        g.lineTo(x, y);
//                    }
//
////                    g.strokeText(border.get(i), lastX, lastY, 6);
////                    g.strokeText(Integer.toString(shapeIndex) + "/" + Integer.toString(i), lastX, lastY, 6);
//                }
//
//                shapeIndex += 10;
//                g.closePath();
//                g.setStroke(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
//                g.stroke();
//            }




//            g.fill();

            g.restore();
        }else{
            for (Object iNodeRegion : regionToDraw.getChildRegions()) {
                drawRegion((Region) iNodeRegion, topleftBorder, bottomRightBorder);
            }
        }
    }
}
