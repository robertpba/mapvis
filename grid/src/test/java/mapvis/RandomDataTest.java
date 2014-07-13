package mapvis;

import mapvis.tree.MPTT;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class RandomDataTest {

    @Test
    public void testGetTree() throws Exception {
        MPTT<Integer> tree = RandomData.getTree();
    }
}