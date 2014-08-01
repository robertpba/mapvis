package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import mapvis.models.TreeModel;
import mapvis.models.Grid;
import mapvis.graphic.HexagonalTilingView;

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


    public ObjectProperty<TreeModel<Integer>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Integer>> grid = new SimpleObjectProperty<>();


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

            System.out.printf("%s id:%s, weight:%d %s\n", point, id, tree.get().getWeight(id), sb.toString());
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
