package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import utils.RandomTreeGenerator;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.HashMapGrid;
import mapvis.grid.jfx.HexagonalTilingView;
import mapvis.Impl.TreeModel;

import java.net.URL;
import java.util.*;


public class AppController implements Initializable {
    @FXML
    public HexagonalTilingView chart;

    @FXML
    public Slider zoomSlider;

    @FXML
    public Text originX;

    @FXML
    public Text originY;

    @FXML
    public TreeTableView<TreeTableViewModelAdapter.Node> treeTableView;

    @FXML
    public SettingController settingController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        originX.textProperty()
                .bind(chart.originXProperty().asString());
        originY.textProperty()
                .bind(chart.originXProperty().asString());

        tree.bindBidirectional(settingController.tree);
        grid.bindBidirectional(settingController.grid);

        chart.setOnMouseClicked(e -> {
            Point2D pl = chart.localToPlane(e.getX(), e.getY());
            Point2D point = chart.planeToHexagonal(pl.getX(), pl.getY());
            Integer id = grid.get().get((int)point.getX(), (int)point.getY());
            if (id == null)
                return;

            StringBuilder sb = new StringBuilder();
            tree.get().getPathToNode(id).forEach(i-> sb.append(">").append(i));

            System.out.printf("id:%s, weight:%d %s\n", id, tree.get().getWeight(id), sb.toString());
        });

        chart.colorMapProperty().bindBidirectional(settingController.colorMap);;

        settingController.chart = chart;

        tree.addListener((v, o, n)-> {
            chart.tree = n;
            //chart.updateHexagons();
        });
        grid.addListener((v, o, n)-> {
            chart.grid = n;
            chart.updateHexagons();
        });

        tree.addListener((v, o, n)-> {
            TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree.get());
            TreeItem<TreeTableViewModelAdapter.Node> root = adapter.getRoot();
            root.setExpanded(true);
            treeTableView.setRoot(root);
        });

    }

    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<HashMapGrid<Integer>> grid = new SimpleObjectProperty<>();


    @FXML
    public void reset(ActionEvent event) {
        chart.zoomTo(1.0);
        chart.scrollTo(0,0);
    }


}
