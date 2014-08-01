package mapvis.gui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import mapvis.Impl.HashMapGrid;
import mapvis.Impl.RampColorStyler;
import mapvis.Impl.RandomColorStyler;
import mapvis.models.TreeModel;
import mapvis.graphic.TileStyler;
import mapvis.models.Grid;
import mapvis.Impl.MPTree;

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

        ObjectBinding db = new ObjectBinding() {
            {
                super.bind(chartController.levelChoiceBox.valueProperty(),
                        chartController.colorPicker.valueProperty(),
                        chartController.colorscheme,
                        tree, grid);
            }
            @Override
            protected Object computeValue() {
                if (tree.get() == null)
                    tree.set(new MPTree<>());
                if (grid.get() == null)
                    grid.set(new HashMapGrid<>());

                String s = chartController.colorscheme.get();
                if (s == null) s = "random";

                if (s.equals("random"))
                    return new RandomColorStyler<>(tree.get(), grid.get(),
                        chartController.levelChoiceBox.valueProperty().get(),
                        chartController.colorPicker.valueProperty().get(),
                        1);
                if (s.equals("ramp")){
                    return new RampColorStyler<>(tree.get(), grid.get(),
                            chartController.levelChoiceBox.valueProperty().get(),
                            chartController.colorPicker.valueProperty().get());
                }
                throw new RuntimeException();
            }
        };
        //tileStyler.bind(db);

        chartController.chart.stylerProperty().bind(db);

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
    public ObjectProperty<TileStyler<Integer>> tileStyler = new SimpleObjectProperty<>();



}
