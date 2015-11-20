package mapvis.graphic.RegionRendering;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.BoundaryShape;

import java.util.ArrayList;
import java.util.List;

public class SimplifiedRegionPathGenerator<T> implements IRegionPathGenerator<T> {
    private final GraphicsContext graphicsContext;
    private final float tolerance;
    private final boolean useHighQuality;

    public SimplifiedRegionPathGenerator(GraphicsContext graphicsContext, float tolerance, boolean useHighQuality) {
        this.tolerance = tolerance;
        this.useHighQuality = useHighQuality;
        this.graphicsContext = graphicsContext;
    }

    public List<Point2D[]> generatePathForBoundaryShape(List<BoundaryShape<T>> regionBoundaryShape) {
        List<Point2D[]> simplifiedShape = new ArrayList<>();
        for (BoundaryShape partialRegionBoundary : regionBoundaryShape) {
            List<Point2D> shapePoints = new ArrayList<Point2D>();

            for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
                double xValue = partialRegionBoundary.getXCoordinateAtIndex(i);
                double yValue = partialRegionBoundary.getYCoordinateAtIndex(i);

                shapePoints.add(new Point2D(xValue, yValue));
            }

            Point2D[] point2Ds = simplifyPoints(shapePoints);
            double[] simpliyfiedXCoords = new double[point2Ds.length];
            double[] simpliyfiedYCoords = new double[point2Ds.length];

            int i = 0;
            for (Point2D point2D : point2Ds) {
                simpliyfiedXCoords[i] = point2D.getX();
                simpliyfiedYCoords[i] = point2D.getY();
                i++;
            }
            partialRegionBoundary.setXCoords(simpliyfiedXCoords);
            partialRegionBoundary.setYCoords(simpliyfiedYCoords);
            partialRegionBoundary.coordinatesNeedToBeReversed = false;

            simplifiedShape.add(point2Ds);
        }

        return simplifiedShape;
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

        return simplify.simplify(points.toArray(new Point2D[points.size()]), tolerance, useHighQuality);
    }

}