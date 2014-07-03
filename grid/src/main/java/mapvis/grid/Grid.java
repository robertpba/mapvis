package mapvis.grid;

import java.util.function.Consumer;

public interface Grid {
    void put(int x, int y, Object obj);
    Object get(int x, int y);
    Tile getNeighbour(int x, int y, Dir dir);

    void foreach(Consumer<Tile> consumer);

}
