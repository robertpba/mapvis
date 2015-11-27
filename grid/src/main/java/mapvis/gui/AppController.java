package mapvis.gui;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import mapvis.Impl.HashMapGrid;
import mapvis.Impl.Region.RampRegionColorStyler;
import mapvis.Impl.Region.RandomRegionColorStyler;
import mapvis.Impl.Tile.RampColorStyler;
import mapvis.Impl.Tile.RandomColorStyler;
import mapvis.Impl.Tile.TileStylerBase;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Tree2;
import mapvis.models.ConfigurationConstants;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init AppController");
        tree.bindBidirectional(datasetSelectionController.tree);
        grid.bindBidirectional(datasetSelectionController.grid);
        tree.bindBidirectional(chartController.treeProperty());
        grid.bindBidirectional(chartController.gridProperty());

        if(ConfigurationConstants.USE_REGION_RENDERING){
            ObjectBinding db = createRegionStylerObjectBinder();
            chartController.chart.regionStylerProperty().bind(db);
        }else {
            ObjectBinding tileStylerObjectBinding = createTileStylerObjectBinding();
            chartController.chart.tileStylerProperty().bind(tileStylerObjectBinding);
        }

        datasetSelectionController.chart = chartController.chart;
        chartController.treeStatisticsProperty().bind(datasetSelectionController.lastTreeStatistics);
        tree.addListener((v, o, n) -> {
            TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree.get());
            TreeItem<INode> root = adapter.getRoot();
            root.setExpanded(true);
            treeTableView.setRoot(root);
        });


    }

    private ObjectBinding createTileStylerObjectBinding() {
        ObjectBinding db = new ObjectBinding() {
            {
                super.bind(
                        chartController.levelsToShowSlider.valueProperty(),
                        chartController.colorPicker.valueProperty(),
                        chartController.colorschemeProperty(),
                        chartController.bordersLevelsToShowSlider.valueProperty(),
                        tree,
                        grid
                );
            }
            private RandomColorStyler<INode> randomStyler;
            private RampColorStyler<INode> rampColorStyler;
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
                                1,
                                chartController.bordersLevelsToShowSlider.valueProperty().intValue());
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
                                chartController.colorPicker.valueProperty().get(),
                                chartController.bordersLevelsToShowSlider.valueProperty().intValue());
                    }
                    return rampColorStyler;
                }
                throw new RuntimeException();
            }
        };
        return db;
    }

    private ObjectBinding createRegionStylerObjectBinder() {
        return new ObjectBinding() {
                {
                    super.bind(
                            chartController.colorschemeProperty(),
                            tree,
                            grid
                    );
                }
                private RandomRegionColorStyler<INode> randomRegionStyler;
                private RampRegionColorStyler<INode> rampRegionStyler;

                private TileStylerBase.StylerUIElements createStylerUIElements(){
                    return new TileStylerBase.StylerUIElements(
                            chartController.colorPicker.valueProperty(),
                            chartController.bordersLevelsToShowSlider.valueProperty(),
                            chartController.levelsToShowSlider.valueProperty(),
                            chartController.labelLevelsToShowSlider.valueProperty(),
                            chartController.showLabelsCheckBox.selectedProperty()
                    );
                }

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
                        if(randomRegionStyler == null){
                            randomRegionStyler = new RandomRegionColorStyler<>(
                                    tree, createStylerUIElements()
                                    , 1);
                        }
                        return randomRegionStyler;
                    }

                    if (s.equals("ramp")){
                        if(rampRegionStyler == null){
                            rampRegionStyler = new RampRegionColorStyler<>(
                                    tree, createStylerUIElements());
                        }
                        return rampRegionStyler;
                    }

                    throw new RuntimeException();
                }
            };
    }
}
