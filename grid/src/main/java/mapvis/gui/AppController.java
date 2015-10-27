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
import mapvis.common.datatype.INode;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.TileStyler;
import mapvis.models.Grid;

import java.net.URL;
import java.util.ResourceBundle;


public class AppController implements Initializable {

    @FXML
    public TreeTableView<INode> treeTableView;

    @FXML
    private DatasetSelectionController datasetSelectionController;

    @FXML
    private RandomTreeSettingsController randomTreeSettingController;

    @FXML
    public ChartController chartController;

    public ObjectProperty<Tree2<INode>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<INode>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<TileStyler<INode>> tileStyler = new SimpleObjectProperty<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init AppController");
        tree.bindBidirectional(datasetSelectionController.tree);
        grid.bindBidirectional(datasetSelectionController.grid);
        tree.bindBidirectional(chartController.treeProperty());
        grid.bindBidirectional(chartController.gridProperty());

        ObjectBinding db = new ObjectBinding() {
            {
                super.bind(chartController.levelsToShowSlider.valueProperty(),
                        chartController.colorPicker.valueProperty(),
                        chartController.colorschemeProperty(),
                        tree,
                        grid
                );
            }

            private RandomColorStyler randomStyler;
            private RampColorStyler rampColorStyler;
            @Override
            protected Object computeValue() {
                System.out.println("requesting styler");
                if (tree.get() == null)
                    tree.set(new MPTreeImp<>());
                if (grid.get() == null)
                    grid.set(new HashMapGrid<>());

                String s = chartController.colorschemeProperty().get();
                if (s == null) s = "random";

                if (s.equals("random")){
                    if(randomStyler == null){
                        randomStyler = new RandomColorStyler<>(tree.get(), grid.get(),
                                (int) chartController.levelsToShowSlider.valueProperty().get(),
                                chartController.colorPicker.valueProperty().get(),
                                1);
                    }else{
                        randomStyler.resetStyler(tree.get(), grid.get(),
                                (int) chartController.levelsToShowSlider.valueProperty().get(),
                                chartController.colorPicker.valueProperty().get(),
                                1);
                    }
                    return randomStyler;
                }

                if (s.equals("ramp")){
                    if(rampColorStyler == null){
                        rampColorStyler = new RampColorStyler<>(tree.get(), grid.get(),
                                (int) chartController.levelsToShowSlider.valueProperty().get(),
                                chartController.colorPicker.valueProperty().get());
                    }else{
                        rampColorStyler.resetStyler(tree.get(), grid.get(),
                                (int) chartController.levelsToShowSlider.valueProperty().get(),
                                chartController.colorPicker.valueProperty().get());
                    }
                    return rampColorStyler;
                }
                throw new RuntimeException();
            }
        };
        //tileStyler.bind(db);

        chartController.chart.stylerProperty().bind(db);

        datasetSelectionController.chart = chartController.chart;
        chartController.treeStatisticsProperty().bind(datasetSelectionController.lastTreeStatistics);
        tree.addListener((v, o, n) -> {
            TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree.get());
            TreeItem<INode> root = adapter.getRoot();
            root.setExpanded(true);
            treeTableView.setRoot(root);
        });


    }
}
