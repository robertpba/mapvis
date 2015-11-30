package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tuple2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.ConfigurationConstants;
import mapvis.models.IBoundaryShape;
import mapvis.models.Region;
import mapvis.models.Tile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mapvis.graphic.RegionRendering.AbstractBoundaryShapeSmoother.BoundaryShapesWithReverseInformation;

/**
 * Created by dacc on 10/26/2015.
 * This class is used to Render the Regions created for each Tree Node.
 * This Renderer first renders the Area of the Region using the @RegionAreaRenderer,
 * then the Borders using the @RegionBorderRenderer and
 * finally the Labels of the Nodes using the @RegionLabelRenderer. The rendering
 * is performed according to the Rendering configuration of the UI which is stored
 * and implemented in the @IRegionStyler, @AbstractBoundaryShapeSmoother and @BoundaryShapeRenderer.
 */
public class RegionRenderer implements ITreeVisualizationRenderer {

    private final Canvas canvas;
    private final HexagonalTilingView view;

    private final RegionAreaRenderer regionAreaRenderer;
    private final RegionBorderRenderer regionBorderRenderer;
    private final RegionLabelRenderer regionLabelRenderer;

    private AbstractBoundaryShapeSmoother<INode> boundarysSmoothingAlgorithm;

    private Region rootRegion;

    public RegionRenderer(HexagonalTilingView view, Canvas canvas) {
        System.out.println("Creating: " + this.getClass().getName());

        this.view = view;
        this.canvas = canvas;

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        this.regionAreaRenderer = new RegionAreaRenderer(graphicsContext2D);
        this.regionBorderRenderer = new RegionBorderRenderer(graphicsContext2D);
        this.regionLabelRenderer = new RegionLabelRenderer(graphicsContext2D);
        this.regionBorderRenderer.setIsSingleSideBorderRenderingEnabled(true);

        setRenderingMethod(ConfigurationConstants.RENDERING_METHOD_DEFAULT);
        setBoundarySimplificationMethod(ConfigurationConstants.BoundaryShapeSmoothingMethod.None);
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

        if(rootRegion == null)
            return;

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.save();


        IRegionStyler<INode> regionStyler = view.getRegionStyler();

        //request the maximum region level to be collected. Area and Borders have
        //to have the same shape, so smoothing of the borders has to be performed on the
        //highest visualized level to ensure consistency between area and border.

        //IRegionStyler ensures that Regions with level > MaxRegionLevel, but which were
        //collected for because maxBorderLevel > MaxRegionLevel, are assigned to color of the
        //parent with is of maxRegionLevel.

        //IRegionStyler also ensures that Borders with level > maxBorderLevel are not rendered
        int maxChildrenToCollect = Math.max(regionStyler.getMaxBorderLevelToShow(),
                                            regionStyler.getMaxRegionLevelToShow());


        List<Region<INode>> childRegionsAtLevel = rootRegion.getChildRegionsAtLevel(maxChildrenToCollect);

        //smooth the BoundaryShaps of Regions
        List<Tuple2<Region<INode>, List<BoundaryShapesWithReverseInformation<INode>>>> regionToSmoothedBorders = new ArrayList<>();
        for (Region<INode> region : childRegionsAtLevel) {
            List<List<IBoundaryShape<INode>>> boundaryShape = region.getBoundaryShape();
            List<BoundaryShapesWithReverseInformation<INode>> simplifiedBorders = new ArrayList<>();

            for (List<IBoundaryShape<INode>> iBoundaryShapes : boundaryShape) {
                BoundaryShapesWithReverseInformation<INode> simplifiedBoundaryShape = boundarysSmoothingAlgorithm.
                        summarizeAndSmoothBoundaryShape(iBoundaryShapes, maxChildrenToCollect, view.getTree());
                simplifiedBorders.add(simplifiedBoundaryShape);
            }
            regionToSmoothedBorders.add(new Tuple2<>(region, simplifiedBorders));
        }

        //Render the Areas
        for (Tuple2<Region<INode>, List<BoundaryShapesWithReverseInformation<INode>>> regionAndSimplifiedBorder : regionToSmoothedBorders) {
            regionAreaRenderer.drawArea(regionStyler, regionAndSimplifiedBorder.first, regionAndSimplifiedBorder.second);
        }

        //Render the Borders
        for (Tuple2<Region<INode>, List<BoundaryShapesWithReverseInformation<INode>>> regionAndSimplifiedBorder : regionToSmoothedBorders) {
            regionBorderRenderer.drawBorder(regionStyler, regionAndSimplifiedBorder.second);
        }


        //Render the Borders
        if(regionStyler.getShowLabels()){
            List<Region<INode>> labelRegions = null;
            if(regionStyler.getMaxLabelLevelToShow() == maxChildrenToCollect){
                labelRegions = childRegionsAtLevel;
            }else{
                labelRegions = rootRegion.getChildRegionsAtLevel(regionStyler.getMaxLabelLevelToShow());
            }

            for (Region<INode> region : labelRegions) {
                List<List<IBoundaryShape<INode>>> boundaryShape = region.getBoundaryShape();
                regionLabelRenderer.drawLabels(regionStyler, region, boundaryShape);
            }
        }

        graphicsContext.restore();
    }

