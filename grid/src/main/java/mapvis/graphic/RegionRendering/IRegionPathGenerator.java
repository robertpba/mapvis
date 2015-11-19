package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.models.BoundaryShape;
import mapvis.models.LeafRegion;

import java.util.List;

/**
 * Created by dacc on 11/16/2015.
 */
public interface IRegionPathGenerator {
    List<Point2D[]> generatePathForBoundaryShape(List<BoundaryShape> regionBoundaryShape);
}
