package mapvis.tree;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.testng.Assert.*;

public class MPTTTest {

    @BeforeMethod
    public void setUp() throws Exception {
    }



    @Test
    public void testGetNullRoot() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        Assert.assertEquals(tree.getRoot(), null);
    }

    @Test
    public void testSetRoot() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        tree.setRoot(1);
        Assert.assertEquals(tree.getRoot(), (Integer)1);
    }

    @Test
    public void testGetPath() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 0);
        tree.addChild(1, 3, 0);
        tree.addChild(2, 5, 0);

        tree.refresh();

        List<Integer> list = tree.getPathToNode(5);

        assertEquals(list.size(), 3);
        assertEquals(list.get(0).intValue(), 1);
        assertEquals(list.get(1).intValue(), 2);
        assertEquals(list.get(2).intValue(), 5);
    }

    @Test
    public void testGetDepth() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 0);
        tree.addChild(1, 3, 0);
        tree.addChild(2, 5, 0);

        tree.refresh();

        assertEquals(tree.getDepth(1), 0);
        assertEquals(tree.getDepth(2), 1);
        assertEquals(tree.getDepth(3), 1);
        assertEquals(tree.getDepth(5), 2);
    }

    @Test
    public void testAddChild() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 0);
        tree.addChild(1, 3, 0);
        tree.addChild(2, 5, 0);

        Set<Integer> children1 = tree.getChildren(1);
        Set<Integer> expected1 = new HashSet<>();
        expected1.add(2);
        expected1.add(3);

        assertEquals(children1, expected1);

        Set<Integer> children2 = tree.getChildren(2);
        Set<Integer> expected2 = new HashSet<>();
        expected2.add(5);

        assertEquals(children2, expected2);
    }

    @Test
    public void testRefresh() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 0);
        tree.addChild(1, 3, 0);
        tree.addChild(2, 5, 0);

        tree.refresh();

        Set<Integer> leaves = tree.getLeaves();
        Set<Integer> expected = new HashSet<>();
        expected.add(3);
        expected.add(5);

        assertEquals(leaves,expected);
    }

    @Test
    public void testGetLCA() throws Exception {
        MPTT<Integer> tree = new MPTT<>();
        tree.setRoot(1);
        tree.addChild(1, 2, 0);
        tree.addChild(1, 3, 0);
        tree.addChild(2, 5, 0);

        tree.refresh();

        assertEquals(tree.getLCA(2, 5), (Integer)2);
        assertEquals(tree.getLCA(3, 5), (Integer)1);
        assertEquals(tree.getLCA(4, 5), null);
    }
}