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
    public enum RenderState{
        StateA,
        StateB
    }
    public static final RenderState INITIAL_BORDER_RENDERSTATE = RenderState.StateA;
    static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{ 0,  1,  2,  3},
            new int[]{10, 11,  0,  1},
            new int[]{ 2,  3,  4,  5},
            new int[]{ 8,  9, 10, 11},
            new int[]{ 4,  5,  6, 7},
            new int[]{ 6,  7,  8,  9},
    };

    private final Canvas canvas;

    private HexagonalTilingView view;
    private final double sideLength;
    private final double COS30 = Math.cos(Math.toRadians(30));
    private final double[] points;
    private final double[] x;
    private final double[] y;
    private int totalDrawnBorder = 0;
    private int drawIndex;
    public int maxBorderLevelToShow;
    public int shapeIndexToDraw = 0;
    private Random rand = new Random(0);
    private RenderState currentRegionRenderState;
    private final IBorderCoordinatesCalculator<INode> borderCoordinatesCalculatorImpl = new BorderCoordinatesCalcImpl<>();

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        super();
        drawIndex = 0;
        maxBorderLevelToShow = 1;
        System.out.println("Creating: " + this.getClass().getName());
        this.sideLength = view.SideLength;
        this.view = view;
        this.canvas = canvas;
        this.currentRegionRenderState = INITIAL_BORDER_RENDERSTATE;

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

    private RenderState getNextRenderState(){
        switch (currentRegionRenderState) {
            case StateA:
                return RenderState.StateB;
            case StateB:
                return RenderState.StateA;
        }
        return null;
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
        this.currentRegionRenderState = getNextRenderState();
//        System.out.println("Rendered " + drawIndex + "/" + totalDrawnBorder);
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

        g.save();
        borderCoordinatesCalculatorImpl.setRegion(regionToDraw);
        Map<Region<INode>, List<List<LeafRegion.BoundaryShape>>> regionToBoundaryShapes = borderCoordinatesCalculatorImpl.computeCoordinates(maxBorderLevelToShow, true);
        regionToBoundaryShapes = borderCoordinatesCalculatorImpl.getRegionToBoundaries();
//        List<Point2D> debugPoints = borderCoordinatesCalculatorImpl.getDebugPoints();



//        for (Point2D debugPoint : debugPoints) {
//            g.setFill(Color.GREEN);
//            g.fillOval(debugPoint.getX(), debugPoint.getY(), 10, 10);
//        }
//        System.out.println("EntySet" + regionToBoundaryShapes.entrySet().size());
//            Color regionColor = view.getStyler().getColorByValue(nodeItem);
//            g.setFillRule(FillRule.NON_ZERO);
//            g.setLineWidth(2);
//
//            int shapeIndex = 0;
//            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//            List<Tuple2<List<String>, LeafRegion.BoundaryShape>> shapeAndDescrp = new ArrayList<>();
//            for (Tuple2<Border<INode>, LeafRegion.BoundaryShape> boundaryShape : boundaryShapes) {
//                List<String> borderItemsDesc = new ArrayList<>();
//                for (Border.BorderItem borderItem : boundaryShape.first.getBorderItems()) {
//                    borderItemsDesc.addAll(borderItem.text);
//                }
//                shapeAndDescrp.add(new Tuple2<>(borderItemsDesc, boundaryShape.second));
//            }
//            List<Tuple2<String, LeafRegion.BoundaryShape>> shapeAndDescrp = new ArrayList<>();
//            for (Tuple2<Border<INode>, LeafRegion.BoundaryShape> boundaryShape : boundaryShapes) {
//                List<String> borderItemsDesc = new ArrayList<>();
////                shapeAndDescrp.add(new Tuple2<>(Integer.toString(boundaryShape.first.getLevel()), boundaryShape.second));
////                String labelA = boundaryShape.first.getNodeA().getId();
//                String labelA = boundaryShape.first.getNodeA()==  null ? "X" : boundaryShape.first.getNodeA().getId();
//                String labelB = boundaryShape.first.getNodeB()==  null ? "X" : boundaryShape.first.getNodeB().getId();
//                shapeAndDescrp.add(new Tuple2<>(labelA+ "/" + labelB, boundaryShape.second));
//            }
//            int borderItemIndex = 0;
//            for (Tuple2<List<String>, LeafRegion.BoundaryShape> boundaryShapeTuple : shapeAndDescrp){
//            for (Tuple2<String, LeafRegion.BoundaryShape> boundaryShapeTuple : shapeAndDescrp){

        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){

//            totalDrawnBorder++;
            drawIndex++;
//            if(drawIndex != shapeIndexToDraw)
//                continue;
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();

            fillPolygon(g, boundaryShapeTuple, boundaryShapes);
//            drawPolylines(g, boundaryShapeTuple);
        }
        g.setFill(Color.GREEN);

        g.fillOval(-20, -86.6, 10, 10);
        g.fillOval(-110, -43.64, 10, 10);
        g.fillOval(-115, -43.3, 10, 10);



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
            g.restore();
