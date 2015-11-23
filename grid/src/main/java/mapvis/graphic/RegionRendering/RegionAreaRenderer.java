package mapvis.graphic.RegionRendering;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import javafx.geometry.Point2D;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Pair;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.BoundaryShape;
import mapvis.models.ConfigurationConstants;
import mapvis.models.Region;

import java.awt.*;
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
    private <T> boolean sameNodeTuple(Tuple2<T, T> nodeTupleA, Tuple2<T, T> nodeTupleB){

        if(nodeTupleA.first == null && nodeTupleB.first == null && nodeTupleA.second == null && nodeTupleB.second == null){
            return true;
        }

        if(nodeTupleA.first != null && nodeTupleA.second == null){
            if(nodeTupleA.first.equals(nodeTupleB.first) && nodeTupleB.second == null)
                return true;

            if(nodeTupleA.first.equals(nodeTupleB.second) && nodeTupleB.first == null)
                return true;
            return false;
        }

        if(nodeTupleA.second != null && nodeTupleA.first == null){
            if(nodeTupleA.second.equals(nodeTupleB.second) && nodeTupleB.first == null)
                return true;

            if(nodeTupleA.second.equals(nodeTupleB.first) && nodeTupleB.second == null)
                return true;

            return false;
        }

        if(nodeTupleA.first.equals(nodeTupleB.first) && nodeTupleA.second.equals(nodeTupleB.second))
            return true;

        if(nodeTupleA.second.equals(nodeTupleB.first) && nodeTupleA.first.equals(nodeTupleB.second))
            return true;

        return false;
    }
    private class QuadraticCurveBoundaryShapeRenderer<T> implements BoundaryShapeRenderer<T>{
        @Override
        public void renderBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape, Color fillColor) {

            int prevLevel = -1;
            int prevLevelNodeA = -1;
            int prevLevelNodeB = -1;
            INode regionNodeA = null;
            INode regionNodeB = null;
            List<Point2D> currList = new ArrayList<>();
            String prevRegionSeperatingIDs = "";
            int maxToShow = Math.max(view.getMaxLevelOfRegionsToShow(), view.getMaxLevelOfBordersToShow());

            List<List<BoundaryShape<T>>> summarizeBoundaryShapes = summarizeBoundaryShape(regionBoundaryShape, currList, maxToShow);
            List<List<Point2D>> borderLevelChangeShapes = new ArrayList<>();

            for (List<BoundaryShape<T>> summarizedShape : summarizeBoundaryShapes) {
                List<Point2D> currPShape = new ArrayList<>();
                for (BoundaryShape<T> tBoundaryShape : summarizedShape) {
                    for (int i = 0; i < tBoundaryShape.getShapeLength(); i++) {
                        currPShape.add(new Point2D(tBoundaryShape.getXCoordinateAtIndex(i), tBoundaryShape.getYCoordinateAtIndex(i)));
                    }
                }
                if(currPShape.size() > 0)
                    borderLevelChangeShapes.add(currPShape);
            }

            Point2D firstPoint = null;
            Point2D currentPoint = null;
            Point2D nextPoint = null;
            boolean firstRenderPass = true;
            double currXCoord = 0;
            double currYCoord = 0;
            double nextXCoord = 0;
            double nextYCoord = 0;

            Point2D lastLineTo = null;
            Point2D prevPoint = null;
            prevLevelNodeA = -1;

            for (int i = 0; i < borderLevelChangeShapes.size(); i++) {
                List<Point2D> boundaryShape = borderLevelChangeShapes.get(i);

                double xMid = 0;
                double yMid = 0;

                for (int j = 0; j < (boundaryShape.size() - 1); j++) {
                    if (firstRenderPass) {
                        xMid = (boundaryShape.get(0).getX() + boundaryShape.get(1).getX()) / 2;
                        yMid = (boundaryShape.get(0).getY() + boundaryShape.get(1).getY()) / 2;
                        firstPoint = boundaryShape.get(0);
                        lastLineTo = firstPoint;
                        graphicsContext.moveTo(xMid, yMid);
//                        firstPoint = new Point2D(xMid, yMid);

                        firstRenderPass = false;
                    }

//                    currXCoord = boundaryShape.getXCoordinateAtIndex(j);
//                    currYCoord = boundaryShape.getYCoordinateAtIndex(j);
                    nextXCoord = boundaryShape.get(j + 1).getX();
                    nextYCoord = boundaryShape.get(j + 1).getY();

                    xMid = (boundaryShape.get(j).getX() + boundaryShape.get(j + 1).getX()) / 2;
                    yMid = (boundaryShape.get(j).getY() + boundaryShape.get(j + 1).getY()) / 2;
                    if(j == 0){
                        graphicsContext.lineTo(boundaryShape.get(j).getX(), boundaryShape.get(j).getY());
                        graphicsContext.lineTo(xMid, yMid);
                    }else{
                        graphicsContext.quadraticCurveTo(boundaryShape.get(j).getX(), boundaryShape.get(j).getY(), xMid, yMid);
                    }

                }
                graphicsContext.setStroke(Color.RED);
                graphicsContext.strokeLine(boundaryShape.get(0).getX(), boundaryShape.get(0).getY(),
                        boundaryShape.get(boundaryShape.size() - 1).getX(), boundaryShape.get(boundaryShape.size() - 1).getY());

//                xMid = (xMid + nextXCoord)/2;
//                yMid = (yMid + nextYCoord)/2;
//                graphicsContext.quadraticCurveTo(xMid, yMid, nextXCoord, nextYCoord);

//                xMid = (xMid + nextPoint.getX())/2;
//                yMid = (yMid + nextPoint.getY())/2;
//                graphicsContext.quadraticCurveTo(xMid, yMid, nextPoint.getX(), nextPoint.getY());
//                graphicsContext.setFill(Color.RED);
//                graphicsContext.fillOval(xMid, yMid, 4, 4);
//
//                graphicsContext.setFill(Color.YELLOW);
//                graphicsContext.fillOval(nextPoint.getX(), nextPoint.getY(), 4, 4);
            }

//            graphicsContext.quadraticCurveTo(nextXCoord, nextYCoord, firstPoint.getX(), firstPoint.getY());
            if(!firstRenderPass)
                graphicsContext.lineTo(nextXCoord, nextYCoord);
        }

        private List<List<BoundaryShape<T>>> summarizeBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape, List<Point2D> currList, int maxToShow) {
            List<List<BoundaryShape<T>>> summarizedBoundaryShapes = new ArrayList<>();
            List<BoundaryShape<T>> currentConcatenatedBoundaryShape = new ArrayList<>();

            List<List<Point2D>> borderLevelChangeShapes = new ArrayList<>();
            INode regionNodeA;
            INode regionNodeB;
            Tuple2<T, T> nodeTuple = null;
            for (BoundaryShape<T> tBoundaryShape : regionBoundaryShape) {
                if ( (nodeTuple != null) && (!sameNodeTuple(nodeTuple, tBoundaryShape.getSeperatedRegionsID(maxToShow, (Tree2<T>) view.getTree()))) ) {
                    summarizedBoundaryShapes.add(currentConcatenatedBoundaryShape);
                    currentConcatenatedBoundaryShape = new ArrayList<>();

                    currentConcatenatedBoundaryShape.add(tBoundaryShape);
                }else {
                    currentConcatenatedBoundaryShape.add(tBoundaryShape);
                }
                nodeTuple = tBoundaryShape.getSeperatedRegionsID(maxToShow, (Tree2<T>) view.getTree());

                if(nodeTuple != null){
                    regionNodeA = (INode) nodeTuple.first;
                    regionNodeB = (INode) nodeTuple.second;
                    String id = (regionNodeA != null ? regionNodeA.getId() : "-1") + "/" + (regionNodeB != null ? regionNodeB.getId() : "-1") ;
                    graphicsContext.strokeText(
                            id,
                            (tBoundaryShape.getXCoordinateStartpoint() + tBoundaryShape.getXCoordinateEndpoint()) / 2,
                            (tBoundaryShape.getYCoordinateStartpoint() + tBoundaryShape.getYCoordinateEndpoint()) / 2
                    );
                }


            }
            if(currentConcatenatedBoundaryShape.size() > 0){
                summarizedBoundaryShapes.add(currentConcatenatedBoundaryShape);
            }

            return summarizedBoundaryShapes;
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