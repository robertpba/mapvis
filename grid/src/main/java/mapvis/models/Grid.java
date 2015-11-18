package mapvis.models;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface Grid<T> {
    /**
     * inserts a Tile into the Grid
     * @param tile the tile to insert
     */
    void putTile(Tile<T> tile);

    /**
     * retrieves the tile at the given position
     * @param pos where to lookup
     * @return the found tile
     */
    Tile<T> getTile(Pos pos);

    /**
     * retrieves the neighbour of the tile in the specified
     * direction
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @param dir direction of the neighbour
     * @return the found tile
     */
    Tile<T> getNeighbour(int x, int y, Dir dir);

    /**
     * collects all surrounding neighbours of the tile
     * at the given coordinates
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @return the set of neighbours
     */
    Set<Tile<T>> getNeighbours(int x, int y);

    /**
     * collects all surrounding neighbours of the tile
     * at the given coordinates fulfilling the given test
     * @param typeTester function defining whether neighbour should be collected
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @return the set of neighbours fulfilling the test
     */
    List<Dir> getNeighborDirectionsFulfilling(Predicate<Tile<T>> typeTester, int x, int y);

    /**
     * iterator for all tiles in the map
     * @param consumer object for the iteration
     */
    void foreach(Consumer<Tile<T>> consumer);

    /**
     *
     * @return all tile stored in the grid
     */
    Collection<Tile<T>> allTiles();

    /**
     * * retrieves the tile at the given position
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @return the found tile
     */
    default Tile<T> getTile(int x, int y){ return getTile(new Pos(x,y));}

    /**
     * creates tile and inserts its into the Grid
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @param item item to use for construction of the tile
     */
    default void putItem(int x, int y, T item){
        putTile(new Tile<>(x,y,item));
    }

    /**
     * creates tile and inserts its into the Grid
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @param item item to use for construction of the tile
     * @param tag the tag of the Tile to be created
     */
    default void putItem(int x, int y, T item, int tag){
        putTile(new Tile<>(x,y,item, tag));
    }

    /**
     * retrieves the tile at the given position
     * @param x x-coordinate of the tile
     * @param y y-coordinate of the tile
     * @return the found tile
     */
    default T getItem(int x, int y){
        return getTile(x,y).getItem();
    }

    /**
     * @return the smallest x coordinate
     * of the tiles stored in the grid
     */
    int getMinX();

    /**
     * @return the maximum x coordinate
     * of the tiles stored in the grid
     */
    int getMaxX();

    /**
     * @return the maximum y coordinate
     * of the tiles stored in the grid
     */
    int getMaxY();

    /**
     @return the smallest y coordinate
      of the tiles stored in the grid
     */
    int getMinY();

    /**
     * deletes all stored values of the grid
     */
    void resetGrid();


}
