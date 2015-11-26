package mapvis.graphic.HexagonRendering;

import javafx.scene.paint.Color;
import mapvis.models.Dir;

public interface TileStyler<T> {
    /**
     * @param x x Coordinate of Tile
     * @param y y Coordinate of Tile
     * @param dir Direction of the border edge of the tile
     * @return true, if border edge should be rendered, otherwise false
     */
    boolean isBorderVisible(int x, int y, Dir dir);

    /**
     *
     * @param x x Coordinate of Tile
     * @param y y Coordinate of Tile
     * @param dir Direction of the border edge of the tile
     * @return the width of the border edge
     */
    double getBorderWidth(int x, int y, Dir dir);

    /**
     * @param level the level of the border edge
     * @return the corresponding width of the border
     */
    double getBorderWidthByLevel(int level);

    /**
     * @param x x Coordinate of Tile
     * @param y y Coordinate of Tile
     * @param dir Direction of the border edge of the edge
     * @return the color of the border
     */
    Color getBorderColor(int x, int y, Dir dir);

    /**
     *
     * @param nodeItem the nodeItem of the Tree
     * @return the color of the Tile
     */
    Color getColorByValue(T nodeItem);

    /**
     *
     * @param x x Coordinate of Tile
     * @param y y Coordinate of Tile
     * @return true, if the Tile should be rendered,
     * otherwise false
     */
    boolean isVisible(int x, int y);

    /**
     *
     * @param x x Coordinate of Tile
     * @param y y Coordinate of Tile
     * @return the Color of the Tile
     */
    Color getColor(int x, int y);

    /**
     * @return the background color of the visualization
     */
    Color getBackground();
}
