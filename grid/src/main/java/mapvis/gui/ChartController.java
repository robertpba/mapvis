package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import mapvis.Impl.TreeModel;
import mapvis.grid.Grid;
import mapvis.graphic.HexagonalTilingView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class ChartController implements Initializable  {
    @FXML
    public HexagonalTilingView chart;

    @FXML
    public Slider zoomSlider;

    @FXML
    public Text originX;

    @FXML
    public Text originY;

    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Function<Object, Color>> colorMap = new SimpleObjectProperty<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        originX.textProperty()
                .bind(chart.originXProperty().asString());
        originY.textProperty()
                .bind(chart.originXProperty().asString());

        grid.bindBidirectional(chart.gridProperty());

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

        grid.addListener(e->{
            chart.updateHexagons();
        });
    }

    @FXML
    public void reset(ActionEvent event) {
        chart.zoomTo(1.0);
        chart.scrollTo(0,0);
    }

}
