package mapvis.Impl;

import javafx.scene.paint.Color;
import mapvis.grid.Grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomColorStyler<T> extends TileStylerBase<T> {
    public int level;
    public int depth;
    Map<T, Color> map = new HashMap<>();
    Random rand;
    private Color background;

    public RandomColorStyler(TreeModel<T> tree, Grid<T> grid, int level, Color background, int seed) {
        super(tree, grid);
        this.background = background;
        this.rand = new Random(seed);
        this.level = level;

        rec(tree.getRoot(), null);
    }

    void rec(T leaf, Color color){
        if (leaf == null)
            return;
        if (tree.getDepth(leaf) <= level)
            color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0);
        else
            new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0);

        map.put(leaf, color);

        for (T child : tree.getChildren(leaf)) {
            rec(child, color);
        }
        depth = Math.max(tree.getDepth(leaf), depth);
    }


    public RandomColorStyler(TreeModel tree, Grid grid) {
        this(tree, grid, 100, Color.AQUAMARINE, 0);
    }

    @Override
    protected Color getColorByValue(T v) {
        return map.get(v);
    }

    @Override
    protected double getBorderWidthByLevel(int l) {
        return (depth + 1 - l)*(depth + 1 - l)/2;
    }

    @Override
    public Color getBackground() {
        return background;
    }
}
