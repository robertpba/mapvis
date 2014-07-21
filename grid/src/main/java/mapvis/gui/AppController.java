package mapvis.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import mapvis.RandomData;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.HashMapGrid;
import mapvis.tree.MPTT;
import utils.PanZoomPanel;

import java.awt.*;
import java.net.URL;
import java.util.*;


public class AppController implements Initializable {

    @FXML
    public GridChart gridChart;

    @FXML
    public PanZoomPanel panPanel;

    @FXML
    public Slider zoomSlider;

    @FXML
    public Text panFactorX;

    @FXML
    public Text panFactorY;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(panPanel.zoomFactorProperty());
        panFactorX.textProperty()
                .bind(panPanel.panOriginXProperty().asString());
        panFactorY.textProperty()
                .bind(panPanel.panOriginXProperty().asString());

//        panPanel.setOnMouseClicked(e -> {
//            Point2D local = gridChart.parentToLocal(e.getX(), e.getY());
//            Point point = gridChart.toGridCoordinate(local.getX(), local.getY());
//            Integer id = grid.get(point.x, point.y);
//            if (id == null)
//                return;
//            System.out.printf("id:%s, weight:%d\n", id, tree.getWeight(id));
//        });
        gridChart.setOnMouseClicked(e -> {
            Point point = gridChart.toGridCoordinate(e.getX(), e.getY());
            Integer id = grid.get(point.x, point.y);
            if (id == null)
                return;
            System.out.printf("id:%s, weight:%d\n", id, tree.getWeight(id));
        });

    }



    public MPTT<Integer> tree;
    public HashMapGrid<Integer> grid;
    public CoastCache<Integer> cache;
    public Method1<Integer>  method1;

    @FXML
    public void generateTree(ActionEvent event) {
        int span = 10, weight = 100, depth = 3, seed = 1;
        try {
            span = Integer.parseInt(spanField.getText());
        }
        catch (NumberFormatException e) { }
        try {
            weight = Integer.parseInt(weightField.getText());
        }
        catch (NumberFormatException e) {  }
        try {
            depth = Integer.parseInt(depthField.getText());
        }
        catch (NumberFormatException e) {  }
        try {
            seed = Integer.parseInt(seedField.getText());
        }
        catch (NumberFormatException e) {  }

        RandomData.rn.setSeed(seed);
        tree = RandomData.getTree(depth, span, weight);

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
        method1 = new Method1<>(tree, cache, grid);
        Set<Integer> leaves = tree.getLeaves();
        Map<Integer, Color> map = new HashMap();
        Random rand = new Random(seed);
        infoArea.setText(String.format("%d leaves\n", leaves.size()));

        for (Integer leaf : leaves) {
            map.put(leaf, new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
        }
        gridChart.grid = null;
        gridChart.colorMap = o -> map.get(o);
        gridChart.grid = grid;
        gridChart.updateHexagons();
    }

    @FXML
    public void begin(ActionEvent event) {
        method1.Begin();
        gridChart.updateHexagons();
    }

    @FXML
    public void reset(ActionEvent event) {
        panPanel.zoomTo(1.0);
        panPanel.scrollTo(0,0);
    }

    @FXML
    private TextField weightField;

    @FXML
    private TextField depthField;

    @FXML
    private TextField spanField;

    @FXML
    private TextField seedField;

    @FXML
    private TextArea infoArea;

}
