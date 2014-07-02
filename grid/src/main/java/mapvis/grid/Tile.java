package mapvis.grid;

public class Tile {
    public int x;
    public int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Object getObj() {
        return obj;
    }

    public Tile(int x, int y, Object obj) {

        this.x = x;
        this.y = y;
        this.obj = obj;
    }

    public Object obj;
}
