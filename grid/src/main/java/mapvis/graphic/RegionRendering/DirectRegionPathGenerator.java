package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.IBoundaryShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public class DirectRegionPathGenerator<T> extends AbstractRegionPathGenerator<T> {

    public DirectRegionPathGenerator(GraphicsContext graphicsContext) {
        super(graphicsContext);
    }

    @Override
    void createPathForBoundaryShape(IBoundaryShape<T> summarizedBoundaryStep) {
        //summarizedBoundaryStep just stays the same
    }

    @Override
    public void generatePathForBoundaryShape(List<IBoundaryShape<T>> regionIBoundaryShape) {
        List<Point2D[]> shapePoints = new ArrayList<>();
        for (IBoundaryShape partialRegionBoundary : regionIBoundaryShape) {
            Point2D[] partialShapPoints = new Point2D[partialRegionBoundary.getShapeLength()];
            for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
                partialShapPoints[i] = new Point2D(partialRegionBoundary.getXCoordinateAtIndex(i), partialRegionBoundary.getYCoordinateAtIndex(i));
            }
            shapePoints.add(partialShapPoints);
        }
//        return shapePoints;
    }

    @Override
    public void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape) {

    }
}
