package mapvis.graphic.RegionRendering;

import javafx.scene.paint.Color;
import mapvis.models.Region;

/**
 * Created by dacc on 11/12/2015.
 */
public interface IRegionAreaStyler<T> {

    boolean isRegionVisible(Region<T> region);

    Color getColor(Region<T> region);

    Color getBackground();

    int getMaxBorderLevelToShow();

    int getMaxRegionLevelToShow();

    boolean isLabelVisible(Region<T> region);

    int getMaxLabelLevelToShow();

    boolean getShowLabels();

}
