package mapvis.graphic.RegionRendering;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.IBoundaryShape;

import java.util.Arrays;
import java.util.List;

/**
 * This class is used to smooth IBoundaryShapes by simplifying them
 * using the Douglas-Peucker algorithm.
 * @param <T>
 */
public class SimplifiedBoundaryShapeSmoother<T> extends AbstractBoundaryShapeSmoother<T> {
    private float tolerance;
    private boolean useHighQuality;

    public SimplifiedBoundaryShapeSmoother(GraphicsContext graphicsContext, float tolerance, boolean useHighQuality) {
        super(graphicsContext);
        this.tolerance = tolerance;
        this.useHighQuality = useHighQuality;
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
    void smoothBoundaryShape(IBoundaryShape<T> partialRegionBoundary) {
        List<Point2D> shapePoints = partialRegionBoundary.getCoordinates();

        Point2D[] simplifiedPoints = simplifyPoints(shapePoints);

        List<Point2D> simplifiedPointCoordinates = Arrays.asList(simplifiedPoints);

        partialRegionBoundary.setCoordinates(simplifiedPointCoordinates);
        partialRegionBoundary.setCoordinatesNeedToBeReversed(false);
    }

    public void setSettings(float tolerance, boolean useHighQuality){
        this.clearChangedPaths();
        this.tolerance = tolerance;
        this.useHighQuality = useHighQuality;
    }
}