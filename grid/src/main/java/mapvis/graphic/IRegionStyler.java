package mapvis.graphic;

import mapvis.models.Region;

/**
 * Created by dacc on 11/12/2015.
 */
public interface IRegionStyler<T> extends IRegionBorderStyler<T>, IRegionAreaStyler<T> {
    int getMaxLabelLevelToShow();

    boolean getShowLabels();
//    boolean hasRegionChildrenWithAreasOrBordersToShow(Region<T> region);
//    boolean hasRegionAreasOrBordersToShow(Region<T> region);
}
