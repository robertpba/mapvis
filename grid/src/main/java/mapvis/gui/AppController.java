package mapvis.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.paint.*;
import javafx.scene.text.Text;
import mapvis.RandomData;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.HashMapGrid;
import mapvis.tree.MPTT;

import java.net.URL;
import java.util.*;
import java.util.function.Function;

public class AppController implements Initializable {

    @FXML
    public GridPanel gridPanel;

    @FXML
    public Slider zoomSlider;

    @FXML
    public Text panFactorX;

    @FXML
    public Text panFactorY;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(gridPanel.zoomFactorProperty());
        panFactorX.textProperty()
                .bind(gridPanel.panFactorXProperty().asString());
        panFactorY.textProperty()
                .bind(gridPanel.panFactorYProperty().asString());

        Platform.runLater(this::init);
    }



    public MPTT<Integer> tree;
    public HashMapGrid<Integer> grid;
    public CoastCache<Integer> cache;
    public Method1<Integer>  method1;

    public void init(){
        tree = RandomData.getTree();

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
        method1 = new Method1<>(tree, cache, grid);

        Set<Integer> leaves = tree.getLeaves();
        Map<Integer, Color> map = new HashMap();
        Random rand = new Random(1);
        System.out.printf("%d leaves\n", leaves.size());
        for (Integer leaf : leaves) {
            map.put(leaf, new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
        }
        gridPanel.colorMap = o -> map.get(o);
        Platform.runLater(this::begin);
    }

    public void begin(){
        method1.Begin();
        gridPanel.grid = grid;
        gridPanel.updateHexagons();
    }
}
