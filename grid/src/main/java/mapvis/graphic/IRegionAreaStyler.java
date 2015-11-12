package mapvis.graphic;

import javafx.scene.paint.Color;
import mapvis.models.Region;

/**
 * Created by dacc on 11/12/2015.
 */
public interface IRegionAreaStyler<T> {

//    boolean hasRegionVisibleChildren(Region<T> region);
    boolean isRegionVisible(Region<T> region);
    Color getColor(Region<T> region);
    Color getBackground();

//    void setMaxBorderLevelToShow(int maxBorderLevelToShow);

    int getMaxBorderLevelToShow();

    int getMaxRegionLevelToShow();

//    void setMaxRegionLevelToShow(int maxRegionLevelToShow);
}
