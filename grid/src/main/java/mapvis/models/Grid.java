package mapvis.models;

import java.util.Set;
import java.util.function.Consumer;

public interface Grid<T> {
    void putTile(Tile<T> tile);
    Tile<T> getTile(Pos pos);
    Tile<T> getNeighbour(int x, int y, Dir dir);
    Set<Tile<T>> getNeighbours(int x, int y);

    void foreach(Consumer<Tile<T>> consumer);

    default Tile<T> getTile(int x, int y){ return getTile(new Pos(x,y));}

    default void putItem(int x, int y, T item){
        putTile(new Tile<>(x,y,item));
    }
    default void putItem(int x, int y, T item, int tag){
        putTile(new Tile<>(x,y,item, tag));
    }
    default T getItem(int x, int y){
        return getTile(x,y).getItem();
    }
}
