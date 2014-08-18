package mapvis.Impl;

import javafx.scene.paint.Color;
import mapvis.models.TreeModel;
import mapvis.graphic.TileStyler;
import mapvis.models.Dir;
import mapvis.models.Grid;
import mapvis.models.Tile;

public class TileStylerBase<T> implements TileStyler<T> {
    TreeModel<T> tree;
    Grid<T>      grid;

    public TileStylerBase(TreeModel<T> tree, Grid<T> grid) {
        this.tree = tree;
        this.grid = grid;
    }

    public Grid<T> getGrid() {
        return grid;
    }

    public void setGrid(Grid<T> grid) {
        this.grid = grid;
    }

    public TreeModel<T> getTree() {
        return tree;
    }

    public void setTree(TreeModel<T> tree) {
        this.tree = tree;
    }


    @Override
    public boolean isBorderVisible(int x, int y, Dir dir) {
        TileCache c = getCache(x, y);
        int l = 0;
        switch (dir){
            case N: l = c.borderN; break;
            case S: l = c.borderS; break;
            case NE: l = c.borderNE;break;
            case SE: l = c.borderSE;break;
            case NW: l = c.borderNW;break;
            case SW: l = c.borderSW;break;
        }
        return l > 0;
    }

    @Override
    public double getBorderWidth(int x, int y, Dir dir) {
        TileCache c = getCache(x, y);
        int l = 0;
        switch (dir){
            case N: l = c.borderN; break;
            case S: l = c.borderS; break;
            case NE: l = c.borderNE;break;
            case SE: l = c.borderSE;break;
            case NW: l = c.borderNW;break;
            case SW: l = c.borderSW;break;
        }
        return getBorderWidthByLevel(l);
    }

    @Override
    public Color getBorderColor(int x, int y, Dir dir) {
        TileCache c = getCache(x, y);
        int l = 0;
        switch (dir){
            case N: l = c.borderN; break;
            case S: l = c.borderS; break;
            case NE: l = c.borderNE;break;
            case SE: l = c.borderSE;break;
            case NW: l = c.borderNW;break;
            case SW: l = c.borderSW;break;
        }
        return getBorderColorByLevel(l);
    }

    @Override
    public boolean isVisible(int x, int y) {
        TileCache c = getCache(x, y);
        return c.v != null;
    }

    @Override
    public Color getColor(int x, int y) {
        TileCache<T> c = getCache(x, y);
        return getColorByValue(c.v);
    }

    @Override
    public Color getBackground() {
        return Color.AQUAMARINE;
    }

    TileCache<T> getCache(int x, int y){
        if (cache == null) cache = new TileCache<>();
        if (cache.x == x && cache.y == y)
            return cache;
        cache.x = x;
        cache.y = y;

        T t = getGrid().getItem(x, y);
        if (t == null)
            cache.v = null;
        cache.v = t;

        cache.borderN = calcLevel(x, y, Dir.N);
        cache.borderS = calcLevel(x, y, Dir.S);
        cache.borderNE = calcLevel(x, y, Dir.NE);
        cache.borderSE = calcLevel(x, y, Dir.SE);
        cache.borderNW = calcLevel(x, y, Dir.NW);
        cache.borderSW = calcLevel(x, y, Dir.SW);

        return cache;
    }

    int calcLevel(int x, int y, Dir dir){
        T t = getGrid().getItem(x, y);
        Tile<T> tn = getGrid().getNeighbour(x, y, dir);
        if (t == null || tn == null || tn.getItem() == null || t.equals(tn.getItem()))
            return 0;
        T lca = getTree().getLCA(t, tn.getItem());
        if (lca == null) return 0;

        return getTree().getDepth(lca) + 1;
    }

    static class TileCache<T> {
        public T v;
        public int x;
        public int y;

        public int borderN;
        public int borderS;
        public int borderNE;
        public int borderSE;
        public int borderNW;
        public int borderSW;
    }
    TileCache<T> cache;

    protected double getBorderWidthByLevel(int l){
        return (4.0 - l)*(4.0 - l)/2;
    }
    protected Color getBorderColorByLevel(int l){
        return Color.BLACK;
    }
    protected Color getColorByValue(T v) {
        return Color.RED;
    }

}
