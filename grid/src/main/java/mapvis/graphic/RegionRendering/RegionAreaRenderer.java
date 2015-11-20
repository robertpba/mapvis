package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Pair;
import mapvis.common.datatype.INode;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.BoundaryShape;
import mapvis.models.ConfigurationConstants;
import mapvis.models.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RegionAreaRenderer {

    private final GraphicsContext graphicsContext;
    private final IRegionPathGenerator regionBoundaryPointsGenerator;

    private final IRegionPathGenerator originalBorderPointsGenerator;
    private final HexagonalTilingView view;
    private BoundaryShapeRenderer<INode> shapeRenderer;

    private interface BoundaryShapeRenderer<T>{
        void renderBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape, Color fillColor);
    }

    private class QuadraticCurveBoundaryShapeRenderer<T> implements BoundaryShapeRenderer<T>{
        @Override
        public void renderBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape, Color fillColor) {
            Point2D firstPoint = null;
            Point2D currentPoint = null;
            Point2D nextPoint = null;
            boolean firstRenderPass = true;
            double currXCoord = 0;
            double currYCoord = 0;
            double nextXCoord = 0;
            double nextYCoord = 0;

            for (int i = 0; i < regionBoundaryShape.size(); i++) {
                BoundaryShape boundaryShape = regionBoundaryShape.get(i);
                if(boundaryShape.getShapeLength() == 2){
//                    graphicsContext.moveTo(boundaryShape.getXCoordinateAtIndex(0), boundaryShape.getYCoordinateAtIndex(0));
//                    graphicsContext.lineTo(boundaryShape.getXCoordinateAtIndex(1), boundaryShape.getYCoordinateAtIndex(1));
                    continue;
                }

                double xMid = 0;
                double yMid = 0;

                for (int j = 0; j < boundaryShape.getShapeLength() - 2; j++) {
                    if(firstRenderPass){
                        xMid = (boundaryShape.getXCoordinateAtIndex(0) + boundaryShape.getXCoordinateAtIndex(1)) / 2;
                        yMid = (boundaryShape.getYCoordinateAtIndex(0) + boundaryShape.getYCoordinateAtIndex(1)) / 2;

                        graphicsContext.moveTo(xMid, yMid);
                        firstPoint = new Point2D(xMid, yMid);
                        firstRenderPass = false;
                    }

                    currXCoord = boundaryShape.getXCoordinateAtIndex(j);
                    currYCoord = boundaryShape.getYCoordinateAtIndex(j);
                    nextXCoord = boundaryShape.getXCoordinateAtIndex(j + 1);
                    nextYCoord = boundaryShape.getYCoordinateAtIndex(j + 1);

                    xMid = (boundaryShape.getXCoordinateAtIndex(j) + boundaryShape.getXCoordinateAtIndex(j + 1)) / 2;
                    yMid = (boundaryShape.getYCoordinateAtIndex(j) + boundaryShape.getYCoordinateAtIndex(j + 1)) / 2;

                    graphicsContext.quadraticCurveTo(boundaryShape.getXCoordinateAtIndex(j), boundaryShape.getYCoordinateAtIndex(j), xMid, yMid);
                }
                xMid = (xMid + nextXCoord)/2;
                yMid = (yMid + nextYCoord)/2;
                graphicsContext.quadraticCurveTo(xMid, yMid, nextXCoord, nextYCoord);

//                xMid = (xMid + nextPoint.getX())/2;
//                yMid = (yMid + nextPoint.getY())/2;
//                graphicsContext.quadraticCurveTo(xMid, yMid, nextPoint.getX(), nextPoint.getY());
//                graphicsContext.setFill(Color.RED);
//                graphicsContext.fillOval(xMid, yMid, 4, 4);
//
//                graphicsContext.setFill(Color.YELLOW);
//                graphicsContext.fillOval(nextPoint.getX(), nextPoint.getY(), 4, 4);
            }

            graphicsContext.quadraticCurveTo(nextXCoord, nextYCoord, firstPoint.getX(), firstPoint.getY());
        }
    }

    private class DirectPolylineBoundaryShapeRenderer<T> implements BoundaryShapeRenderer<T>{
        @Override
        public void renderBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape, Color fillColor) {
            boolean firstRenderPass = true;
            for (BoundaryShape<T> boundaryShape : regionBoundaryShape) {
                for (int i = 0; i < boundaryShape.getXCoords().length; i++) {
                    if (firstRenderPass) {
                        graphicsContext.moveTo(boundaryShape.getXCoordinateAtIndex(i), boundaryShape.getYCoordinateAtIndex(i));
                        firstRenderPass = false;
                    } else {
                        graphicsContext.lineTo(boundaryShape.getXCoordinateAtIndex(i), boundaryShape.getYCoordinateAtIndex(i));
                    }
                }
            }
        }
    }

    private class BezierCurveBoundaryShapeRenderer<T> implements BoundaryShapeRenderer<T>{

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

        private void drawSpline(GraphicsContext graphicsContext, List<BoundaryShape<T>> boundaryShapesOfRegion, Color fillColor, float bezierCurveSmoothness) {
            List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...

            List<Point2D> inputPointList = new ArrayList<>();
            BoundaryShape lastBoundaryShape = boundaryShapesOfRegion.get(boundaryShapesOfRegion.size() - 1);
            Point2D lastPoint = new Point2D(lastBoundaryShape.getXCoordinateEndpoint(), lastBoundaryShape.getYCoordinateEndpoint());
            inputPointList.add(lastPoint);

            for (int i = 0; i < boundaryShapesOfRegion.size(); i++) {
                BoundaryShape boundaryStep = boundaryShapesOfRegion.get(i);
                for (int j = 0; j < boundaryStep.getShapeLength(); j++) {
                    inputPointList.add(new Point2D(boundaryStep.getXCoordinateAtIndex(j), boundaryStep.getYCoordinateAtIndex(j)));
                }
            }

            Point2D firstPoint = inputPointList.get(0);
            inputPointList.add(firstPoint);

            Point2D[] points = inputPointList.toArray(new Point2D[inputPointList.size()]);
            int n = points.length;

            for (int i = 0; i < n - 2; i++) {
                Pair<Point2D, Point2D> controlPoints = getControlPoints(points[i], points[i + 1], points[i + 2], bezierCurveSmoothness);
                cp.add(controlPoints.getKey());
                cp.add(controlPoints.getValue());
            }
            cp.add(cp.get(cp.size() - 2));
            cp.add(cp.get(cp.size() - 1));

            Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

            for (int j = 1; j < n - 1; j++) {
                if(j == 1){
                    graphicsContext.moveTo(points[j].getX(), points[j].getY());
                }
                Point2D controlPoint1 = controlPoints[2 * j - 1];
                Point2D controlPoint2 = controlPoints[2 * j];

                graphicsContext.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(),
                        controlPoint2.getX(), controlPoint2.getY(),
                        points[j + 1].getX(), points[j + 1].getY());
            }
        }
        @Override
        public void renderBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape, Color fillColor) {
            drawSpline(graphicsContext, regionBoundaryShape, fillColor, ConfigurationConstants.BEZIER_CURVE_SMOOTHNESS);
        }
    }

    public RegionAreaRenderer(GraphicsContext graphicsContext, HexagonalTilingView view) {
        this.graphicsContext = graphicsContext;

//        this.regionBoundaryPointsGenerator = new SimplifiedRegionPathGenerator(graphicsContext,
//                ConfigurationConstants.SIMPLIFICATION_TOLERANCE, ConfigurationConstants.USE_HIGH_QUALITY_SIMPLIFICATION);
//        this.regionBoundaryPointsGenerator = new MovingAverageRegionPathGenerator(2);
        this.regionBoundaryPointsGenerator = new DirectRegionPathGenerator(graphicsContext);

        this.originalBorderPointsGenerator = new DirectRegionPathGenerator(graphicsContext);

        switch (ConfigurationConstants.RENDERING_METHOD){
            case Bezier:
                this.shapeRenderer = new BezierCurveBoundaryShapeRenderer<>();
                break;
            case Quadric:
                this.shapeRenderer = new QuadraticCurveBoundaryShapeRenderer<>();
                break;
            case Direct:
                this.shapeRenderer = new DirectPolylineBoundaryShapeRenderer<>();
                break;
        }

        this.view = view;
    }


    public void drawArea(final IRegionStyler<INode> regionStyler, final Region<INode> regionToDraw, final List<List<BoundaryShape<INode>>> regionBoundaryShapes) {
        Color regionFillColor = regionStyler.getColor(regionToDraw);
        graphicsContext.setFill(regionFillColor);
        graphicsContext.setFillRule(FillRule.EVEN_ODD);

        if (regionBoundaryShapes.size() == 0)
            return;

        regionBoundaryShapes.sort((o1, o2) -> o2.size() - o1.size());
        graphicsContext.beginPath();

        for (List<BoundaryShape<INode>> regionBoundaryShape : regionBoundaryShapes) {

            if (regionBoundaryShape.size() == 0)
                continue;

            shapeRenderer.renderBoundaryShape(regionBoundaryShape, regionFillColor);
        }


        graphicsContext.setLineCap(StrokeLineCap.ROUND);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        if(ConfigurationConstants.DRAW_ORIGINAL_SHAPE){
            this.graphicsContext.setStroke(Color.RED);
        }else{
            this.graphicsContext.setStroke(Color.BLACK);
        }


        graphicsContext.stroke();
        if(ConfigurationConstants.FILL_SHAPE){
            graphicsContext.fill();
        }

        if(!ConfigurationConstants.DRAW_ORIGINAL_SHAPE)
            return;

        graphicsContext.beginPath();
        for (List<BoundaryShape<INode>> regionBoundaryShape : regionBoundaryShapes) {

            if (regionBoundaryShape.size() == 0)
                continue;

            List<Point2D[]> originalShapePoints = originalBorderPointsGenerator.generatePathForBoundaryShape(regionBoundaryShape);
//            this.bezierCurveRenderer.accept(shapePoints);
            new DirectPolylineBoundaryShapeRenderer<INode>().renderBoundaryShape(regionBoundaryShape, regionFillColor);
            this.graphicsContext.setStroke(Color.BLACK);
        }

        graphicsContext.setLineCap(StrokeLineCap.ROUND);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        graphicsContext.stroke();
//        graphicsContext.fill();
    }

    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }

}