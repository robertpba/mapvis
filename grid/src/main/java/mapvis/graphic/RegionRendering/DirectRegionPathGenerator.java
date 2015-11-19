package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.BoundaryShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public class DirectRegionPathGenerator implements IRegionPathGenerator {
    private final GraphicsContext graphicsContext;

    public DirectRegionPathGenerator(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    @Override
    public List<Point2D[]> generatePathForBoundaryShape(List<BoundaryShape> regionBoundaryShape) {
        List<Point2D[]> shapePoints = new ArrayList<>();
        for (BoundaryShape partialRegionBoundary : regionBoundaryShape) {
            Point2D[] partialShapPoints = new Point2D[partialRegionBoundary.getShapeLength()];
            for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
                partialShapPoints[i] = new Point2D(partialRegionBoundary.getXCoordinateAtIndex(i), partialRegionBoundary.getYCoordinateAtIndex(i));
            }
            shapePoints.add(partialShapPoints);
        }
        return shapePoints;
    }
}
