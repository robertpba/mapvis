package mapvis.graphic;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;
import mapvis.models.Region;
import mapvis.models.Tile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 10/26/2015.
 */
public class RegionRenderer {

    static final int[][] DIR_TO_POINTS = new int[][]{
            new int[]{0, 1, 2, 3},
            new int[]{10, 11, 0, 1},
            new int[]{2, 3, 4, 5},
            new int[]{8, 9, 10, 11},
            new int[]{4, 5, 6, 7},
            new int[]{6, 7, 8, 9},
    };

    private final Canvas canvas;
    private final RegionLabelRenderer regionLabelRenderer;

    private HexagonalTilingView view;
    private final double sideLength;
    private final double COS30 = Math.cos(Math.toRadians(30));
    private final double[] points;
    private final double[] x;
    private final double[] y;
    public int maxBorderLevelToShow;
    private final RegionAreaRenderer regionAreaRenderer;
    private final RegionBorderRenderer regionBorderRenderer;

    private final IBorderCoordinatesCalculator<INode> borderCoordinatesCalculator;

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        super();
        maxBorderLevelToShow = 1;
        System.out.println("Creating: " + this.getClass().getName());
        this.sideLength = view.SideLength;
        this.view = view;
        this.canvas = canvas;
        regionAreaRenderer = new RegionAreaRenderer(canvas.getGraphicsContext2D());
        regionBorderRenderer = new RegionBorderRenderer(canvas.getGraphicsContext2D());
        regionLabelRenderer = new RegionLabelRenderer(canvas.getGraphicsContext2D());
        borderCoordinatesCalculator = new BorderCoordinatesCalcImpl<>(view);
        points = new double[]{
                -sideLength / 2, -sideLength * COS30, //  0 - 1
                sideLength / 2, -sideLength * COS30,  // 5  c  2
                sideLength, 0.0,               //  4 - 3
                sideLength / 2, sideLength * COS30,
                -sideLength / 2, sideLength * COS30,
                -sideLength, 0.0
        };
        x = new double[]{
                -sideLength / 2,
                sideLength / 2,
                sideLength,
                sideLength / 2,
                -sideLength / 2,
                -sideLength
        };
        y = new double[]{
                -sideLength * COS30,
                -sideLength * COS30,
                0.0,
                sideLength * COS30,
                sideLength * COS30,
                0.0
        };
    }


    private boolean isTileVisibleOnScreen(Tile<INode> tile, Point2D topleftBorder, Point2D bottomRightBorder) {
        return tile.getX() > topleftBorder.getX()
                && tile.getX() < bottomRightBorder.getX()
                && tile.getY() > topleftBorder.getY()
                && tile.getY() < bottomRightBorder.getY();
    }


    public void drawRegionHelper(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder) {
        regionAreaRenderer.initForNextRenderingPhase();
        regionBorderRenderer.initForNextRenderingPhase();
        drawRegion(regionToDraw, topleftBorder, bottomRightBorder);
        regionAreaRenderer.finishRenderingPhase();
        regionBorderRenderer.finishRenderingPhase();
    }

    public static double roundTo4Digits(double val) {
        return Math.round(100.0 * val) / 100.0;
    }

    public static Point2D roundToCoordinatesTo4Digits(Point2D point2D) {
        return new Point2D(roundTo4Digits(point2D.getX()), roundTo4Digits(point2D.getY()));
    }

    public void drawRegion(Region regionToDraw, Point2D topleftBorder, Point2D bottomRightBorder) {
//        TileStyler<INode> styler = view.getStyler();
        GraphicsContext g = canvas.getGraphicsContext2D();

        g.save();
        borderCoordinatesCalculator.setRegion(regionToDraw);
        regionBorderRenderer.setIsSingleSideBorderRenderingEnabled(true);
//
        boolean disableOrdering = false;
        IRegionStyler<INode> regionStyler = view.getRegionStyler();
        int maxLevelToCollect = Math.max(regionStyler.getMaxBorderLevelToShow(), regionStyler.getMaxRegionLevelToShow());

        Map<Region<INode>, List<List<LeafRegion.BoundaryShape>>> regionToBoundaryShapes = borderCoordinatesCalculator.
                computeCoordinates(!disableOrdering, maxLevelToCollect);
//        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();


        double[] xValues = {
                -635.0
                , -640.0
                , -650.0
                , -655.0
                , -665.0
                , -670.0
                , -665.0
                , -670.0
                , -665.0
                , -655.0
                , -650.0
                , -640.0
                , -635.0
        };
        double[] yValues = {
                580.24
                , 588.9
                , 588.9
                , 597.56
                , 597.56
                , 588.9
                , 580.24
                , 571.58
                , 562.92
                , 562.92
                , 571.58
                , 571.58
                , 580.24
        };

//
//        graphicsContext2D.beginPath();
//        graphicsContext2D.moveTo(50, 50);
//        graphicsContext2D.bezierCurveTo(150, 20, 150, 150, 75, 150);
//        graphicsContext2D.closePath();
//        graphicsContext2D.stroke();

//        graphicsContext2D.beginPath();
//        graphicsContext2D.moveTo(xValues[0], yValues[1]);
//
//        for (int i = 1; i < (xValues.length - 2); i = i + 2) {
//            double control1X = xValues[i];
//            double control1Y = yValues[i];
//
//            double control2X = xValues[i + 1];
//            double control2Y = yValues[i + 1];
//
//            double endX = xValues[i + 2];
//            double endY = yValues[i + 2];
//            graphicsContext2D.bezierCurveTo(control1X, control1Y, control2X, control2Y, endX, endY);
//            graphicsContext2D.moveTo(endX, endY);
//        }
//
//        graphicsContext2D.closePath();
//        graphicsContext2D.stroke();

//        xValues = Input.xValues;
//        yValues = Input.yValues;
//        double[] coords = new double[xValues.length * 2];
//        for (int i = 0; i < xValues.length; i++) {
//            coords[i * 2] = xValues[i];
//            coords[i * 2 + 1] = yValues[i];
//        }
//        drawSpline(graphicsContext2D, coords, 0.8, false);
//
//        int w = 10;
//        int h = w;
//        graphicsContext2D.setFill(Color.BLUE);
//        graphicsContext2D.fillOval(50, 50, w, h);
//        graphicsContext2D.fillOval(150, 20, w, h);
//        graphicsContext2D.fillOval(150, 150, w, h);
//        graphicsContext2D.fillOval(75, 150, w, h);


//        xValues = Input.xValues;
//        yValues = Input.yValues;
//        Point2D[] points = new Point2D[xValues.length];
//        for (int i = 0; i < xValues.length; i++) {
//            points[i] = new Point2D(xValues[i], yValues[i]);
//        }
//        Simplify<Point2D> simplify = new Simplify<Point2D>(new Point2D[0], new PointExtractor<Point2D>() {
//            @Override
//            public double getX(Point2D point) {
//                return point.getX();
//            }
//
//            @Override
//            public double getY(Point2D point) {
//                return point.getY();
//            }
//        });
//        float tolerance = 13;
//        boolean qualityHigh = true;
//        Point2D[] simplyfiedPoints = simplify.simplify(points, tolerance, qualityHigh);
////        double[] newValuesX = new double[simplyfiedPoints.length];
////        double[] newValuesY = new double[simplyfiedPoints.length];
//        List<Double> xCoordsFiltered = new ArrayList<>();
//        List<Double> yCoordsFiltered = new ArrayList<>();
//
//        for (int i = 0; i < simplyfiedPoints.length; i++) {
//            if(simplyfiedPoints[i] == null)
//                continue;
//            xCoordsFiltered.add(simplyfiedPoints[i].getX());
//            yCoordsFiltered.add(simplyfiedPoints[i].getY());
//        }
//        double[] newValuesX = xCoordsFiltered.stream().mapToDouble(Double::doubleValue).toArray();
//        double[] newValuesY = yCoordsFiltered.stream().mapToDouble(Double::doubleValue).toArray();
//
//        xValues = newValuesX;
//        yValues = newValuesY;
//        double[] coords = new double[xValues.length * 2];
//        for (int i = 0; i < xValues.length; i++) {
//            coords[i * 2] = xValues[i];
//            coords[i * 2 + 1] = yValues[i];
//        }
//        drawSpline(g, coords, 0.8, false);

//        g.strokePolyline(newValuesX, newValuesY, newValuesX.length);
//
        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
            regionAreaRenderer.drawArea(regionStyler, boundaryShapeTuple.getKey(), boundaryShapes);
        }
