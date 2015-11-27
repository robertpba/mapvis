package mapvis.graphic.RegionRendering;

import javafx.scene.canvas.GraphicsContext;
import mapvis.models.IBoundaryShape;

/**
 * Created by dacc on 11/16/2015.
 * This class is used to render the IBoundaryShape directly without any simplifications.
 * Therefore the given IBoundaryShape is not adjusted.
 */
public class DirectBoundaryShapeSmoother<T> extends AbstractBoundaryShapeSmoother<T> {

    public DirectBoundaryShapeSmoother(GraphicsContext graphicsContext) {
        super(graphicsContext);
    }

    @Override
    void smoothBoundaryShape(IBoundaryShape<T> summarizedBoundaryStep) {
        //summarizedBoundaryStep just stays the same
    }
}
