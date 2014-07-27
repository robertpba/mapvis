package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import mapvis.Impl.TreeModel;
import mapvis.grid.Grid;

import java.net.URL;
import java.util.ResourceBundle;


public class AppController implements Initializable {

    @FXML
    public TreeTableView<TreeTableViewModelAdapter.Node> treeTableView;

    @FXML
    public SettingController settingController;

    @FXML
    public ChartController chartController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tree.bindBidirectional(settingController.tree);
        grid.bindBidirectional(settingController.grid);
        tree.bindBidirectional(chartController.tree);
        grid.bindBidirectional(chartController.grid);

        chartController.chart.stylerProperty().bindBidirectional(settingController.tileStyler);

        settingController.chart = chartController.chart;

        tree.addListener((v, o, n)-> {
            TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree.get());
            TreeItem<TreeTableViewModelAdapter.Node> root = adapter.getRoot();
            root.setExpanded(true);
            treeTableView.setRoot(root);
        });
    }

    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty<>();



}
