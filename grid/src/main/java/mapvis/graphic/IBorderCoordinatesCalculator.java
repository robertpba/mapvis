package mapvis.graphic;

import mapvis.common.datatype.Tuple2;
import mapvis.models.Border;
import mapvis.models.LeafRegion;

import java.util.List;

/**
 * Created by dacc on 11/5/2015.
 */
public interface IBorderCoordinatesCalculator<T> {
    void setLeafRegion(LeafRegion<T> leafRegion);

    List<Tuple2<Border<T>, LeafRegion.BoundaryShape>> computeCoordinates();
}
