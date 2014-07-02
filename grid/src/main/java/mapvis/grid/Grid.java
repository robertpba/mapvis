package mapvis.grid;

public interface Grid {
    void put(int x, int y, Object obj);
    Object get(int x, int y);

    Tile getNeighbour(int x, int y, Dir dir);
}