    public void setRootRegion(Region<INode> rootRegion) {
        this.rootRegion = rootRegion;
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

    public void setBoundarySimplificationMethod(ConfigurationConstants.BoundaryShapeSmoothingMethod boundaryShapeSmoothingMethod) {
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        switch (boundaryShapeSmoothingMethod) {
            case DouglasPeucker:
                this.boundarysSmoothingAlgorithm =
                        new SimplifiedBoundaryShapeSmoother<>(graphicsContext2D,
                                ConfigurationConstants.SIMPLIFICATION_TOLERANCE, ConfigurationConstants.USE_HIGH_QUALITY_SIMPLIFICATION);
                break;
            case Average:
                this.boundarysSmoothingAlgorithm =
                        new MovingAverageBoundaryShapeSmoother<>(graphicsContext2D);
                break;
            case None:
                this.boundarysSmoothingAlgorithm =
                        new DirectBoundaryShapeSmoother<>(graphicsContext2D);
                break;
        }
    }

    public void setBoundarySimplificationAlgorithmSettings(float simplificationTolerance, boolean useHighQualityDouglasPeucker){
        if(this.boundarysSmoothingAlgorithm instanceof SimplifiedBoundaryShapeSmoother){
            ((SimplifiedBoundaryShapeSmoother) boundarysSmoothingAlgorithm).setSettings(simplificationTolerance, useHighQualityDouglasPeucker);
        }
    }

    /**
     * Abstract class for Rendering the IBoundaryShapes
     * @param <T>
     */
    protected abstract class BoundaryShapeRenderer<T>{
        protected GraphicsContext graphicsContext;

        public BoundaryShapeRenderer(GraphicsContext graphicsContext) {
            this.graphicsContext = graphicsContext;
        }

        /**
         * This method renders the area defined by the @BoundaryShapesWithReverseInformation
         * as a Path.
         * @param regionIBoundaryShape the regionBoundaryShape defining the area of a Region
         */
        abstract void renderClosedBoundaryShapeArea(BoundaryShapesWithReverseInformation<T> regionIBoundaryShape);

        /**
         * This method renders the boundaryShape as Path. regionIBoundaryShape is not supposed
         * to be a closed area but also a part of a border.
         * @param regionIBoundaryShape the IBoundaryShape to be rendered
         */
        abstract void renderBoundaryShapeSegment(IBoundaryShape<T> regionIBoundaryShape);
    }

    /**
     * This class renders IBoundaryShapes by connecting subsequent IBoundaryShape coordinates
     * with a quadratic curve.
     * @param <T> the NodeType of the tree
     */
    private class QuadraticCurveBoundaryShapeRenderer<T> extends BoundaryShapeRenderer<T>{

        public QuadraticCurveBoundaryShapeRenderer(GraphicsContext graphicsContext) {
            super(graphicsContext);
        }

        @Override
        public void renderClosedBoundaryShapeArea(BoundaryShapesWithReverseInformation<T> regionBoundaryShape) {
            boolean firstRenderPass = true;

            Point2D closingEndPoint = null;

            for (Tuple2<IBoundaryShape<T>, Boolean> tIBoundaryShape : regionBoundaryShape) {
                //recover the reverse information
                tIBoundaryShape.first.setCoordinatesNeedToBeReversed(tIBoundaryShape.second);
                closingEndPoint = renderBoundaryShapeAsLoop(tIBoundaryShape.first, firstRenderPass);
                firstRenderPass = false;
            }

            //close the area by a line
            if(!firstRenderPass)
                graphicsContext.lineTo(closingEndPoint.getX(), closingEndPoint.getY());
        }


        Point2D renderBoundaryShapeAsLoop(IBoundaryShape<T> regionIBoundaryShape, boolean moveToRequired) {
            boolean newSummarizedShape = true;

            Iterator<Point2D> currCoordinateIterator = regionIBoundaryShape.iterator();
            Iterator<Point2D> nextCoordinateIterator = regionIBoundaryShape.iterator();
            Point2D nextCoordinate = nextCoordinateIterator.next();
            Point2D currCoordinate = null;

            while (nextCoordinateIterator.hasNext()){

                currCoordinate = currCoordinateIterator.next();
                nextCoordinate = nextCoordinateIterator.next();

                //calc secondPoint between two subsequent coordinates
                Point2D midPoint = currCoordinate.add(nextCoordinate).multiply(0.5);;

                if (moveToRequired) {
                    graphicsContext.moveTo(midPoint.getX(), midPoint.getY());
                    moveToRequired = false;
                }
                if(newSummarizedShape){
                    //current point to secondPoint is connected by single lines
                    graphicsContext.lineTo(currCoordinate.getX(), currCoordinate.getY());
                    graphicsContext.lineTo(midPoint.getX(), midPoint.getY());
                    newSummarizedShape = false;
                }else{
                    //quadratic curve from last mid point to current secondPoint and using the current
                    //coordinate as control point
                    graphicsContext.quadraticCurveTo(currCoordinate.getX(), currCoordinate.getY(),
                            midPoint.getX(), midPoint.getY());
                }
            }

            return nextCoordinate;
        }


        @Override
        void renderBoundaryShapeSegment(IBoundaryShape<T> regionIBoundaryShape) {
            if(regionIBoundaryShape.getShapeLength() == 0)
                return;

            Point2D lastLine = renderBoundaryShapeAsLoop(regionIBoundaryShape, true);
            graphicsContext.lineTo(lastLine.getX(), lastLine.getY());
        }
    }

    /**
     * This @BoundaryShapeRenderer renders the IBoundaryShape coordinates by connecting
     * them by lines.
     * @param <T>
     */
    private class DirectPolylineBoundaryShapeRenderer<T> extends BoundaryShapeRenderer<T>{

        public DirectPolylineBoundaryShapeRenderer(GraphicsContext graphicsContext) {
            super(graphicsContext);
        }

        @Override
        public void renderClosedBoundaryShapeArea(BoundaryShapesWithReverseInformation<T> regionBoundaryShape) {
            if(regionBoundaryShape.size() == 0)
                return;
            //first renderpass always requires moveTo as initial point of the path
            boolean firstRenderPass = true;
            for (Tuple2<IBoundaryShape<T>, Boolean> boundaryShape : regionBoundaryShape) {
                //recover the reverse information
                boundaryShape.first.setCoordinatesNeedToBeReversed(boundaryShape.second);

                renderBoundaryShapeAsLoop(boundaryShape.first, firstRenderPass);
                firstRenderPass = false;
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
        void renderBoundaryShapeSegment(IBoundaryShape<T> regionIBoundaryShape) {
            renderBoundaryShapeAsLoop(regionIBoundaryShape, true);
        }
    }

    /**
     * This @BoundaryShapeRenderer renders the @IBoundaryShapes by fitting Bezier curves. The general
     * algorithm is implemented according to the explanations and source code available on:
     *
     * http://scaledinnovation.com/analytics/splines/aboutSplines.html
     * view-source:http://scaledinnovation.com/analytics/splines/splines.html
     *
     * Source code is published under  GNU General Public License of version 3 or later with
     * Copyright 2010 by Robin W. Spencer.
     * @param <T> the ItemType of the Nodes of the Tree
     */
    private class BezierCurveBoundaryShapeRenderer<T> extends BoundaryShapeRenderer<T>{

        public BezierCurveBoundaryShapeRenderer(GraphicsContext graphicsContext) {
            super(graphicsContext);
        }

        private Tuple2<Point2D, Point2D> getControlPoints(Point2D x0, Point2D x1, Point2D x2, double t) {
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

            return new Tuple2(new Point2D(p1x, p1y), new Point2D(p2x, p2y));
        }

        @Override
        public void renderClosedBoundaryShapeArea(BoundaryShapesWithReverseInformation<T> boundaryShapesWithReverseInformation) {
            List<Tuple2<IBoundaryShape<T>, Boolean>> boundaryShapeAndOrdering = boundaryShapesWithReverseInformation;
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

                boundaryStep.forEach(point2D -> inputPointList.add(point2D));
            }

            Point2D firstPoint = inputPointList.get(0);
            inputPointList.add(firstPoint);

            List<Point2D> controlPoints = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...

            Iterator<Point2D> firstPointIt = inputPointList.iterator();
            Iterator<Point2D> secondPointIt = inputPointList.iterator();
            secondPointIt.next();

            Iterator<Point2D> thirdPointIt = inputPointList.iterator();
            thirdPointIt.next();
            thirdPointIt.next();

            while(thirdPointIt.hasNext()){
                Point2D firstPointCoord = firstPointIt.next();
                Point2D secondPointCoord = secondPointIt.next();
                Point2D thirdPointCoord = thirdPointIt.next();

                Tuple2<Point2D, Point2D> controlPointPair = getControlPoints(firstPointCoord, secondPointCoord, thirdPointCoord,
                        ConfigurationConstants.BEZIER_CURVE_SMOOTHNESS);
                controlPoints.add(controlPointPair.first);
                controlPoints.add(controlPointPair.second);
            }

            controlPoints.add(controlPoints.get(controlPoints.size() - 2));
            controlPoints.add(controlPoints.get(controlPoints.size() - 1));

            renderBezierCurve(inputPointList, controlPoints);
        }

        void renderBezierCurve(List<Point2D> inputPointList, List<Point2D> controlPoints){

            Iterator<Point2D> inputPointIt = inputPointList.iterator();
            inputPointIt.next();


            Iterator<Point2D> firstControlPointIt = controlPoints.iterator();
            firstControlPointIt.next();

            Iterator<Point2D> secondControlPointIt = controlPoints.iterator();
            secondControlPointIt.next();
            secondControlPointIt.next();

            Point2D currInputPoint = inputPointIt.next();

            graphicsContext.moveTo(currInputPoint.getX(), currInputPoint.getY());

            while (inputPointIt.hasNext()) {
                //startIndex 1
                currInputPoint = inputPointIt.next();

                //startIndex 1
                Point2D controlPoint1 = firstControlPointIt.next();
                //startIndex 2
                Point2D controlPoint2 = secondControlPointIt.next();

                //switch to next Pair<controlPoint1, controlPoint2>
                firstControlPointIt.next();
                secondControlPointIt.next();

                graphicsContext.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(),
                        controlPoint2.getX(), controlPoint2.getY(),
                        currInputPoint.getX(), currInputPoint.getY());
            }
        }

        @Override
        void renderBoundaryShapeSegment(IBoundaryShape<T> regionIBoundaryShape) {
            if(regionIBoundaryShape.getShapeLength() == 0)
                return;

            if(regionIBoundaryShape.getShapeLength() == 2){
                graphicsContext.moveTo(regionIBoundaryShape.getXCoordinateStartpoint(), regionIBoundaryShape.getYCoordinateStartpoint());
                graphicsContext.lineTo(regionIBoundaryShape.getXCoordinateEndpoint(), regionIBoundaryShape.getYCoordinateEndpoint());
                return;
            }

            List<Point2D> inputPointList = regionIBoundaryShape.getCoordinates();

            List<Point2D> controlPoints = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...


            Iterator<Point2D> firstPointIt = inputPointList.iterator();
            Iterator<Point2D> secondPointIt = inputPointList.iterator();
            secondPointIt.next();

            Iterator<Point2D> thirdPointIt = inputPointList.iterator();
            thirdPointIt.next();
            thirdPointIt.next();

            while(thirdPointIt.hasNext()){
                Point2D firstPointCoord = firstPointIt.next();
                Point2D secondPointCoord = secondPointIt.next();
                Point2D thirdPointCoord = thirdPointIt.next();

                Tuple2<Point2D, Point2D> controlPointPair = getControlPoints(firstPointCoord, secondPointCoord, thirdPointCoord,
                        ConfigurationConstants.BEZIER_CURVE_SMOOTHNESS);
                controlPoints.add(controlPointPair.first);
                controlPoints.add(controlPointPair.second);
            }

            Iterator<Point2D> inputPointIterator = inputPointList.iterator();

            Point2D currInputPoint = inputPointIterator.next();
            graphicsContext.moveTo(currInputPoint.getX(), currInputPoint.getY());

            //index 0
            currInputPoint = inputPointIterator.next();

            Iterator<Point2D> firstControlPointIt = controlPoints.iterator();
            Point2D firstControlPoint = firstControlPointIt.next();//index 0

            graphicsContext.quadraticCurveTo(firstControlPoint.getX(), firstControlPoint.getY(), currInputPoint.getX(), currInputPoint.getY());
            currInputPoint = inputPointIterator.next(); //index 1

            Iterator<Point2D> secondControlPointIt = controlPoints.iterator();
            secondControlPointIt.next();                             //index 0
            Point2D secondControlPoint = secondControlPointIt.next();//index 1

            while (secondControlPointIt.hasNext()){
                firstControlPoint = firstControlPointIt.next();
                secondControlPoint = secondControlPointIt.next();

                graphicsContext.bezierCurveTo(firstControlPoint.getX(), firstControlPoint.getY(),
                        secondControlPoint.getX(), secondControlPoint.getY(),
                        currInputPoint.getX(), currInputPoint.getY());


                firstControlPoint = firstControlPointIt.next();
                secondControlPoint = secondControlPointIt.next();

                currInputPoint = inputPointIterator.next();
            }

            graphicsContext.quadraticCurveTo(secondControlPoint.getX(), secondControlPoint.getY(),
                    currInputPoint.getX(), currInputPoint.getY());
        }
    }
}
