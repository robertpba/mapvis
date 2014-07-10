package mapvis.grid;

import java.util.Set;
import java.util.function.Consumer;

public interface Grid<T> {
    void put(int x, int y, T obj);
    T get(int x, int y);
    Tile<T> getTile(int x, int y);
    Tile<T> getNeighbour(int x, int y, Dir dir);
    Set<Tile<T>> getNeighbours(int x, int y);

    void foreach(Consumer<Tile<T>> consumer);

}