//
//        for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
//            List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
//            Region<INode> region = boundaryShapeTuple.getKey();
//            regionBorderRenderer.drawBorder(regionStyler, boundaryShapes, view);
//        }
//
//        if(regionStyler.getShowLabels()){
//            if(maxLevelToCollect != regionStyler.getMaxLabelLevelToShow()){
//                regionToBoundaryShapes = borderCoordinatesCalculator.computeCoordinates(false, regionStyler.getMaxLabelLevelToShow());
//            }
//            for (Map.Entry<Region<INode>, List<List<LeafRegion.BoundaryShape>>> boundaryShapeTuple : regionToBoundaryShapes.entrySet()){
//                List<List<LeafRegion.BoundaryShape>> boundaryShapes = boundaryShapeTuple.getValue();
//                Region<INode> region = boundaryShapeTuple.getKey();
//                regionLabelRenderer.drawLabels(regionStyler, region, boundaryShapes);
//            }
//        }

        g.restore();
    }

    public HexagonalTilingView getView() {
        return view;
    }

    private Pair<Point2D, Point2D> getControlPoints(Point2D x0, Point2D x1, Point2D x2, double t) {
        //  x0,y0,x1,y1 are the coordinates of the end (knot) pts of this segment
        //  x2,y2 is the next knot -- not connected here but needed to calculate p2
        //  p1 is the control point calculated here, from x1 back toward x0.
        //  p2 is the next control point, calculated here and returned to become the
        //  next segment's p1.
        //  t is the 'tension' which controls how far the control points spread.

        //  Scaling factors: distances from this knot to the previous and following knots.
        double d01 = Math.sqrt(Math.pow(x1.getX() - x0.getX(), 2) + Math.pow(x1.getY() - x0.getY(), 2));
        double d12 = Math.sqrt(Math.pow(x2.getX() - x1.getX(), 2) + Math.pow(x2.getY() - x1.getY(), 2));

        double fa = t * d01 / (d01 + d12);
        double fb = t - fa;

        double p1x = x1.getX() + fa * (x0.getX() - x2.getX());
        double p1y = x1.getY() + fa * (x0.getY() - x2.getY());

        double p2x = x1.getX() - fb * (x0.getX() - x2.getX());
        double p2y = x1.getY() - fb * (x0.getY() - x2.getY());

        return new Pair(new Point2D(p1x, p1y), new Point2D(p2x, p2y));
    }

    void drawSpline(GraphicsContext ctx, double[] pts, double t, boolean closed) {
//        showDetails=document.getElementById('details').checked;
//        ctx.lineWidth=4;
//        ctx.save();
        List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...
        int n = pts.length;

//        if(closed){
//            //   Append and prepend knots and control points to close the curve
//            pts.push(pts[0],pts[1],pts[2],pts[3]);
//            pts.unshift(pts[n-1]);
//            pts.unshift(pts[n-1]);
//            for(var i=0;i<n;i+=2){
//                cp=cp.concat(getControlPoints(pts[i],pts[i+1],pts[i+2],pts[i+3],pts[i+4],pts[i+5],t));
//            }
//            cp=cp.concat(cp[0],cp[1]);
//            for(var i=2;i<n+2;i+=2){
//                var color=HSVtoRGB(Math.floor(240*(i-2)/(n-2)),0.8,0.8);
//                if(!showDetails){color="#555555"}
//                ctx.strokeStyle=hexToCanvasColor(color,0.75);
//                ctx.beginPath();
//                ctx.moveTo(pts[i],pts[i+1]);
//                ctx.bezierCurveTo(cp[2*i-2],cp[2*i-1],cp[2*i],cp[2*i+1],pts[i+2],pts[i+3]);
//                ctx.stroke();
//                ctx.closePath();
//                if(showDetails){
//                    drawControlLine(ctx,pts[i],pts[i+1],cp[2*i-2],cp[2*i-1]);
//                    drawControlLine(ctx,pts[i+2],pts[i+3],cp[2*i],cp[2*i+1]);
//                }
//            }
//        }
//        else{
        // Draw an open curve, not connected at the ends
        for (int i = 0; i < n - 4; i += 2) {
            Pair<Point2D, Point2D> controlPoints = getControlPoints(new Point2D(pts[i], pts[i + 1]), new Point2D(pts[i + 2], pts[i + 3]), new Point2D(pts[i + 4], pts[i + 5]), t);
            cp.add(controlPoints.getKey());
            cp.add(controlPoints.getValue());
        }

//        Point2D[] controlPoints = (Point2D[]) cp.toArray();
        Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

        for (int i = 2; i < pts.length - 5; i += 2) {

            ctx.beginPath();
            ctx.moveTo(pts[i], pts[i + 1]);

            Point2D controlPoint1 = controlPoints[i - 1];
            Point2D controlPoint2 = controlPoints[i];

            ctx.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), pts[i + 2], pts[i + 3]);
