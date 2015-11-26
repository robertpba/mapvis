package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.models.IBoundaryShape;

import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public interface IRegionPathGenerator<T> {
    void generatePathForBoundaryShape(List<IBoundaryShape<T>> regionIBoundaryShape);
    void generatePathForBoundaryShapes(List<List<IBoundaryShape<T>>> regionBoundaryShape);
}
