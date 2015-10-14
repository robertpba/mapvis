package mapvis;

import javafx.beans.binding.ObjectBinding;
import mapvis.Impl.HashMapGrid;
import mapvis.Impl.RampColorStyler;
import mapvis.Impl.RandomColorStyler;
import mapvis.algo.Method1;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.gui.DatesetSelectionController;
import mapvis.models.Grid;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.RandomTreeGenerator;

import java.util.Set;

public class RandomTreeGeneratorTest {
    private static final int DEFAULT_SEED = 1;
    private static final int DEFAULT_WEIGHT = 100;
    private static final int DEFAULT_DEPTH = 3;
    private static final int DEFAULT_SPAN = 10;

    DatesetSelectionController controller;
    HexagonalTilingView chart;

    @BeforeMethod
    public void setup(){

    }

    void generateTree(){

    }
    @Test
    public void testGetTree() throws Exception {
        controller = new DatesetSelectionController();
        chart = new HexagonalTilingView();

        //generateTree
        RandomTreeGenerator randomTreeGenerator = new RandomTreeGenerator(DEFAULT_SEED);
        Tree2<Node> genTree = randomTreeGenerator.getTree(DEFAULT_DEPTH, DEFAULT_SPAN, DEFAULT_WEIGHT);

        Grid<Node> grid = new HashMapGrid<>();
        Method1<Node> method1 = new Method1<>(genTree, grid);

        Set<Node> leaves = genTree.getLeaves();
        System.out.println(String.format("%d leaves\n", leaves.size()));

        //Begin
        method1.Begin();
        chart.updateHexagons();


        randomTreeGenerator = new RandomTreeGenerator(DEFAULT_SEED);
        genTree = randomTreeGenerator.getTree(DEFAULT_DEPTH, DEFAULT_SPAN, DEFAULT_WEIGHT);

        grid = new HashMapGrid<>();
        method1 = new Method1<>(genTree, grid);

        leaves = genTree.getLeaves();
        System.out.println(String.format("%d leaves\n", leaves.size()));
    }
}