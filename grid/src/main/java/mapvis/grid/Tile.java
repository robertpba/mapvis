package mapvis.grid;

public class Tile<T> {
    public int x;
    public int y;

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

    public T obj;
}
