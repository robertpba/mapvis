package mapvis.algo;

import mapvis.models.Grid;
import mapvis.Impl.HashMapGrid;
import mapvis.models.Tile;
import mapvis.Impl.MPTree;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

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
        grid.putItem(2, 4, 3);
        cache.insert(2, 4, 3);
        Set<Tile<Integer>> edges = cache.getEdge(3);

        List<Tile<Integer>> expected = Arrays.asList(new Tile<>(2, 4, 3));

        assertEquals(edges, expected);

        edges = cache.getEdge(1);
        assertEquals(edges, expected);
    }

    @Test
    public void testInsertAffect2() throws Exception {
        grid.putItem(2, 4, 5);
        cache.insert(2, 4, 5);

        List<Tile<Integer>> expected = Arrays.asList(new Tile<>(2, 4, 3));

        Set<Tile<Integer>> set = cache.getEdge(5);
        assertEquals(set, expected);

        set = cache.getEdge(2);
        assertEquals(set, expected);

        set = cache.getEdge(1);
        assertEquals(set, expected);

        set = cache.getEdge(5);
        assertEquals(set, expected);

        set = cache.getEdge(3);
        assertEquals(set.size(), 0);
    }


    @Test
    public void testInsertAffect3() throws Exception {
        grid.putItem(2, 4, 5);
        cache.insert(2, 4, 5);

        grid.putItem(2, 5, 2);
        cache.insert(2, 5, 2);

        //       <2,4,"5">
        // <1,4>           <3,4>
        //       <2,5,"2">

        Set<Tile<Integer>> set;

        set = cache.getEdge(5);
        assertEquals(set.size(), 1);

        assertTrue(set.contains(new Tile<>(2, 4, 5)));

        set = cache.getEdge(2);
        assertEquals(set.size(), 2);
        assertTrue(set.contains(new Tile<>(2, 4, 5)));
        assertTrue(set.contains(new Tile<>(2, 5, 2)));

        set = cache.getEdge(3);
        assertEquals(set.size(), 0);
    }



    @Test
    public void testInsertWater1() throws Exception {
        grid.putItem(2, 4, 3);
        cache.insert(2, 4, 3);
        Set<Tile<Integer>> waters = cache.getWaters(3);

        Set<Tile<Integer>> expected = Sets.newHashSet(Arrays.asList(
                new Tile<>(2, 3, null),
                new Tile<>(2, 5, null),
                new Tile<>(1, 3, null),
                new Tile<>(1, 4, null),
                new Tile<>(3, 3, null),
                new Tile<>(3, 4, null)
        ));

        assertEquals(waters, expected);

        //waters = cache.getEdge(1);
        //assertEquals(waters, expected);
    }

    @Test
    public void testInsertWater2() throws Exception {
        grid.putItem(2, 4, 3);
        cache.insert(2, 4, 3);
        grid.putItem(2, 3, 3);
        cache.insert(2, 3, 3);
        Set<Tile<Integer>> waters = cache.getWaters(3);

        Set<Tile<Integer>> expected = Sets.newHashSet(Arrays.asList(
                new Tile<>(2, 2, null),
                new Tile<>(2, 5, null),
                new Tile<>(1, 2, null),
                new Tile<>(1, 3, null),
                new Tile<>(1, 4, null),
                new Tile<>(3, 2, null),
                new Tile<>(3, 3, null),
                new Tile<>(3, 4, null)
        ));

        assertEquals(waters, expected);

        //waters = cache.getEdge(1);
        //assertEquals(waters, expected);
    }



}