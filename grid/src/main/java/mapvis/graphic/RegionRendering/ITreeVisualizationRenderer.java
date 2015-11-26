package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;
import mapvis.common.datatype.INode;
import mapvis.models.Region;

/**
 * Created by dacc on 11/19/2015.
 */
public interface ITreeVisualizationRenderer {
    void renderScene(Point2D topleftBorder, Point2D bottomRightBorder);
    void configure(Object input);
}
