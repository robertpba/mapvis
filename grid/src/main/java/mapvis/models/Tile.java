package mapvis.models;

public class Tile<T> {
    int x;
    int y;
    T item;
    int tag;

    Pos pos;

    final static int EMPTY = -1;
    final static int LAND = 0;
    final static int SEA = 1;  // territorial sea

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public T getItem() {
        return item;
    }

    public Pos getPos(){ return pos; }

    public boolean isEmpty(){return tag == EMPTY;}

    public Tile(int x, int y){ this(new Pos(x,y), null, EMPTY); }
    public Tile(int x, int y, T item) { this (new Pos(x,y), item, LAND); }
    public Tile(int x, int y, T item, int tag){ this (new Pos(x,y), item, tag); }

    public Tile(Pos pos) { this(pos, null, EMPTY); }
    public Tile(Pos pos, T item) { this(pos, item, EMPTY); }
    public Tile(Pos pos, T item, int tag){
        this.x = pos.getX();
        this.y = pos.getY();
        this.pos = pos;
        this.item = item;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        if (y != tile.y) return false;
        //if (item != null ? !item.equals(tile.item) : tile.item != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        //result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

}