//        }else{
//            for (Object iNodeRegion : regionToDraw.getChildRegions()) {
//                drawRegion((Region) iNodeRegion, topleftBorder, bottomRightBorder);
//            }
//        }
    }

    private void drawPolylines(GraphicsContext g, Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple) {
        int i = 0;
        for (List<LeafRegion.BoundaryShape> boundaryShapes : boundaryShapeTuple.getValue()) {
            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
                g.strokePolygon(boundaryShape.xValues, boundaryShape.yValues, boundaryShape.xValues.length);
                int polygonSize = boundaryShape.yValues.length - 1;
                g.strokeText(Integer.toString(i), boundaryShape.xValues[polygonSize/2], boundaryShape.yValues[polygonSize/2]);
                g.strokeLine(boundaryShape.xValues[0], boundaryShape.yValues[0], boundaryShape.xValues[polygonSize], boundaryShape.yValues[polygonSize]);
                g.setStroke(Color.YELLOW);
                g.strokeOval(boundaryShape.xValues[0], boundaryShape.yValues[0], 2, 2);
                g.setStroke(Color.GREEN);
                g.strokeOval(boundaryShape.xValues[polygonSize], boundaryShape.yValues[polygonSize], 2, 2);
                g.setStroke(Color.BLACK);
                i++;
            }
        }
    }

    private void fillPolygon(GraphicsContext g, Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple,
                             List<List<LeafRegion.BoundaryShape>> regionBoundaryShapes) {

        Region<INode> border = boundaryShapeTuple.getKey();
        TileStyler<INode> styler = view.getStyler();
        Color regionFillColor = styler.getColorByValue(border.getNodeItem());
        g.setFill(regionFillColor);
//        int drawIndex = 0;

        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {
//            if(drawIndex != shapeIndexToDraw)
//                continue;
            if(regionBoundaryShape.size() == 0)
                continue;
            g.beginPath();
            boolean firstDraw = true;
            for (LeafRegion.BoundaryShape partialRegionBoundary : regionBoundaryShape) {
                int level = partialRegionBoundary.border.getLevel();
                if(partialRegionBoundary.coorinateNeedToBeReversed){
                    for (int i = partialRegionBoundary.xValues.length - 1; i >= 0; i--) {
                        if(firstDraw){
                            g.moveTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                            firstDraw = false;
                        }else {
                            g.lineTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                        }
                        totalDrawnBorder++;

                    }
                }else{
                    for (int i = 0; i < partialRegionBoundary.xValues.length; i++) {
                        if(firstDraw){
                            g.moveTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                            firstDraw = false;
                        }else {
                            g.lineTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                        }
                        totalDrawnBorder++;
                    }
                }
            }
            g.closePath();
            g.fill();

//            double[] xValuesArr = xValues.stream().mapToDouble(Double::doubleValue).toArray();
//            double[] yValuesArr = yValues.stream().mapToDouble(Double::doubleValue).toArray();
//            g.fillPolygon(xValuesArr, yValuesArr, xValuesArr.length);
//            double borderWidth = styler.getBorderWidthByLevel(border.getLevel());
//            g.setLineWidth(borderWidth);
//            g.strokePolygon(xValuesArr, yValuesArr, xValuesArr.length);
            drawIndex++;
        }

    }


