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
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.TileStyler;
import mapvis.models.Grid;

import java.net.URL;
import java.util.ResourceBundle;


public class AppController implements Initializable {

    @FXML
    public TreeTableView<Node> treeTableView;

    @FXML
    private DatesetSelectionController datesetSelectionController;

    @FXML
    private RandomTreeSettingsController randomTreeSettingController;

    @FXML
    public ChartController chartController;

    public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<TileStyler<Node>> tileStyler = new SimpleObjectProperty<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init AppController");
        tree.bindBidirectional(datesetSelectionController.tree);
        grid.bindBidirectional(datesetSelectionController.grid);
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
                    tree.set(new MPTreeImp<>());
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

        datesetSelectionController.chart = chartController.chart;

        tree.addListener((v, o, n)-> {
            TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree.get());
            TreeItem<Node> root = adapter.getRoot();
            root.setExpanded(true);
            treeTableView.setRoot(root);
        });


    }
}
