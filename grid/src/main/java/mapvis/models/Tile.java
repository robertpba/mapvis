package mapvis.models;

public class Tile<T> {
    private T item;
    private int tag;

    private Pos pos;

    public final static int EMPTY = -1;
    public final static int LAND = 0;
    public final static int SEA = 1;  // territorial sea

    public int getX() {
        return pos.getX();
    }
    public int getY() {
        return pos.getY();
    }
    public T getItem() {
        return item;
    }
    public int getTag(){ return tag; }

    public Pos getPos(){ return pos; }

    public boolean isEmpty(){return tag == EMPTY;}

    public Tile(int x, int y){ this(new Pos(x,y), null, EMPTY); }
    public Tile(int x, int y, T item) { this (new Pos(x,y), item, LAND); }
    public Tile(int x, int y, T item, int tag){ this (new Pos(x,y), item, tag); }

    public Tile(Pos pos) { this(pos, null, EMPTY); }
    public Tile(Pos pos, T item) { this(pos, item, LAND); }
    public Tile(Pos pos, T item, int tag){
        this.pos = pos;
        this.item = item;
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (getX() != tile.getX()) return false;
        if (getY() != tile.getY()) return false;
        //if (item != null ? !item.equals(tile.item) : tile.item != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        //result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Label " + getTag() + " Pos: " + getPos().getX() + ";" + getPos().getY();
    }
}
