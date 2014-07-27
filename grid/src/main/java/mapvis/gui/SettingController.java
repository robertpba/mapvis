package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import mapvis.Impl.TileStylerImpl;
import mapvis.Impl.TreeModel;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.graphic.TileStyler;
import mapvis.grid.Grid;
import mapvis.Impl.HashMapGrid;
import mapvis.graphic.HexagonalTilingView;
import utils.RandomTreeGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class SettingController {
    @FXML
    public TextField weightField;
    @FXML
    public TextField depthField;
    @FXML
    public TextField spanField;
    @FXML
    public TextField seedField;
    @FXML
    public TextArea infoArea;

    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<CoastCache<Integer>> cache = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<Integer>> method1 = new SimpleObjectProperty<>();
    public ObjectProperty<TileStyler<Integer>> tileStyler = new SimpleObjectProperty<>();

    public HexagonalTilingView chart;


    @FXML
    public void begin(ActionEvent event) {
        method1.get().Begin();
        chart.updateHexagons();
    }

    @FXML
    public void generateTree(ActionEvent event) {
        int span = 10, weight = 100, depth = 3, seed = 1;
        try {
            span = Integer.parseInt(spanField.getText());
        }
        catch (NumberFormatException ignored) { }
        try {
            weight = Integer.parseInt(weightField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            depth = Integer.parseInt(depthField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            seed = Integer.parseInt(seedField.getText());
        }
        catch (NumberFormatException ignored) {  }

        RandomTreeGenerator gen = new RandomTreeGenerator(seed);
        tree.set(gen.getTree(depth, span, weight));
        grid.set(new HashMapGrid<>());
        cache.set(new CoastCache<>(grid.get(), tree.get()));
        method1.set(new Method1<>(tree.get(), cache.get(), grid.get()));
        Set<Integer> leaves = tree.get().getLeaves();
        Map<Integer, Color> map = new HashMap<>();
        Random rand = new Random(seed);
        infoArea.setText(String.format("%d leaves\n", leaves.size()));

        tileStyler.set(new TileStylerImpl<Integer>(tree.get(), grid.get()){
            @Override
            protected Color getColorByValue(Integer v) {
                return map.get(v);
            }
        });

        for (Integer leaf : leaves) {
            map.put(leaf, new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
        }
    }


}