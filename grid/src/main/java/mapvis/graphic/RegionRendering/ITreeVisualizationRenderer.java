package mapvis.graphic.RegionRendering;

import javafx.geometry.Point2D;

/**
 * Created by dacc on 11/19/2015.
 */
public interface ITreeVisualizationRenderer {
    void renderScene(Point2D topleftBorder, Point2D bottomRightBorder);
    void configure(Object input);
}