//    private void fillPolygon(GraphicsContext g, Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple,
//                             List<List<LeafRegion.BoundaryShape>> boundaryShapes) {
//
//        Region<INode> border = boundaryShapeTuple.getKey();
//        TileStyler<INode> styler = view.getStyler();
//        Color regionFillColor = styler.getColorByValue(border.getNodeItem());
//        g.setFill(regionFillColor);
////        int drawIndex = 0;
//
//        for (List<LeafRegion.BoundaryShape> boundaryShapes1 : boundaryShapes) {
////            if(drawIndex != shapeIndexToDraw)
////                continue;
//            List<Double> xValues = new ArrayList<>();
//            List<Double> yValues = new ArrayList<>();
//            boundaryShapes1.stream().forEach(boundaryShape -> {
//                if(boundaryShape.renderColored){
//                    List<Double> debugX = new ArrayList<>();
//                    List<Double> debugY = new ArrayList<>();
//                    for (int i = boundaryShape.xValues.length - 1; i >= 0; i--) {
//                        if(i == 0 || i == boundaryShape.xValues.length - 1){
//                            debugX.add(boundaryShape.xValues[i]);
//                            debugY.add(boundaryShape.yValues[i]);
//                        }
//
//                    }
//                    g.setStroke(Color.GREEN);
//                    g.setLineWidth(9);
////                    g.strokePolyline(debugX.stream().mapToDouble(Double::doubleValue).toArray()
////                            , debugY.stream().mapToDouble(Double::doubleValue).toArray(), debugX.size());
//                    g.setStroke(Color.BLACK);
//                }
//                int level = boundaryShape.border.getLevel();
////                g.strokeText("L: " + level, boundaryShape.xValues[boundaryShape.xValues.length/2], boundaryShape.yValues[boundaryShape.xValues.length/2]);
//                if(boundaryShape.coorinateNeedToBeReversed){
//                    for (int i = boundaryShape.xValues.length - 1; i >= 0; i--) {
//                        totalDrawnBorder++;
////                        g.setLineWidth(1);
////                        g.setFont(new Font(g.getFont().getName(), 4));
////                        g.strokeText(Integer.toString(totalDrawnBorder),boundaryShape.xValues[i], boundaryShape.yValues[i]);
//                        xValues.add(boundaryShape.xValues[i]);
//                        yValues.add(boundaryShape.yValues[i]);
//                    }
//                }else{
//                    for (int i = 0; i < boundaryShape.xValues.length; i++) {
//                        totalDrawnBorder++;
////                        g.setLineWidth(1);
////                        g.setFont(new Font(g.getFont().getName(), 4));
////
////                        g.strokeText(Integer.toString(totalDrawnBorder),boundaryShape.xValues[i], boundaryShape.yValues[i]);
//                        xValues.add(boundaryShape.xValues[i]);
//                        yValues.add(boundaryShape.yValues[i]);
//                    }
//                }
//
//            });
//            double[] xValuesArr = xValues.stream().mapToDouble(Double::doubleValue).toArray();
//            double[] yValuesArr = yValues.stream().mapToDouble(Double::doubleValue).toArray();
////            System.out.println("Begin------------------------------------------------------");
////            System.out.println("X");
////            for (int i = 0; i < xValuesArr.length; i++) {
////                System.out.println(xValuesArr[i]);
////            }
////            System.out.println("Y");
////            for (int i = 0; i < yValuesArr.length; i++) {
////                System.out.println(yValuesArr[i]);
////            }
////            System.out.println("EnD------------------------------------------------------");
//            g.fillPolygon(xValuesArr, yValuesArr, xValuesArr.length);
//            double borderWidth = styler.getBorderWidthByLevel(border.getLevel());
//            g.setLineWidth(borderWidth);
//            g.strokePolygon(xValuesArr, yValuesArr, xValuesArr.length);
//            drawIndex++;
//        }
//        boundaryShapes.forEach(boundaryShapes1 -> {
//            List<Double> xValues = new ArrayList<>();
//            List<Double> yValues = new ArrayList<>();
//            boundaryShapes1.stream().forEach(boundaryShape -> {
//                if(boundaryShape.renderColored){
//                    List<Double> debugX = new ArrayList<>();
//                    List<Double> debugY = new ArrayList<>();
//                    for (int i = boundaryShape.xValues.length - 1; i >= 0; i--) {
//                        debugX.add(boundaryShape.xValues[i]);
//                        debugY.add(boundaryShape.yValues[i]);
//                    }
//                    g.setStroke(Color.GREEN);
//                    g.setLineWidth(9);
//                    g.strokePolyline(debugX.stream().mapToDouble(Double::doubleValue).toArray()
//                            , debugY.stream().mapToDouble(Double::doubleValue).toArray(), debugX.size());
//                    g.setStroke(Color.BLACK);
//                }
//                int level = boundaryShape.border.getLevel();
//                g.strokeText("L: " + level, boundaryShape.xValues[boundaryShape.xValues.length/2], boundaryShape.yValues[boundaryShape.xValues.length/2]);
//                if(boundaryShape.coorinateNeedToBeReversed){
//                    for (int i = boundaryShape.xValues.length - 1; i >= 0; i--) {
//                        xValues.add(boundaryShape.xValues[i]);
//                        yValues.add(boundaryShape.yValues[i]);
//                    }
//                }else{
//                    for (int i = 0; i < boundaryShape.xValues.length; i++) {
//                        xValues.add(boundaryShape.xValues[i]);
//                        yValues.add(boundaryShape.yValues[i]);
//                    }
//                }
//
//            });
//            double[] xValuesArr = xValues.stream().mapToDouble(Double::doubleValue).toArray();
//            double[] yValuesArr = yValues.stream().mapToDouble(Double::doubleValue).toArray();
////            System.out.println("Begin------------------------------------------------------");
////            System.out.println("X");
////            for (int i = 0; i < xValuesArr.length; i++) {
////                System.out.println(xValuesArr[i]);
////            }
////            System.out.println("Y");
////            for (int i = 0; i < yValuesArr.length; i++) {
////                System.out.println(yValuesArr[i]);
////            }
////            System.out.println("EnD------------------------------------------------------");
////            g.fillPolygon(xValuesArr, yValuesArr, xValuesArr.length);
//            double borderWidth = styler.getBorderWidthByLevel(border.getLevel());
//            g.setLineWidth(borderWidth);
//            g.strokePolygon(xValuesArr, yValuesArr, xValuesArr.length);
//        });


