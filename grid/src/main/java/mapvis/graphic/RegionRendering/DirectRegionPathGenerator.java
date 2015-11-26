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

    }

    @Override
    public void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape) {

    }
}
