package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import mapvis.Drawer;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.TreeStatistics;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.ConfigurationConstants;
import mapvis.models.Grid;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This Controller is responsible for the Hexagon-Region visualization.
 * It delegates the UI controls to the @HexagonalTilingView which is responsible
 * for rendering of the visualization.
 */
public class ChartController implements Initializable  {
    @FXML
    public HexagonalTilingView chart;
    @FXML
    public Slider zoomSlider;
    @FXML
    public Text originX;
    @FXML
    public Text originY;
    @FXML
    public ColorPicker colorPicker;

    @FXML
    public Slider labelLevelsToShowSlider;
    @FXML
    public Slider bordersLevelsToShowSlider;
    @FXML
    public Slider levelsToShowSlider;

    @FXML
    private ComboBox<ConfigurationConstants.SimplificationMethod> simplificationMethodComboBox;
    @FXML
    public ComboBox<ConfigurationConstants.RenderingMethod> renderingMethodComboBox;
    @FXML
    private CheckBox HQDouglasPeuckerSimplifCheckBox;
    @FXML
    private Slider douglasPeuckerToleranceSlider;

    @FXML
    private ChoiceBox labelLevelsToShowChoiceBox;
    @FXML
    protected CheckBox showLabelsCheckBox;

    private ObjectProperty<Tree2<INode>> tree = new SimpleObjectProperty<>();
    private ObjectProperty<Grid<INode>> grid = new SimpleObjectProperty<>();
    private StringProperty colorscheme = new SimpleStringProperty();
    private ObjectProperty<TreeStatistics> treeStatistics = new SimpleObjectProperty<>();

    public ChartController() {
        System.out.println("Creating: " + this.getClass().getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        StringConverter<Number> sc = new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object != null) {
                    return Integer.toString((int) Math.round(object.doubleValue()));
                }else
                    return null;
            }

            @Override
            public Number fromString(String string) {
                Double d = Double.parseDouble(string);
                return d;
            }
        };
        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        originX.textProperty()
                .bindBidirectional(chart.originXProperty(), sc);
        originY.textProperty()
                .bindBidirectional(chart.originYProperty(), sc);
        showLabelsCheckBox.selectedProperty()
                .bindBidirectional(chart.areLabelsShownProperty());
        bordersLevelsToShowSlider.valueProperty()
                .bindBidirectional(chart.maxLevelOfBordersToShowProperty());
        labelLevelsToShowSlider.valueProperty()
                .bindBidirectional(chart.maxLevelOfLabelsToShowProperty());
        levelsToShowSlider.valueProperty()
                .bindBidirectional(chart.maxLevelOfRegionsToShowProperty());
        douglasPeuckerToleranceSlider.valueProperty()
                .bindBidirectional(chart.simplificationToleranceProperty());
        HQDouglasPeuckerSimplifCheckBox.selectedProperty()
                .bindBidirectional(chart.useHQDouglasSimplificationProperty());
        grid.bindBidirectional(chart.gridProperty());
        tree.bindBidirectional(chart.treeProperty());

        chart.setOnMouseClicked(e -> {
            Point2D pl = chart.localToPlane(e.getX(), e.getY());
            Point2D point = chart.planeToHexagonal(pl.getX(), pl.getY());
            INode node = grid.get().getItem((int) point.getX(), (int) point.getY());
            if (node == null)
                return;

            StringBuilder sb = new StringBuilder();
            tree.get().getPathToNode(node).forEach(i-> sb.append(">").append(i.getLabel()));

            System.out.printf("%s node:%s, weight:%d %s\n", point, node.getLabel(), tree.get().getWeight(node), sb.toString());
        });
        
        treeStatistics.addListener((observable2, oldValue1, newValue1) -> {
            if(newValue1 == null)
                return;

            int maxDepth = newValue1.maxDepth;
            labelLevelsToShowSlider.setMax(maxDepth);
            bordersLevelsToShowSlider.setMax(maxDepth);
            levelsToShowSlider.setMax(maxDepth);
        });
        renderingMethodComboBox.valueProperty().bindBidirectional(chart.renderingMethodProperty());
        for (ConfigurationConstants.RenderingMethod renderingMethod : ConfigurationConstants.RenderingMethod.values()) {
            renderingMethodComboBox.getItems().add(renderingMethod);
        }
        renderingMethodComboBox.getSelectionModel().select(ConfigurationConstants.RENDERING_METHOD_DEFAULT);
        simplificationMethodComboBox.valueProperty().bindBidirectional(chart.simplificationMethodProperty());
        for (ConfigurationConstants.SimplificationMethod simplificationMethod : ConfigurationConstants.SIMPLIFICATION_METHOD_DEFAULT.values()) {
            simplificationMethodComboBox.getItems().add(simplificationMethod);
        }
        simplificationMethodComboBox.valueProperty().addListener(this::onSimplificationSelectionChanged);
        simplificationMethodComboBox.getSelectionModel().select(ConfigurationConstants.SIMPLIFICATION_METHOD_DEFAULT);


        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> chart.updateHexagons());
    }

    private void onSimplificationSelectionChanged(ObservableValue<? extends ConfigurationConstants.SimplificationMethod> observable,
                                                       ConfigurationConstants.SimplificationMethod oldValue, ConfigurationConstants.SimplificationMethod newValue){
        if(newValue == ConfigurationConstants.SimplificationMethod.DouglasPeucker){
            HQDouglasPeuckerSimplifCheckBox.setVisible(true);
            douglasPeuckerToleranceSlider.setVisible(true);
        }else{
            HQDouglasPeuckerSimplifCheckBox.setVisible(false);
            douglasPeuckerToleranceSlider.setVisible(false);
        }
    }
    @FXML
    public void reset(ActionEvent event) {
        chart.zoomTo(1.0);
        chart.scrollTo(0,0);
    }

    @FXML
    public void save(ActionEvent event) throws IOException {

//        long startTime = System.currentTimeMillis();
//        chart.save("CanvasImage.png");
//        long estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.printf("sav: %d",estimatedTime);

        Drawer drawer = new Drawer(grid.get(), tree.get());
        drawer.export();


    }

    @FXML
    public void onChooseRandom(ActionEvent event){
        colorscheme.set("random");
    }

    @FXML
    public void onChooseRamp(ActionEvent event){
        colorscheme.set("ramp");
    }

    public TreeStatistics getTreeStatistics() {return treeStatistics.get(); }
    public ObjectProperty<TreeStatistics> treeStatisticsProperty() {return treeStatistics;  }
    public void setTreeStatistics(TreeStatistics treeStatistics) {this.treeStatistics.set(treeStatistics);  }

    public Grid<INode> getGrid() {
        return grid.get();
    }
    public ObjectProperty<Grid<INode>> gridProperty() {
        return grid;
    }
    public void setGrid(Grid<INode> grid) {
        this.grid.set(grid);
    }

    public String getColorscheme() {
        return colorscheme.get();
    }
    public StringProperty colorschemeProperty() {
        return colorscheme;
    }
    public void setColorscheme(String colorscheme) {
        this.colorscheme.set(colorscheme);
    }

    public Tree2<INode> getTree() {
        return tree.get();
    }
    public ObjectProperty<Tree2<INode>> treeProperty() {
        return tree;
    }

    public void setTree(Tree2<INode> tree) {
        this.tree.set(tree);
    }

    public void onSimplificationMethodChanged(ActionEvent event) {
    }

    public void onRenderingMethodChanged(ActionEvent event) {
    }
}