//            ctx.bezierCurveTo(cp[2*i-2],cp[2*i-1],cp[2*i],cp[2*i+1],pts[i+2],pts[i+3]);
            ctx.stroke();
            ctx.closePath();
//                if(showDetails){
//                    drawControlLine(ctx,pts[i],pts[i+1],cp[2*i-2],cp[2*i-1]);
//                    drawControlLine(ctx,pts[i+2],pts[i+3],cp[2*i],cp[2*i+1]);
//                }
        }
        //  For open curves the first and last arcs are simple quadratics.
//            var color=HSVtoRGB(40,0.4,0.4);  // brown
//            if(!showDetails){color="#555555"}
//            ctx.strokeStyle=hexToCanvasColor(color,0.75);
        ctx.beginPath();
        ctx.moveTo(pts[0], pts[1]);
        ctx.quadraticCurveTo(controlPoints[0].getX(), controlPoints[0].getY(), pts[2], pts[3]);
        ctx.stroke();
        ctx.closePath();

//            var color=HSVtoRGB(240,0.8,0.8); // indigo
//            if(!showDetails){color="#555555"}
//            ctx.strokeStyle=hexToCanvasColor(color,0.75);
//        ctx.beginPath();
//        ctx.moveTo(pts[n - 2], pts[n - 1]);
////        ctx.quadraticCurveTo(cp[2 * n - 10], cp[2 * n - 9], pts[n - 4], pts[n - 3]);
//        ctx.quadraticCurveTo(cp[2 * n - 10], cp[2 * n - 9], pts[n - 4], pts[n - 3]);
//        ctx.stroke();
//        ctx.closePath();
//            if(showDetails){
//                drawControlLine(ctx,pts[2],pts[3],cp[0],cp[1]);
//                drawControlLine(ctx,pts[n-4],pts[n-3],cp[2*n-10],cp[2*n-9]);
//            }
        ctx.restore();
    }



//        if(showDetails){   //   Draw the knot points.
//            for(var i=0;i<n;i+=2){
//                drawPoint(ctx,pts[i],pts[i+1],2.5,"#ffff00");
//            }
//        }
//    }

}
