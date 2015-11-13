package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import mapvis.Drawer;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;
import mapvis.common.datatype.TreeStatistics;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.Grid;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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

        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        originX.textProperty()
                .bind(chart.originXProperty().asString());
        originY.textProperty()
                .bind(chart.originXProperty().asString());
        showLabelsCheckBox.selectedProperty()
                .bindBidirectional(chart.areLabelsShownProperty());
        bordersLevelsToShowSlider.valueProperty()
                .bindBidirectional(chart.maxLevelOfBordersToShowProperty());
        labelLevelsToShowSlider.valueProperty()
                .bindBidirectional(chart.maxLevelOfLabelsToShowProperty());
        levelsToShowSlider.valueProperty()
                .bindBidirectional(chart.maxLevelOfRegionsToShowProperty());
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

//        levelsToShowSlider.valueProperty().addListener((observable1, oldValue, newValue) -> chart.updateHexagons());
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> chart.updateHexagons());
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
}
