package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import utils.RandomTreeGenerator;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.HashMapGrid;
import mapvis.grid.jfx.HexagonalTilingView;
import mapvis.Impl.TreeModel;

import java.net.URL;
import java.util.*;


public class AppController implements Initializable {
    @FXML
    public HexagonalTilingView chart;

    @FXML
    public Slider zoomSlider;

    @FXML
    public Text originX;

    @FXML
    public Text originY;

    @FXML
    public TreeTableView<TreeTableViewModelAdapter.Node> treeTableView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        originX.textProperty()
                .bind(chart.originXProperty().asString());
        originY.textProperty()
                .bind(chart.originXProperty().asString());

        chart.setOnMouseClicked(e -> {
            Point2D pl = chart.localToPlane(e.getX(), e.getY());
            Point2D point = chart.planeToHexagonal(pl.getX(), pl.getY());
            Integer id = grid.get((int)point.getX(), (int)point.getY());
            if (id == null)
                return;

            StringBuilder sb = new StringBuilder();
            tree.getPathToNode(id).forEach(i-> sb.append(">").append(i));

            System.out.printf("id:%s, weight:%d %s\n", id, tree.getWeight(id), sb.toString());
        });
    }

    public TreeModel<Integer> tree;
    public HashMapGrid<Integer> grid;
    public CoastCache<Integer> cache;
    public Method1<Integer>  method1;

    @FXML
    public void generateTree(ActionEvent event) {
        int span = 10, weight = 100, depth = 3, seed = 1;
        try {
            span = Integer.parseInt(spanField.getText());
        }
        catch (NumberFormatException ignored) { }
        try {
            weight = Integer.parseInt(weightField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            depth = Integer.parseInt(depthField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            seed = Integer.parseInt(seedField.getText());
        }
        catch (NumberFormatException ignored) {  }

        RandomTreeGenerator gen = new RandomTreeGenerator(seed);
        tree = gen.getTree(depth, span, weight);

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
        method1 = new Method1<>(tree, cache, grid);
        Set<Integer> leaves = tree.getLeaves();
        Map<Integer, Color> map = new HashMap<>();
        Random rand = new Random(seed);
        infoArea.setText(String.format("%d leaves\n", leaves.size()));

        for (Integer leaf : leaves) {
            map.put(leaf, new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0));
        }

        chart.grid = null;
        chart.colorMap = map::get;
        chart.grid = grid;
        chart.tree = tree;
        chart.updateHexagons();

        TreeTableViewModelAdapter adapter = new TreeTableViewModelAdapter(tree);
        TreeItem<TreeTableViewModelAdapter.Node> root = adapter.getRoot();
        root.setExpanded(true);
        treeTableView.setRoot(root);
    }

    @FXML
    public void begin(ActionEvent event) {
        method1.Begin();
        chart.updateHexagons();
    }

    @FXML
    public void reset(ActionEvent event) {
        chart.zoomTo(1.0);
        chart.scrollTo(0,0);
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
