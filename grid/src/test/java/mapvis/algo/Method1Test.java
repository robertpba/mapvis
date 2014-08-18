package mapvis.algo;

import mapvis.models.Grid;
import mapvis.Impl.HashMapGrid;
import mapvis.Impl.MPTree;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Method1Test {
    private MPTree<Integer> tree;
    private CoastCache<Integer> cache;
    private Grid<Integer> grid;

    @BeforeMethod
    public void setUp() throws Exception {
        tree = new MPTree<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 10);
        tree.addChild(1, 3, 10);
        tree.addChild(2, 5, 10);

        //       1
        //     /  \
        //    2    3
        //    |
        //    5


        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
    }

    @Test
    public void testBegin() throws Exception {
        Method1<Integer> method1 = new Method1<>(tree, grid);
        method1.Begin();

    }
}