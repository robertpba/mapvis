package mapvis.algo;

import mapvis.grid.Grid;
import mapvis.Impl.HashMapGrid;
import mapvis.grid.Tile;
import mapvis.tree.MPTree;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.*;

public class CoastCacheTest {
    private MPTree<Integer> tree;
    private CoastCache<Integer> cache;
    private Grid<Integer> grid;

    @BeforeMethod
    public void setUp() throws Exception {
        tree = new MPTree<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 0);
        tree.addChild(1, 3, 0);
        tree.addChild(2, 5, 0);

        //       1
        //     /  \
        //    2    3
        //    |
        //    5


        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
    }

    @Test
    public void testInsertAffect1() throws Exception {
        grid.put(2,4, 3);
        cache.insertAffect(2, 4, 3);
        Set<Tile<Integer>> set = cache.getCoast(3);
        assertEquals(set.size(), 1);

        set = cache.getCoast(1);
        assertEquals(set.size(), 1);
    }

    @Test
    public void testInsertAffect2() throws Exception {
        grid.put(2,4, 5);
        cache.insertAffect(2, 4, 5);
        Set<Tile<Integer>> set = cache.getCoast(5);
        assertEquals(set.size(), 1);

        set = cache.getCoast(2);
        assertEquals(set.size(), 1);

        set = cache.getCoast(1);
        assertEquals(set.size(), 1);

        set = cache.getCoast(5);
        assertEquals(set.size(), 1);

        set = cache.getCoast(3);
        assertEquals(set.size(), 0);
    }


    @Test
    public void testInsertAffect3() throws Exception {
        grid.put(2,4, 5);
        cache.insertAffect(2, 4, 5);

        grid.put(2,5, 2);
        cache.insertAffect(2, 5, 2);

        //       <2,4,"5">
        // <1,4>           <3,4>
        //       <2,5,"2">

        Set<Tile<Integer>> set;

        set = cache.getCoast(5);
        assertEquals(set.size(), 1);
        assertTrue(set.contains(new Tile<>(2, 4, 5)));

        set = cache.getCoast(2);
        assertEquals(set.size(), 2);
        assertTrue(set.contains(new Tile<>(2, 4, 5)));
        assertTrue(set.contains(new Tile<>(2, 5, 2)));

        set = cache.getCoast(3);
        assertEquals(set.size(), 0);
    }

}