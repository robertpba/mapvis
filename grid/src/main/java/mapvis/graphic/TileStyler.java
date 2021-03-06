package mapvis.graphic;

import javafx.scene.paint.Color;
import mapvis.models.Dir;

public interface TileStyler<T> {
    boolean isBorderVisible(int x, int y, Dir dir);
    double getBorderWidth(int x, int y, Dir dir);
    Color getBorderColor(int x, int y, Dir dir);
    boolean isVisible(int x, int y);
    Color getColor(int x, int y);
    Color getBackground();
}
