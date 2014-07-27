package mapvis;

import mapvis.tree.TreeModel;
import org.testng.annotations.Test;

public class RandomDataTest {

    @Test
    public void testGetTree() throws Exception {
        TreeModel<Integer> tree = RandomData.getTree();
    }
}