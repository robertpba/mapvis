package mapvis.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Pair;
import mapvis.common.datatype.INode;
import mapvis.models.LeafRegion;
import mapvis.models.Region;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class RegionAreaRenderer {
    private final GraphicsContext graphicsContext;
    private final IRegionPathGenerator regionBoundaryPointsGenerator;
    private final BiConsumer<Point2D[], Boolean> directPolyLineRenderer;
    private final Consumer<Point2D[]> bezierCurveRenderer;
    private final IRegionPathGenerator originalBoundaryPointsGenerator;

    public RegionAreaRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
        this.regionBoundaryPointsGenerator = new SimplyfiedRegionPathGenerator(graphicsContext);
//        this.regionBoundaryPointsGenerator = new DirectRegionPathGenerator(graphicsContext);
//        this.regionBoundaryPointsGenerator = new MovingAverageRegionPathGenerator(2);
        this.originalBoundaryPointsGenerator = new DirectRegionPathGenerator(graphicsContext);


        directPolyLineRenderer = (shapePointArr, useMoveTo) -> {
            for (int i = 0; i < shapePointArr.length; i++) {
                if(i == 0 && useMoveTo){
                    graphicsContext.moveTo(shapePointArr[i].getX(), shapePointArr[i].getY());
                }else{
                    graphicsContext.lineTo(shapePointArr[i].getX(), shapePointArr[i].getY());
                }
            }
        };

        bezierCurveRenderer = (shapePointArr) -> {
            if(shapePointArr.length < 4)
                return;

            drawSpline(graphicsContext, shapePointArr, 0.2);
        };
    }


    public void drawArea(IRegionStyler<INode> regionStyler, Region<INode> regionToDraw, List<List<LeafRegion.BoundaryShape>> regionBoundaryShapes) {
        Color regionFillColor = regionStyler.getColor(regionToDraw);
        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (regionBoundaryShapes.size() == 0)
            return;


        regionBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());
        graphicsContext.beginPath();
//        graphicsContext.moveTo(0, 0);

        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {

            if (regionBoundaryShape.size() == 0)
                continue;

            List<Point2D[]> shapePoints = regionBoundaryPointsGenerator.drawRegionPaths(regionBoundaryShape);
            boolean firstDraw = true;

            
            for (Point2D[] shapePointArr : shapePoints) {
//                this.directPolyLineRenderer.accept(shapePointArr, firstDraw);
                firstDraw = false;
                this.bezierCurveRenderer.accept(shapePointArr);
            }

//            shapePoints = originalBoundaryPointsGenerator.drawRegionPaths(regionBoundaryShape);
//            for (Point2D[] shapePointArr : shapePoints) {
//                this.directPolyLineRenderer.accept(shapePointArr);
////                this.bezierCurveRenderer.accept(shapePointArr);
//            }
        }

//        graphicsContext.closePath();
//        graphicsContext.setLineCap(StrokeLineCap.ROUND);
//        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
//        graphicsContext.stroke();
//        graphicsContext.fill();
    }

    void drawSpline(GraphicsContext ctx, Point2D[] points, double t) {

        if(true){
            List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...

            int n = points.length;

            for (int i = 0; i < n - 2; i++) {
                Pair<Point2D, Point2D> controlPoints = getControlPoints(points[i], points[i + 1], points[i + 2], t);
                cp.add(controlPoints.getKey());
                cp.add(controlPoints.getValue());
            }

            Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

            ctx.beginPath();
            ctx.moveTo(points[0].getX(), points[0].getY());
            ctx.quadraticCurveTo(controlPoints[0].getX(), controlPoints[0].getY(), points[1].getX(), points[1].getY());
            ctx.stroke();

            for (int j = 1; j < n - 3; j++) {
//                ctx.beginPath();
                ctx.moveTo(points[j].getX(), points[j].getY());

                Point2D controlPoint1 = controlPoints[2 * j - 1];
                Point2D controlPoint2 = controlPoints[2 * j];

                ctx.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), points[j + 1].getX(), points[j + 1].getY());
//                ctx.stroke();
            }

//            ctx.beginPath();
            ctx.moveTo(points[n - 1].getX(), points[n - 1].getY());
            ctx.quadraticCurveTo(controlPoints[controlPoints.length - 2].getX(), controlPoints[controlPoints.length - 2].getY(), points[n - 2].getX(), points[n - 2].getY());

            ctx.stroke();
        }else {

            List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...
            int n = points.length;

            for (int i = 0; i < n - 2; i++) {
                Pair<Point2D, Point2D> controlPoints = getControlPoints(points[i], points[i + 1], points[i + 2], t);
                cp.add(controlPoints.getKey());
                cp.add(controlPoints.getValue());
            }

            Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

            for (int j = 1; j < n - 3; j++) {
                ctx.beginPath();
                ctx.moveTo(points[j].getX(), points[j].getY());

                Point2D controlPoint1 = controlPoints[2 * j - 1];
                Point2D controlPoint2 = controlPoints[2 * j];

                ctx.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), points[j + 1].getX(), points[j + 1].getY());
                ctx.stroke();
            }

            ctx.beginPath();
            ctx.moveTo(points[0].getX(), points[0].getY());
            ctx.quadraticCurveTo(controlPoints[0].getX(), controlPoints[0].getY(), points[1].getX(), points[1].getY());
            ctx.stroke();


            ctx.beginPath();
            ctx.moveTo(points[n - 1].getX(), points[n - 1].getY());
            ctx.quadraticCurveTo(controlPoints[controlPoints.length - 2].getX(), controlPoints[controlPoints.length - 2].getY(), points[n - 2].getX(), points[n - 2].getY());
            ctx.stroke();
        }
    }

//
//    void drawSpline(Point2D[] inputPoints, double t) {
//
//        List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...
//        int n = inputPoints.length;
//
//        // Draw an open curve, not connected at the ends
//        for (int i = 0; i < n - 2; i ++) {
//            Pair<Point2D, Point2D> controlPoints = getControlPoints(inputPoints[i], inputPoints[i + 1], inputPoints[i + 2], t);
//            cp.add(controlPoints.getKey());
//            cp.add(controlPoints.getValue());
//        }
//        Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);
//
//        for (int i = 1; i < n - 2; i += 1) {
//
//            graphicsContext.beginPath();
//            Point2D startPoint = inputPoints[i];
//            graphicsContext.moveTo(startPoint.getX(), startPoint.getY());
//
//            Point2D controlPoint1 = controlPoints[i - 1];
//            Point2D controlPoint2 = controlPoints[i];
//
//            Point2D endPoint = inputPoints[i + 1];
//            graphicsContext.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(), controlPoint2.getX(), controlPoint2.getY(), endPoint.getX(), endPoint.getY());
//
//            graphicsContext.stroke();
//            graphicsContext.closePath();
//        }
//
////        graphicsContext.beginPath();
////        graphicsContext.moveTo(inputPoints[0].getX(), inputPoints[0].getY());
////        graphicsContext.quadraticCurveTo(controlPoints[0].getX(), controlPoints[0].getY(), inputPoints[1].getX(), inputPoints[1].getY());
////        graphicsContext.stroke();
////        graphicsContext.closePath();
//
////        graphicsContext.restore();
//    }

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


    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }

}