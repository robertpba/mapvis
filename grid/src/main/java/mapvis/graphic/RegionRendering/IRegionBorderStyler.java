package mapvis.graphic.RegionRendering;

import javafx.scene.paint.Color;
import mapvis.models.Border;

/**
 * Created by dacc on 11/12/2015.
 */
public interface IRegionBorderStyler<T> {
    boolean isBorderVisible(Border<T> border);
    double getBorderWidth(Border<T> border);
    Color getBorderColor(Border<T> border);
}
