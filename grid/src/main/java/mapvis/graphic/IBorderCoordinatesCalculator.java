package mapvis.graphic;

import javafx.geometry.Point2D;
import mapvis.models.LeafRegion;
import mapvis.models.Region;

import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 11/5/2015.
 */
public interface IBorderCoordinatesCalculator<T> {

    void setRegion(Region<T> region);

    List<Point2D> getDebugPoints();

    Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> getRegionToBoundaries();

    Map<Region<T>, List<List<LeafRegion.BoundaryShape>>> computeCoordinates(int maxLevelToDraw, boolean orderBorders);
}
