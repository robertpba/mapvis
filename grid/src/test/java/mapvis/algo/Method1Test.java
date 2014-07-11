package mapvis.algo;

import mapvis.grid.Grid;
import mapvis.grid.HashMapGrid;
import mapvis.tree.MPTT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Method1Test {
    private MPTT<Integer> tree;
    private CoastCache<Integer> cache;
    private Grid<Integer> grid;

    @BeforeMethod
    public void setUp() throws Exception {
        tree = new MPTT<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 10);
        tree.addChild(1, 3, 10);
        tree.addChild(2, 5, 10);

        //       1
        //     /  \
        //    2    3
        //    |
        //    5

        tree.refresh();

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
    }

    @Test
    public void testBegin() throws Exception {
        Method1<Integer> method1 = new Method1<>(tree, cache, grid);
        method1.Begin();

    }
}