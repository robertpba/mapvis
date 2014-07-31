package mapvis.grid;

public class Tile<T> {
    int x;
    int y;
    T obj;

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public T getObj() {
        return obj;
    }

    public Tile(int x, int y, T obj) {

        this.x = x;
        this.y = y;
        this.obj = obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        if (y != tile.y) return false;
        //if (obj != null ? !obj.equals(tile.obj) : tile.obj != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        //result = 31 * result + (obj != null ? obj.hashCode() : 0);
        return result;
    }

}
