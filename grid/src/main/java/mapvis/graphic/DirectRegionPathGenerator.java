package mapvis.graphic;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import mapvis.models.LeafRegion;

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
    public List<Point2D[]> drawRegionPaths(List<LeafRegion.BoundaryShape> regionBoundaryShape) {
        List<Point2D[]> shapePoints = new ArrayList<>();
        for (LeafRegion.BoundaryShape partialRegionBoundary : regionBoundaryShape) {
            Point2D[] partialShapPoints = new Point2D[partialRegionBoundary.getShapeLength()];
            for (int i = 0; i < partialRegionBoundary.getShapeLength(); i++) {
                partialShapPoints[i] = new Point2D(partialRegionBoundary.getXValueAtIndex(i), partialRegionBoundary.getYValueAtIndex(i));
            }
            shapePoints.add(partialShapPoints);
        }
        return shapePoints;
    }
//
//    private boolean drawPath(boolean firstDraw, LeafRegion.BoundaryShape partialRegionBoundary, int i) {
//        double xValue = partialRegionBoundary.xValues[i];
//        double yValue = partialRegionBoundary.yValues[i];
//
//        if (firstDraw) {
//            graphicsContext.moveTo(xValue, yValue);
//            firstDraw = false;
//        } else {
//            graphicsContext.lineTo(xValue, yValue);
//        }
//        return firstDraw;
//    }


}
