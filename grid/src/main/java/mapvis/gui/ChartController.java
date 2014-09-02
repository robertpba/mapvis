package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Text;
import mapvis.models.TreeModel;
import mapvis.models.Grid;
import mapvis.graphic.HexagonalTilingView;
import utils.Node;

import javax.imageio.ImageIO;
import java.io.File;
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
    public ChoiceBox<Integer> levelChoiceBox;
    @FXML
    public ColorPicker colorPicker;


    public ObjectProperty<TreeModel<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        originX.textProperty()
                .bind(chart.originXProperty().asString());
        originY.textProperty()
                .bind(chart.originXProperty().asString());

        grid.bindBidirectional(chart.gridProperty());
        tree.bindBidirectional(chart.treeProperty());

        chart.setOnMouseClicked(e -> {
            Point2D pl = chart.localToPlane(e.getX(), e.getY());
            Point2D point = chart.planeToHexagonal(pl.getX(), pl.getY());
            Node node = grid.get().getItem((int) point.getX(), (int) point.getY());
            if (node == null)
                return;

            StringBuilder sb = new StringBuilder();
            tree.get().getPathToNode(node).forEach(i-> sb.append(">").append(i.name));

            System.out.printf("%s node:%s, weight:%d %s\n", point, node.name, tree.get().getWeight(node), sb.toString());
        });

        grid.addListener(e->{
            chart.updateHexagons();
        });
    }

    @FXML
    public void reset(ActionEvent event) {
        chart.zoomTo(1.0);
        chart.scrollTo(0,0);
    }

    @FXML
    public void save(ActionEvent event) throws IOException {
        chart.save("CanvasImage.png");
    }


    public StringProperty colorscheme = new SimpleStringProperty();

    @FXML
    public void onChooseRandom(ActionEvent event){
        colorscheme.set("random");
    }
    @FXML
    public void onChooseRamp(ActionEvent event){
        colorscheme.set("ramp");
    }
}
