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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RegionAreaRenderer {
    private final static boolean USE_HIGH_QUALITY_SIMPLIFICATION = true;
    private final static float SIMPLIFICATION_TOLERANCE = 4.55f;
    private final static float BEZIER_CURVE_SMOOTHNESS = 0.3f;

    private final static boolean USE_BEZIER_CURVE = false;

    private final static boolean DRAW_ORIGINAL_SHAPE = false;
    private final static boolean FILL_SHAPE = true;

    private final GraphicsContext graphicsContext;
    private final IRegionPathGenerator regionBoundaryPointsGenerator;
    private final Consumer<List<Point2D[]>> directPolyLineRenderer;
    private final Consumer<List<Point2D[]>> bezierCurveRenderer;
    private final Consumer<List<Point2D[]>> quadricCurveRenderer;
    private final IRegionPathGenerator originalBoundaryPointsGenerator;

    public RegionAreaRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;

//        this.regionBoundaryPointsGenerator = new SimplifiedRegionPathGenerator(graphicsContext, SIMPLIFICATION_TOLERANCE, USE_HIGH_QUALITY_SIMPLIFICATION);
//        this.regionBoundaryPointsGenerator = new MovingAverageRegionPathGenerator(2);
        this.regionBoundaryPointsGenerator = new DirectRegionPathGenerator(graphicsContext);

        this.originalBoundaryPointsGenerator = new DirectRegionPathGenerator(graphicsContext);

        this.quadricCurveRenderer = (shapePointArr) -> {
            boolean firstRenderPass = true;
            Point2D firstPoint = null;
            Point2D currentPoint = null;
            Point2D nextPoint = null;
            for (int i = 0; i < shapePointArr.size(); i++) {
                Point2D[] point2Ds = shapePointArr.get(i);
                for (int j = 0; j < point2Ds.length - 2; j++) {
                    if(firstRenderPass){
                        double xMid = (point2Ds[0].getX() + point2Ds[1].getX()) / 2;
                        double yMid = (point2Ds[0].getY() + point2Ds[1].getY()) / 2;

                        graphicsContext.moveTo(xMid, yMid);
                        firstPoint = new Point2D(xMid, yMid);
                        firstRenderPass = false;
                    }
                    currentPoint = point2Ds[j];
                    nextPoint = point2Ds[j + 1];
                    double xMid = (currentPoint.getX() + nextPoint.getX()) / 2;
                    double yMid = (currentPoint.getY() + nextPoint.getY()) / 2;
                    graphicsContext.quadraticCurveTo(currentPoint.getX(), currentPoint.getY(), xMid, yMid);
                }
            }
            graphicsContext.quadraticCurveTo(nextPoint.getX(), nextPoint.getY(), firstPoint.getX(), firstPoint.getY());
        };

        this.directPolyLineRenderer = (shapePointArr) -> {

            boolean firstRenderPass = true;
            for (Point2D[] point2Ds : shapePointArr) {
                for (Point2D point2D : point2Ds) {
                    if(firstRenderPass){
                        graphicsContext.moveTo(point2D.getX(), point2D.getY());
                        firstRenderPass = false;
                    }else{
                        graphicsContext.lineTo(point2D.getX(), point2D.getY());
                    }
                }
            }
        };

        this.bezierCurveRenderer = (shapePointArr) -> {
            drawSpline(graphicsContext, shapePointArr, BEZIER_CURVE_SMOOTHNESS);
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

        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {

            if (regionBoundaryShape.size() == 0)
                continue;

            List<Point2D[]> shapePoints = regionBoundaryPointsGenerator.generatePathForBoundaryShape(regionBoundaryShape);
            if(USE_BEZIER_CURVE){
//                this.bezierCurveRenderer.accept(shapePoints);
                this.quadricCurveRenderer.accept(shapePoints);
            }else{
                this.directPolyLineRenderer.accept(shapePoints);
            }
        }

        graphicsContext.setLineCap(StrokeLineCap.ROUND);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        if(DRAW_ORIGINAL_SHAPE){
            this.graphicsContext.setStroke(Color.RED);
        }else{
            this.graphicsContext.setStroke(Color.BLACK);
        }


        graphicsContext.stroke();
        if(FILL_SHAPE){
            graphicsContext.fill();
        }

        if(!DRAW_ORIGINAL_SHAPE)
            return;

        graphicsContext.beginPath();
        for (List<LeafRegion.BoundaryShape> regionBoundaryShape : regionBoundaryShapes) {

            if (regionBoundaryShape.size() == 0)
                continue;

            List<Point2D[]> originalShapePoints = originalBoundaryPointsGenerator.generatePathForBoundaryShape(regionBoundaryShape);
//            this.bezierCurveRenderer.accept(shapePoints);
            this.directPolyLineRenderer.accept(originalShapePoints );
            this.graphicsContext.setStroke(Color.BLACK);
        }

        graphicsContext.setLineCap(StrokeLineCap.ROUND);
        graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
        graphicsContext.stroke();
//        graphicsContext.fill();
    }

    void drawSpline(GraphicsContext ctx, List<Point2D[]> input, double t) {
        List<Point2D> cp = new ArrayList<>();   // array of control points, as x0,y0,x1,y1,...

        List<Point2D> inputPointList = new ArrayList<>();
        Point2D[] lastBoundaryShape = input.get(input.size() - 1);
        Point2D lastPoint = lastBoundaryShape[lastBoundaryShape.length - 1];
        inputPointList.add(lastPoint);

        for (int i = 0; i < input.size(); i++) {
            Point2D[] point2Ds = input.get(i);
            for (int j = 0; j < point2Ds.length; j++) {
                inputPointList.add(point2Ds[j]);
            }
        }

        Point2D firstPoint = inputPointList.get(0);
        inputPointList.add(firstPoint);

        Point2D[] points = inputPointList.toArray(new Point2D[inputPointList.size()]);
        int n = points.length;

        for (int i = 0; i < n - 2; i++) {
            Pair<Point2D, Point2D> controlPoints = getControlPoints(points[i], points[i + 1], points[i + 2], t);
            cp.add(controlPoints.getKey());
            cp.add(controlPoints.getValue());
        }
        cp.add(cp.get(cp.size() - 2));
        cp.add(cp.get(cp.size() - 1));

        Point2D[] controlPoints = cp.toArray(new Point2D[cp.size()]);

        for (int j = 1; j < n - 1; j++) {
            if(j == 1){
                ctx.moveTo(points[j].getX(), points[j].getY());
            }
            Point2D controlPoint1 = controlPoints[2 * j - 1];
            Point2D controlPoint2 = controlPoints[2 * j];

            ctx.bezierCurveTo(controlPoint1.getX(), controlPoint1.getY(),
                    controlPoint2.getX(), controlPoint2.getY(),
                    points[j + 1].getX(), points[j + 1].getY());
        }
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


    public void initForNextRenderingPhase() {
    }

    public void finishRenderingPhase() {
    }

}