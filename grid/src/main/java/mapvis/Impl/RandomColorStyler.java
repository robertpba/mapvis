package mapvis.Impl;

import javafx.scene.paint.Color;
import mapvis.common.datatype.Tree2;
import mapvis.models.Grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomColorStyler<T> extends TileStylerBase<T> {
    public int maxHexagonLevelToShow;
    public int depth;
    Map<T, Color> map = new HashMap<>();
    Random rand;
    private Color background;

    public RandomColorStyler(Tree2<T> tree, Grid<T> grid, int maxHexagonLevelToShow, Color background, int seed) {
        super(tree, grid);
        initSyler(maxHexagonLevelToShow, background, seed);
        System.out.println("Creating: " + this.getClass().getName());
    }

    public RandomColorStyler(Tree2 tree, Grid grid) {
        this(tree, grid, 100, Color.AQUAMARINE, 0);
    }

    private void initSyler(int level, Color background, int seed){
        this.background = background;
        this.rand = new Random(seed);
        this.maxHexagonLevelToShow = level;
        rec(tree.getRoot(), null);
    }

    public void resetStyler(Tree2<T> tree, Grid<T> grid, int level, Color background, int seed){
        super.resetStyler(tree, grid);
        initSyler(level, background, seed);
    }

    private void rec(T leaf, Color color){
        if (leaf == null)
            return;
        if (tree.getDepth(leaf) <= maxHexagonLevelToShow)
            color = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0);
        else
            new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0);

        map.put(leaf, color);

        for (T child : tree.getChildren(leaf)) {
            rec(child, color);
        }
        depth = Math.max(tree.getDepth(leaf), depth);
    }

    @Override
    protected Color getColorByValue(T v) {
        return map.get(v);
    }

    @Override
    protected double getBorderWidthByLevel(int l) {
        return  Math.pow(depth + 1 - l, 1.2)/2.0;
    }

    @Override
    public Color getBackground() {
        return background;
    }

}
