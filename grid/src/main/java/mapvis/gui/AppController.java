package mapvis.gui;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import mapvis.RandomData;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.HashMapGrid;
import mapvis.grid.jfx.HexagonalTilingView;
import mapvis.tree.MPTT;

import java.net.URL;
import java.util.*;


public class AppController implements Initializable {

    @FXML
    public HexagonalTilingView chart;


    @FXML
    public Slider zoomSlider;

    @FXML
    public Text panFactorX;

    @FXML
    public Text panFactorY;

    @FXML
    public TreeTableView<Integer> treeTableView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        zoomSlider.valueProperty()
                .bindBidirectional(chart.zoomProperty());
        panFactorX.textProperty()
                .bind(chart.originXProperty().asString());
        panFactorY.textProperty()
                .bind(chart.originXProperty().asString());

        chart.setOnMouseClicked(e -> {
            Point2D pl = chart.localToPlane(e.getX(), e.getY());
            Point2D point = chart.planeToHexagonal(pl.getX(), pl.getY());
            Integer id = grid.get((int)point.getX(), (int)point.getY());
            if (id == null)
                return;

            StringBuilder sb = new StringBuilder();
            tree.getPathToNode(id).forEach(i->sb.append(">"+i));

            System.out.printf("id:%s, weight:%d %s\n", id, tree.getWeight(id), sb.toString());
        });

        for (TreeTableColumn column : treeTableView.getColumns()) {
            if (column.getText().equals("ID")) {
                column.setCellValueFactory(
                    new Callback<TreeTableColumn.CellDataFeatures<Integer, ?>, ObservableValue<?>>() {
                    @Override
                    public ObservableValue<?> call(TreeTableColumn.CellDataFeatures<Integer, ?> param) {
                        return new ReadOnlyIntegerWrapper(param.getValue().getValue());
                    }
                });
            } else if (column.getText().equals("Size")) {
                column.setCellValueFactory(
                        new Callback<TreeTableColumn.CellDataFeatures<Integer, ?>, ObservableValue<?>>() {
                            @Override
                            public ObservableValue<?> call(TreeTableColumn.CellDataFeatures<Integer, ?> param) {
                                return new ReadOnlyIntegerWrapper(tree.getWeight(param.getValue().getValue()));
                            }
                        });
            } else {
                column.setCellValueFactory( p -> new ReadOnlyStringWrapper(""));
            }


        }
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

        chart.grid = null;
        chart.colorMap = o -> map.get(o);
        chart.grid = grid;
        chart.tree = tree;
        chart.updateHexagons();

        TreeItem<Integer> rootItem = translateTree(tree.getRoot());
        rootItem.setExpanded(true);
        treeTableView.setRoot(rootItem);
    }


    private TreeItem translateTree(Integer p){
        TreeItem item = new TreeItem(p);

        for (Integer integer : tree.getChildren(p)) {
            item.getChildren().add(translateTree(integer));
        }
        return item;
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
