import mapvis.grid.HashMapGrid;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HashMapGridTest {


    @Test
    public void test1() throws Exception {
        HashMapGrid grid = new HashMapGrid();
        Assert.assertNull(grid.get(0, 0));
    }

    @Test
    public void test2() throws Exception {
        HashMapGrid grid = new HashMapGrid();
        grid.put(0, 0, 1);
        Object o = grid.get(0, 0);
        Assert.assertEquals(o, 1);
    }

    @Test
    public void test3() throws Exception {
        HashMapGrid grid = new HashMapGrid();
        grid.put(0, 0, 1);
        grid.put(0, 0, null);
        Object o = grid.get(0, 0);
        Assert.assertNull(grid.get(0, 0));
    }
}
