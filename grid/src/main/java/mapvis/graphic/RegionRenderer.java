package mapvis.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
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
        boolean disableOrdering = false;
        Map<Region<INode>, List<List<LeafRegion.BoundaryShape>>> regionToBoundaryShapes = borderCoordinatesCalculatorImpl.
                computeCoordinates(maxBorderLevelToShow, !disableOrdering);
        regionToBoundaryShapes = borderCoordinatesCalculatorImpl.getRegionToBoundaries();


        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){

//            totalDrawnBorder++;
            drawIndex++;
//            if(drawIndex >= shapeIndexToDraw)
//                continue;
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
            if(disableOrdering){
                drawPolylines(g, boundaryShapeTuple);
            }else {
                fillPolygon(g, boundaryShapeTuple, boundaryShapes);
            }
        }
        g.setFill(Color.GREEN);

        g.restore();
    }

    private void drawPolylines(GraphicsContext g, Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple) {
        int i = 0;
        for (List<LeafRegion.BoundaryShape> boundaryShapes : boundaryShapeTuple.getValue()) {
            for (LeafRegion.BoundaryShape boundaryShape : boundaryShapes) {
//                Node nodeA = (Node) boundaryShape.border.getNodeA();
//                Node nodeB = (Node) boundaryShape.border.getNodeB();
////                if( !(nodeA != null && (nodeA.getId().equals("4") || nodeA.getId().equals("5")) /*&& (nodeB != null && (nodeB.getId().equals("4") || nodeB.getId().equals("5") ))*/) )
//                    continue;
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
        g.setFillRule(FillRule.EVEN_ODD);
//        int drawIndex = 0;

//        regionBoundaryShapes.sort((o1, o2) -> o1.size() -o2.size() );

        if(regionBoundaryShapes.size() == 0)
            return;

        g.beginPath();
        totalDrawnBorder++;
        regionBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());
        System.out.println("Shape Start");
        drawIndex = 0;
        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {
//            if(drawIndex != shapeIndexToDraw)
//                continue;
            if(regionBoundaryShape.size() == 0)
                continue;

            drawIndex++;
            List<Double> xValues = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();
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
                    }
                }else{
                    for (int i = 0; i < partialRegionBoundary.xValues.length; i++) {
                        if(firstDraw){
                            g.moveTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                            firstDraw = false;
                        }else {
                            g.lineTo(partialRegionBoundary.xValues[i], partialRegionBoundary.yValues[i]);
                        }
//                        totalDrawnBorder++;
                    }
                }
            }
            String startText = "Start";
            String endText = "End";

//            printCoordinates(xValues, yValues, startText, endText);
        }


        g.closePath();
        g.stroke();
        g.fill();
        System.out.println("Shape End");
    }

    public static void printCoordinates(List<Double> xValues, List<Double> yValues, String startText, String endText) {
        System.out.println(startText);
        System.out.println("XVal");
        for (Double xValue : xValues) {
            System.out.println(xValue);
        }
        System.out.println("YVal");
        for (Double xValue : yValues) {
            System.out.println(xValue);
        }
        System.out.println(endText);
    }
}
