package utils;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class PanZoomPanelTest  extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane pane = new StackPane();
        Scene scene = new Scene(pane, 500, 500);
        primaryStage.setScene(scene);

        PanZoomPanel panZoomPanel = new PanZoomPanel();

        String imageUrl = "file:\\C:\\Users\\Public\\Pictures\\Sample Pictures\\Desert.jpg";
        Image image = new Image(imageUrl);
        ImageView imageView = new ImageView(image);
        panZoomPanel.setContent(imageView);


        pane.getChildren().add(panZoomPanel);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}