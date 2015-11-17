package mapvis.graphic;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.LeafRegion;

import java.util.ArrayList;
import java.util.List;

public class SimplyfiedRegionPathGenerator implements IRegionPathGenerator {
    private final GraphicsContext graphicsContext;

    public SimplyfiedRegionPathGenerator(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public List<Point2D[]> drawRegionPaths(List<LeafRegion.BoundaryShape> regionBoundaryShape) {
        List<Point2D[]> simplyfiedShape = new ArrayList<>();

//        boolean firstDraw = true;
        for (LeafRegion.BoundaryShape partialRegionBoundary : regionBoundaryShape) {
            List<Point2D> shapePoints = new ArrayList<Point2D>();

            for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
                double xValue = partialRegionBoundary.getXValueAtIndex(i);
                double yValue = partialRegionBoundary.getYValueAtIndex(i);

                shapePoints.add(new Point2D(xValue, yValue));
//                firstDraw = addPathPoint(shapePoints, firstDraw, partialRegionBoundary, i);
            }

            Point2D[] point2Ds = simplifyPoints(shapePoints);
            simplyfiedShape.add(point2Ds);
        }

        return simplyfiedShape;
    }


    private Point2D[] simplifyPoints(List<Point2D> points) {
        Simplify<Point2D> simplify = new Simplify<Point2D>(new Point2D[0], new PointExtractor<Point2D>() {
            @Override
            public double getX(Point2D point) {
                return point.getX();
            }

            @Override
            public double getY(Point2D point) {
                return point.getY();
            }
        });
        float tolerance = 4.5f;
        boolean qualityHigh = false;
        return simplify.simplify(points.toArray(new Point2D[points.size()]), tolerance, qualityHigh);
    }


    private void drawSimplifiedPoints(List<Point2D[]> point2Ds) {
        boolean firstDraw = true;

        for (Point2D[] point2DArray : point2Ds) {
            for (Point2D point : point2DArray) {
                if (firstDraw) {
                    graphicsContext.moveTo(point.getX(), point.getY());
                    firstDraw = false;
                } else {
                    graphicsContext.lineTo(point.getX(), point.getY());
                }
            }

        }
    }
}