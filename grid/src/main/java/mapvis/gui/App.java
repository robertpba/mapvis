package mapvis.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mapvis.RandomData;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.Grid;
import mapvis.grid.HashMapGrid;
import mapvis.tree.MPTT;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class App extends Application {
    static MPTT<Integer> tree;
    static CoastCache<Integer> cache;
    static Grid<Integer> grid;
    private Map<Integer, Color> colormap;
    private Method1 method1;

    @Override
    public void start(Stage stage) throws Exception {

        URL location = AppController.class.getResource("app.fxml");

        FXMLLoader loader = new FXMLLoader();
        AppController controller = new AppController();
        loader.setController(controller);

        Parent root = loader.load(location.openStream());

        generateTree();
        GridPanel gridPanel = createPanel();



        //SwingUtilities.invokeLater(()-> {
        Platform.runLater(() -> {
            controller.swingnode.setContent(gridPanel);
        });


        Scene scene = new Scene(root);

        stage.setScene(scene);

        stage.setWidth(1000);
        stage.setHeight(1000);

        stage.show();

        Platform.runLater(() -> {
            method1.Begin();
        });

    }

    private GridPanel createPanel(){

        GridPanel gridPanel = new GridPanel(){
            @Override
            protected void renderCell(Graphics2D g2d, int i, int j, Object o) {
                super.renderCell(g2d, i, j, o);
                //hexagon
                g2d.setColor(colormap.get(o));
                g2d.fill(hexagon);
                g2d.drawString(o.toString(), 0,0);
            }
        };
        gridPanel.grid = grid;
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point point = e.getPoint();
                Point coord = gridPanel.screenToGridCoordinate(point.x, point.y);
                Integer str = grid.get(coord.x, coord.y);
                System.out.printf("id:%s, weight:%d\n", str, tree.getWeight(str));

            }
        });

        //gridPanel.setPreferredSize(new Dimension(1000, 1000));



        return gridPanel;



        //method1.Begin();

    }

    private void generateTree() {
        tree = RandomData.getTree();

        grid = new HashMapGrid<>();
        cache = new CoastCache<>(grid, tree);
         method1 = new Method1<>(tree, cache, grid);

        Set<Integer> leaves = tree.getLeaves();
        Map<Integer, Color> map = new HashMap();
        Random rand = new Random(1);
        System.out.printf("%d leaves\n", leaves.size());
        for (Integer leaf : leaves) {
            map.put(leaf, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        }
        colormap = map;
    }
}
