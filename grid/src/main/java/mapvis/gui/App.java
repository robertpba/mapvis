package mapvis.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mapvis.algo.CoastCache;
import mapvis.algo.Method1;
import mapvis.grid.Grid;
import mapvis.tree.TreeModel;

import java.awt.*;
import java.net.URL;
import java.util.Map;

public class App extends Application {
    static TreeModel<Integer> tree;
    static CoastCache<Integer> cache;
    static Grid<Integer> grid;
    private Map<Integer, Color> colormap;
    private Method1 method1;

    @Override
    public void start(Stage stage) throws Exception {

        URL location = AppController.class.getResource("app.fxml");

        FXMLLoader loader = new FXMLLoader();
        BorderPane root = loader.load(location.openStream());

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(1000);

        stage.show();
    }

}
