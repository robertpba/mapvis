package mapvis.graphic.RegionRendering;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.IBoundaryShape;

import java.util.ArrayList;
import java.util.List;

public class SimplifiedRegionPathGenerator<T> extends AbstractRegionPathGenerator<T> {
    private float tolerance;
    private boolean useHighQuality;

    public SimplifiedRegionPathGenerator(GraphicsContext graphicsContext, float tolerance, boolean useHighQuality) {
        super(graphicsContext);
        this.tolerance = tolerance;
        this.useHighQuality = useHighQuality;
    }

    @Override
    public void generatePathForBoundaryShape(List<IBoundaryShape<T>> regionIBoundaryShape) {

        for (IBoundaryShape partialRegionBoundary : regionIBoundaryShape) {

        }

//        return simplifiedShape;
    }

    @Override
    public void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape) {
        for (List<IBoundaryShape<T>> iBoundaryShapes : regionBoundaryShape) {
            generatePathForBoundaryShape(iBoundaryShapes);
        }
    }


    private Point2D[] simplifyPoints(List<Point2D> points) {
        Simplify<Point2D> simplify = new Simplify<>(new Point2D[0], new PointExtractor<Point2D>() {
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

    @Override
    void createPathForBoundaryShape(IBoundaryShape<T> partialRegionBoundary) {
        List<Point2D> shapePoints = new ArrayList<Point2D>();

        for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
            double xValue = partialRegionBoundary.getXCoordinateAtIndex(i);
            double yValue = partialRegionBoundary.getYCoordinateAtIndex(i);

            shapePoints.add(new Point2D(xValue, yValue));
        }

        Point2D[] simplifiedPoints = simplifyPoints(shapePoints);

        List<Double> simplifiedXCoords = new ArrayList<>();
        List<Double> simplifiedYCoords = new ArrayList<>();

        for (Point2D point2D : simplifiedPoints) {
            simplifiedXCoords.add(point2D.getX());
            simplifiedYCoords.add(point2D.getY());
        }

        partialRegionBoundary.setXCoords(simplifiedXCoords);
        partialRegionBoundary.setYCoords(simplifiedYCoords);
        partialRegionBoundary.setCoordinatesNeedToBeReversed(false);
    }

    public void setSettings(float tolerance, boolean useHighQuality){
        this.clearChangedPaths();
        this.tolerance = tolerance;
        this.useHighQuality = useHighQuality;
    }
}