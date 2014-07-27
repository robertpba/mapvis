package mapvis.Impl;

import javafx.scene.paint.Color;
import mapvis.grid.Grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomColorStyler<T> extends TileStylerBase<T> {
    Map<T, Color> map = new HashMap<>();
    Random rand;

    public RandomColorStyler(TreeModel<T> tree, Grid<T> grid, int seed) {
        super(tree, grid);
        this.rand = new Random(seed);

        Set<T> leaves = tree.getLeaves();
        for (T leaf : leaves) {
            map.put(leaf, new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
        }
    }
    public RandomColorStyler(TreeModel tree, Grid grid) {
        this(tree, grid, 0);
    }

    @Override
    protected Color getColorByValue(T v) {
        return map.get(v);
    }
}
