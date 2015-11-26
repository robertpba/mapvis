package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.util.Pair;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dacc on 10/26/2015.
 */
public class RegionRenderer implements ITreeVisualizationRenderer {

    private final Canvas canvas;
    private final RegionLabelRenderer regionLabelRenderer;
    private final HexagonalTilingView view;
    private final RegionAreaRenderer regionAreaRenderer;
    private final RegionBorderRenderer regionBorderRenderer;
    private AbstractRegionPathGenerator<INode> boundarySimplificationAlgorithm;
    private Region regionToDraw;

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        System.out.println("Creating: " + this.getClass().getName());

        this.view = view;
        this.canvas = canvas;
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        this.regionAreaRenderer = new RegionAreaRenderer(graphicsContext2D, view);
        this.regionBorderRenderer = new RegionBorderRenderer(graphicsContext2D);
        this.regionLabelRenderer = new RegionLabelRenderer(graphicsContext2D);
        this.regionBorderRenderer.setIsSingleSideBorderRenderingEnabled(true);
        setRenderingMethod(ConfigurationConstants.RENDERING_METHOD_DEFAULT);
        setBoundarySimplificationMethod(ConfigurationConstants.SimplificationMethod.None);
    }


    private boolean isTileVisibleOnScreen(Tile<INode> tile, Point2D topleftBorder, Point2D bottomRightBorder) {
        return tile.getX() > topleftBorder.getX()
                && tile.getX() < bottomRightBorder.getX()
                && tile.getY() > topleftBorder.getY()
                && tile.getY() < bottomRightBorder.getY();
    }


    @Override
    public void renderScene(final Point2D topleftBorder, final Point2D bottomRightBorder) {
        regionAreaRenderer.initForNextRenderingPhase();
        regionBorderRenderer.initForNextRenderingPhase();
        drawRegion(topleftBorder, bottomRightBorder);
        regionAreaRenderer.finishRenderingPhase();
        regionBorderRenderer.finishRenderingPhase();
    }

    @Override
    public void configure(Object input) {
        if(input == null)
            return;

        if(input instanceof Region){
            this.setRootRegion((Region<INode>) input);
        }
    }

    public void drawRegion(final Point2D topleftBorder, final Point2D bottomRightBorder) {

        if(regionToDraw == null)
            return;
        GraphicsContext g = canvas.getGraphicsContext2D();
//        Path path = new Path();
//
//        MoveTo moveTo = new MoveTo();
//        moveTo.setX(100.0f);
//        moveTo.setY(100.0f);
//
//        HLineTo hLineTo = new HLineTo();
//        hLineTo.setX(300.0f);
//
//        VLineTo vLineTo = new VLineTo();
//        vLineTo.setY(300.0f);
//
//
//        path.getElements().addAll(moveTo, hLineTo, vLineTo);
//        path.setStroke(Color.RED);
//
//
//
//        Path path2 = new Path();
//
//        MoveTo moveTo2 = new MoveTo();
//        moveTo2.setX(300.0f);
//        moveTo2.setY(300.0f);
//
//        HLineTo hLineTo3 = new HLineTo();
//        hLineTo3.setX(100.0f);
//
//        VLineTo hLineTo2 = new VLineTo();
//        hLineTo2.setY(100.0f);
//
//        path2.getElements().addAll(moveTo2, hLineTo3, hLineTo2);
//        path2.setStroke(Color.YELLOW);
//
//        Path path3 = new Path();
//        path3.getElements().addAll(path.getElements());
//        path3.getElements().addAll(path2.getElements());
//        path3.setStroke(Color.TRANSPARENT);
//        path3.setFill(Color.GREEN);
//
//        this.view.getChildren().addAll(/*path, path2, */path3);
//        if(true)
//            return;

        g.save();


        IRegionStyler<INode> regionStyler = view.getRegionStyler();
        int maxChildrenToCollect =
                Math.max(regionStyler.getMaxBorderLevelToShow(),
                regionStyler.getMaxRegionLevelToShow());


        List<Region<INode>> childRegionsAtLevel = regionToDraw.getChildRegionsAtLevel(maxChildrenToCollect);

        List<Tuple2<Region<INode>, List<AbstractRegionPathGenerator.SortedBounaryShapes<INode>>>> regionToSimplifiedBorders = new ArrayList<>();
        for (Region<INode> region : childRegionsAtLevel) {
            List<List<IBoundaryShape<INode>>> boundaryShape = region.getBoundaryShape();
            List<AbstractRegionPathGenerator.SortedBounaryShapes<INode>> simplifiedBorders = new ArrayList<>();

            for (List<IBoundaryShape<INode>> iBoundaryShapes : boundaryShape) {
                AbstractRegionPathGenerator.SortedBounaryShapes<INode> simplifiedBoundaryShape = boundarySimplificationAlgorithm.
                        generatePathForBoundaryShape(iBoundaryShapes, maxChildrenToCollect, view.getTree());
                simplifiedBorders.add(simplifiedBoundaryShape);
            }
            regionToSimplifiedBorders.add(new Tuple2<>(region, simplifiedBorders));
        }

//        for (Tuple2<Region<INode>, List<AbstractRegionPathGenerator.SortedBounaryShapes<INode>>> region : regionToSimplifiedBorders) {
//            regionAreaRenderer.drawArea(regionStyler, region.first, region.second);
//        }

        for (Tuple2<Region<INode>, List<AbstractRegionPathGenerator.SortedBounaryShapes<INode>>> region : regionToSimplifiedBorders) {
            regionBorderRenderer.drawBorder(regionStyler, region.second);
        }


        if(regionStyler.getShowLabels()){
            List<Region<INode>> labelRegions = null;
            if(regionStyler.getMaxLabelLevelToShow() == maxChildrenToCollect){
                labelRegions = childRegionsAtLevel;
            }else{
                labelRegions = regionToDraw.getChildRegionsAtLevel(regionStyler.getMaxLabelLevelToShow());
            }

            for (Region<INode> region : labelRegions) {
                List<List<IBoundaryShape<INode>>> boundaryShape = region.getBoundaryShape();
                regionLabelRenderer.drawLabels(regionStyler, region, boundaryShape);
            }
        }

        g.restore();
    }

    public void setRootRegion(Region<INode> rootRegion) {
        this.regionToDraw = rootRegion;
    }

    public void setRenderingMethod(ConfigurationConstants.RenderingMethod renderingMethod) {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        BoundaryShapeRenderer<INode> shapeRenderer = null;
        switch (renderingMethod){
            case Bezier:
                shapeRenderer = new BezierCurveBoundaryShapeRenderer<>(graphicsContext2D);
                break;
            case Quadric:
                shapeRenderer = new QuadraticCurveBoundaryShapeRenderer<>(graphicsContext2D);
                break;
            case Direct:
                shapeRenderer = new DirectPolylineBoundaryShapeRenderer<>(graphicsContext2D);
                break;
        }
        this.regionAreaRenderer.setShapeRenderer(shapeRenderer);
        this.regionBorderRenderer.setShapeRenderer(shapeRenderer);
    }


    public void setBoundarySimplificationMethod(ConfigurationConstants.SimplificationMethod boundarySimplificationMethod) {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        switch (boundarySimplificationMethod) {
            case DouglasPeucker:
                this.boundarySimplificationAlgorithm =
                        new SimplifiedRegionPathGenerator<>(graphicsContext2D,
                                ConfigurationConstants.SIMPLIFICATION_TOLERANCE, ConfigurationConstants.USE_HIGH_QUALITY_SIMPLIFICATION);
                break;
            case Average:
                this.boundarySimplificationAlgorithm =
                        new MovingAverageRegionPathGenerator<>(graphicsContext2D);
                break;
            case None:
                this.boundarySimplificationAlgorithm =
                        new DirectRegionPathGenerator<>(graphicsContext2D);
                break;
        }
    }

    public void setBoundarySimplificationAlgorithmSettings(float simplificationTolerance, boolean useHighQualityDouglasPeucker){
        if(this.boundarySimplificationAlgorithm instanceof SimplifiedRegionPathGenerator){
            ((SimplifiedRegionPathGenerator) boundarySimplificationAlgorithm).setSettings(simplificationTolerance, useHighQualityDouglasPeucker);
        }
    }

    protected abstract class BoundaryShapeRenderer<T>{
        protected GraphicsContext graphicsContext;

        public BoundaryShapeRenderer(GraphicsContext graphicsContext) {
            this.graphicsContext = graphicsContext;
        }

        abstract void renderBoundaryShape(AbstractRegionPathGenerator.SortedBounaryShapes<T> regionIBoundaryShape);
        abstract void renderBoundaryShape(IBoundaryShape<T> regionIBoundaryShape);
    }

    private class QuadraticCurveBoundaryShapeRenderer<T> extends BoundaryShapeRenderer<T>{

        public QuadraticCurveBoundaryShapeRenderer(GraphicsContext graphicsContext) {
            super(graphicsContext);
        }

        @Override
        public void renderBoundaryShape(AbstractRegionPathGenerator.SortedBounaryShapes<T> regionBoundaryShape) {
            boolean firstRenderPass = true;
            double currXCoord = 0;
            double currYCoord = 0;
            double nextXCoord = 0;
            double nextYCoord = 0;


            for (Tuple2<IBoundaryShape<T>, Boolean> tIBoundaryShape : regionBoundaryShape.boundaryShapeAndOrdering) {
                boolean newSummarizedShape = true;
                tIBoundaryShape.first.setCoordinatesNeedToBeReversed(tIBoundaryShape.second);
                Point2D endPoint = renderBoundaryShapeAsLoop(tIBoundaryShape.first, firstRenderPass);
                nextXCoord = endPoint.getX();
                nextYCoord = endPoint.getY();
                firstRenderPass = false;
//                for (int i = 0; i < tIBoundaryShape.getShapeLength() - 1; i++) {
//                    double xMid = 0;
//                    double yMid = 0;
//                    if (firstRenderPass) {
//                        xMid = (tIBoundaryShape.getXCoordinateAtIndex(0) + tIBoundaryShape.getXCoordinateAtIndex(1)) / 2;
//                        yMid = (tIBoundaryShape.getYCoordinateAtIndex(0) + tIBoundaryShape.getYCoordinateAtIndex(1)) / 2;
//
//                        graphicsContext.moveTo(xMid, yMid);
//
//                        firstRenderPass = false;
//                    }
//                    currXCoord = tIBoundaryShape.getXCoordinateAtIndex(i);
//                    currYCoord = tIBoundaryShape.getYCoordinateAtIndex(i);
//
//                    nextXCoord = tIBoundaryShape.getXCoordinateAtIndex(i + 1);
//                    nextYCoord = tIBoundaryShape.getYCoordinateAtIndex(i + 1);
//
//                    xMid = (currXCoord + nextXCoord) / 2;
//                    yMid = (currYCoord + nextYCoord) / 2;
////                    if (newSummarizedShape) {
////                        graphicsContext.lineTo(currXCoord, currYCoord);
////                        graphicsContext.lineTo(xMid, yMid);
////                        newSummarizedShape = false;
////                    } else {
////                        graphicsContext.quadraticCurveTo(currXCoord, currYCoord, xMid, yMid);
////                    }
//                    graphicsContext.quadraticCurveTo(currXCoord, currYCoord, xMid, yMid);
//                }
            }
//            graphicsContext.lineTo(nextXCoord, nextYCoord);
//            graphicsContext.save();
//            graphicsContext.setStroke(Color.RED);
//            graphicsContext.setLineWidth(5);
//            graphicsContext.strokeLine(currXCoord, currYCoord, nextXCoord, nextYCoord);
//            graphicsContext.restore();
//
            if(!firstRenderPass)
                graphicsContext.lineTo(nextXCoord, nextYCoord);
        }


        Point2D renderBoundaryShapeAsLoop(IBoundaryShape<T> regionIBoundaryShape, boolean moveToRequired) {
            boolean newSummarizedShape = true;

            Iterator<Point2D> currCoordinateIterator = regionIBoundaryShape.iterator();
            Iterator<Point2D> nextCoordinateIterator = regionIBoundaryShape.iterator();
            Point2D nextCoordinate = nextCoordinateIterator.next();

            while (nextCoordinateIterator.hasNext()){

                Point2D currCoordinate = currCoordinateIterator.next();
                nextCoordinate = nextCoordinateIterator.next();

                Point2D midPoint = currCoordinate.add(nextCoordinate).multiply(0.5);;

                if (moveToRequired) {
                    graphicsContext.moveTo(midPoint.getX(), midPoint.getY());
                    moveToRequired = false;
                }
                if(newSummarizedShape){
                    graphicsContext.lineTo(currCoordinate.getX(), currCoordinate.getY());
                    graphicsContext.lineTo(midPoint.getX(), midPoint.getY());
                    newSummarizedShape = false;
                }else{
                    graphicsContext.quadraticCurveTo(currCoordinate.getX(), currCoordinate.getY(),
                            midPoint.getX(), midPoint.getY());
                }

            }

            return nextCoordinate;
        }


        @Override
        void renderBoundaryShape(IBoundaryShape<T> regionIBoundaryShape) {
            if(regionIBoundaryShape.getShapeLength() == 0)
                return;

            Point2D lastLine = renderBoundaryShapeAsLoop(regionIBoundaryShape, true);
            graphicsContext.lineTo(lastLine.getX(), lastLine.getY());
        }
    }

    private class DirectPolylineBoundaryShapeRenderer<T> extends BoundaryShapeRenderer<T>{

        public DirectPolylineBoundaryShapeRenderer(GraphicsContext graphicsContext) {
            super(graphicsContext);
        }

        @Override
        public void renderBoundaryShape(AbstractRegionPathGenerator.SortedBounaryShapes<T> regionBoundaryShape) {
            if(regionBoundaryShape.boundaryShapeAndOrdering.size() == 0)
                return;

            boolean firstRenderPass = true;
            for (Tuple2<IBoundaryShape<T>, Boolean> boundaryShape : regionBoundaryShape.boundaryShapeAndOrdering) {
                boundaryShape.first.setCoordinatesNeedToBeReversed(boundaryShape.second);
                renderBoundaryShapeAsLoop(boundaryShape.first, firstRenderPass);
                firstRenderPass = false;
//                for (int i = 0; i < boundaryShape.getShapeLength(); i++) {
//                    if (firstRenderPass) {
//                        graphicsContext.moveTo(boundaryShape.getXCoordinateAtIndex(i), boundaryShape.getYCoordinateAtIndex(i));
//                        firstRenderPass = false;
//                    } else {
//                        graphicsContext.lineTo(boundaryShape.getXCoordinateAtIndex(i), boundaryShape.getYCoordinateAtIndex(i));
//                    }
//                }
//                graphicsContext.setStroke(Color.RED);
//                graphicsContext.strokeLine(boundaryShape.getXCoordinateStartpoint(), boundaryShape.getYCoordinateStartpoint(),
//                    boundaryShape.getXCoordinateEndpoint(), boundaryShape.getYCoordinateEndpoint());
            }
        }

        void renderBoundaryShapeAsLoop(IBoundaryShape<T> boundaryShape, boolean moveToRequired){
            for (Point2D borderCoordinate: boundaryShape) {
                if (moveToRequired) {
                    graphicsContext.moveTo(borderCoordinate.getX(), borderCoordinate.getY());
                    moveToRequired = false;
                } else {
                    graphicsContext.lineTo(borderCoordinate.getX(), borderCoordinate.getY());
                }
            }
        }

        @Override
        void renderBoundaryShape(IBoundaryShape<T> regionIBoundaryShape) {
            renderBoundaryShapeAsLoop(regionIBoundaryShape, true);
        }
    }

    private class BezierCurveBoundaryShapeRenderer<T> extends BoundaryShapeRenderer<T>{

        public BezierCurveBoundaryShapeRenderer(GraphicsContext graphicsContext) {
            super(graphicsContext);
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

        @Override
        public void renderBoundaryShape(AbstractRegionPathGenerator.SortedBounaryShapes<T> sortedBounaryShapes) {
            List<Tuple2<IBoundaryShape<T>, Boolean>> boundaryShapeAndOrdering = sortedBounaryShapes.boundaryShapeAndOrdering;
            if(boundaryShapeAndOrdering.size() == 0)
                return;

            List<Point2D> inputPointList = new ArrayList<>();
            Tuple2<IBoundaryShape<T>, Boolean> lastIBoundaryShapeTuple = boundaryShapeAndOrdering.get(boundaryShapeAndOrdering.size() - 1);
            IBoundaryShape<T> lastBoundaryShape = lastIBoundaryShapeTuple.first;
            lastBoundaryShape.setCoordinatesNeedToBeReversed(lastIBoundaryShapeTuple.second);
            Point2D lastPoint = new Point2D(lastBoundaryShape.getXCoordinateEndpoint(), lastBoundaryShape.getYCoordinateEndpoint());
            inputPointList.add(lastPoint);

            for (int i = 0; i < boundaryShapeAndOrdering.size(); i++) {
                Tuple2<IBoundaryShape<T>, Boolean> boundaryStepTuple = boundaryShapeAndOrdering.get(i);
                IBoundaryShape<T> boundaryStep = boundaryStepTuple.first;
                boundaryStep.setCoordinatesNeedToBeReversed(boundaryStepTuple.second);
                inputPointList.addAll(boundaryStep.getCoordinates());

//                boundaryStep.forEach(point2D -> inputPointList.add(point2D));
            }

            Point2D firstPoint = inputPointList.get(0);
            inputPointList.add(firstPoint);

            List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...

            Point2D[] points = inputPointList.toArray(new Point2D[inputPointList.size()]);
            int n = points.length;

            for (int i = 0; i < n - 2; i++) {
                Pair<Point2D, Point2D> controlPoints = getControlPoints(points[i], points[i + 1], points[i + 2],
                        ConfigurationConstants.BEZIER_CURVE_SMOOTHNESS);
                cp.add(controlPoints.getKey());
                cp.add(controlPoints.getValue());
            }
            cp.add(cp.get(cp.size() - 2));
            cp.add(cp.get(cp.size() - 1));

            Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

            renderBezierCurve(points, controlPoints);
        }

        void renderBezierCurve(Point2D[] points, Point2D[] controlPoints){
            int n = points.length;

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
        void renderBoundaryShape(IBoundaryShape<T> regionIBoundaryShape) {
            if(regionIBoundaryShape.getShapeLength() == 0)
                return;

            if(regionIBoundaryShape.getShapeLength() == 2){
                graphicsContext.moveTo(regionIBoundaryShape.getXCoordinateStartpoint(), regionIBoundaryShape.getYCoordinateStartpoint());
                graphicsContext.lineTo(regionIBoundaryShape.getXCoordinateEndpoint(), regionIBoundaryShape.getYCoordinateEndpoint());
                return;
            }

            List<Point2D> inputPointList = new ArrayList<>();
//            List<Point2D> inputPointList = regionIBoundaryShape.getCoordinates();
            regionIBoundaryShape.forEach(point2D -> inputPointList.add(point2D));

            List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...

            Point2D[] points = inputPointList.toArray(new Point2D[inputPointList.size()]);
            int n = points.length;

            for (int i = 0; i < n - 2; i++) {
                Pair<Point2D, Point2D> controlPoints = getControlPoints(points[i], points[i + 1], points[i + 2],
                        ConfigurationConstants.BEZIER_CURVE_SMOOTHNESS);
                cp.add(controlPoints.getKey());
                cp.add(controlPoints.getValue());
            }

            Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

            graphicsContext.moveTo(points[0].getX(), points[0].getY());
            graphicsContext.quadraticCurveTo(controlPoints[0].getX(), controlPoints[0].getY(), points[1].getX(), points[1].getY());

            for (int j = 1; j < n - 2; j++) {
                Point2D controlPoint1 = controlPoints[2 * j - 1];
                Point2D controlPoint2 = controlPoints[2 * j];

                graphicsContext.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(),
                        controlPoint2.getX(), controlPoint2.getY(),
                        points[j + 1].getX(), points[j + 1].getY());
            }

            graphicsContext.quadraticCurveTo(controlPoints[controlPoints.length - 1].getX(), controlPoints[controlPoints.length - 1].getY(),
                    points[points.length - 1].getX(), points[points.length - 1].getY());
        }
    }
}
