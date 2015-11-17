package mapvis.graphic;

import javafx.geometry.Point2D;
import mapvis.models.LeafRegion;

import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public interface IRegionPathGenerator {
    List<Point2D[]> drawRegionPaths(List<LeafRegion.BoundaryShape> regionBoundaryShape);
}