//        for (List<LeafRegion.BoundaryShape> boundaryShapeList : boundaryShapes) {
//            int subRegionIndex = 0;
//
//
//            boundaryShapeList.stream().forEach(boundaryShape -> {
//                for (int i = 0; i < boundaryShape.xValues.length - 1; i++) {
//                    xValues.add(boundaryShape.xValues[i]);
//                    yValues.add(boundaryShape.yValues[i]);
//                }
//            });
//            double[] xValuesArr = xValues.stream().mapToDouble(Double::doubleValue).toArray();
//            double[] yValuesArr = yValues.stream().mapToDouble(Double::doubleValue).toArray();
//            g.strokePolygon(xValuesArr, yValuesArr, xValuesArr.length);
////            g.fillPolygon();
////            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapeList) {
////
////                int borderLevel = border.getLevel();
////
////                boundaryShape.border.setRenderState(getNextRenderState());
////
////                drawIndex++;
////
////                g.setStroke(Color.BLACK);
//////                    if(borderLevel == 0){
//////                        g.setStroke(Color.BLACK);
//////                    }else if(borderLevel == 1){
//////                        g.setStroke(Color.BLUE);
//////                    }else if(borderLevel == 2){
//////                        g.setStroke(Color.GREEN);
//////                    }else if(borderLevel == 3){
//////                        g.setStroke(Color.YELLOW);
//////                    }else{
//////                        g.setStroke(Color.GREY);
//////                    }
////                g.setLineWidth(2);
////                if(shapeIndexToDraw == totalDrawnBorder){
////                    g.setLineWidth(4);
////                    g.setStroke(Color.GREEN);
////                }else{
////                    g.setStroke(Color.BLACK);
////                    g.setLineWidth(2);
////                }
////
////                g.strokePolyline(boundaryShape.xValues, boundaryShape.yValues, boundaryShape.xValues.length);
////                double x1 = boundaryShape.xValues[0];
////                double y1 = boundaryShape.yValues[0];
////                double x2 = boundaryShape.xValues[boundaryShape.xValues.length -1];
////                double y2 = boundaryShape.yValues[boundaryShape.xValues.length -1];
////
////                g.setLineWidth(2);
////                g.setStroke(Color.BLACK);
////
////                if(boundaryShape.renderColored){
////                    g.setStroke(boundaryShape.color);
////                    g.setLineWidth(10);
////                    g.strokeOval(x1, y1, 9, 9);
////                }else{
////                    g.setStroke(Color.BLACK);
////                }
//////                    if(x1 == 5.0 && y1 == -8.66){
//////                        g.setStroke(Color.GREEN);
//////                        g.strokeOval(x1, y1, 9, 9);
//////                        g.setStroke(Color.BLACK);
//////                    }
////
////                g.strokeLine(x1, y1, x2, y2);
////
////                g.setLineWidth(2);
////                g.setStroke(Color.BLACK);
////                double midx = boundaryShape.xValues[(boundaryShape.xValues.length -1)/2];
////                double midy = boundaryShape.yValues[(boundaryShape.xValues.length -1)/2];
////
////                g.strokeText(Integer.toString(drawIndex), midx, midy);
////                subRegionIndex++;
////            }
//
////                g.setLineWidth( 4.0/ (boundaryShape.level + 1));
////                boundaryShape.level
////                List<String> border = boundaryShapeTuple.first;
////                g.beginPath();
////                g.setFill(regionColor);
////                double lastX = 0;
////                double lastY = 0;
//
//
////                for (int i = 0; i < boundaryShape.xValues.length; i++) {
////                    double x = boundaryShape.xValues[i];
////                    double y = boundaryShape.yValues[i];
////                    if(i == 0 || i == boundaryShape.xValues.length - 1){
////                        g.setLineWidth(1);
////                        g.setFill(Color.GREEN);
////                        g.fillOval(x, y, 4, 4);
////                    }
////                    lastX = x;
////                    lastY = y;
//////                    if(i == 0){
//////                        g.moveTo(x, y);
//////                    }else{
//////                        g.lineTo(x, y);
//////                    }
////
////                    g.setFont(new Font(g.getFont().getName(), 5));
////                    g.setLineWidth(0.3);
//////                    g.strokeText(Integer.toString(boundaryShape.level), x, y);
////                    g.strokeText(Integer.toString(shapeIndex) + "/" + Integer.toString(i), x, y);
////
////                }
//
//
////                g.setLineWidth(1);
////                g.setFont(new Font(g.getFont().getName(), 10));
//////                g.strokeText(Integer.toString(shapeIndex), boundaryShape.xValues[boundaryShape.xValues.length/2], boundaryShape.yValues[boundaryShape.xValues.length/2]);
////
//////                g.strokeText(boundaryShapeTuple.first, lastX, lastY);
////                shapeIndex += 1;
////                g.closePath();
////                g.setStroke(new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
////                g.stroke();
//        }
//    }
}
