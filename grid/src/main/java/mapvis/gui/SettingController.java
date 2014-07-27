package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import mapvis.Impl.RandomColorStyler;
import mapvis.Impl.TileStylerBase;
import mapvis.Impl.TreeModel;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.graphic.TileStyler;
import mapvis.grid.Grid;
import mapvis.Impl.HashMapGrid;
import mapvis.graphic.HexagonalTilingView;
import utils.RandomTreeGenerator;

import java.net.URL;
import java.util.*;

public class SettingController implements Initializable {
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
    public StringProperty colorscheme = new SimpleStringProperty();

    public HexagonalTilingView chart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void onChooseRandomColor(ActionEvent event){
        colorscheme.set("random");
    }
    @FXML
    public void onChooseLevel1(ActionEvent event){
        colorscheme.set("level1");
    }
    @FXML
    public void onChooseLevel2(ActionEvent event){
        colorscheme.set("level2");
    }
    @FXML
    public void onChooseLevel3(ActionEvent event){
        colorscheme.set("level3");
    }
    @FXML
    public void onChooseLevel4(ActionEvent event){
        colorscheme.set("level4");
    }

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
        infoArea.setText(String.format("%d leaves\n", leaves.size()));

    }


}
