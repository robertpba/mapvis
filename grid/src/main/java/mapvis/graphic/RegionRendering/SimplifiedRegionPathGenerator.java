package mapvis.graphic.RegionRendering;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.IBoundaryShape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimplifiedRegionPathGenerator<T> extends AbstractRegionPathGenerator<T> {
    private float tolerance;
    private boolean useHighQuality;

    public SimplifiedRegionPathGenerator(GraphicsContext graphicsContext, float tolerance, boolean useHighQuality) {
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
    void createPathForBoundaryShape(IBoundaryShape<T> partialRegionBoundary) {
        List<Point2D> shapePoints =partialRegionBoundary.getCoordinates();

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