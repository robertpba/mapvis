package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dacc on 10/9/2015.
 */
public class DirectoryChooserController implements Initializable {
    @FXML
    VBox vBox;

//    void setVisible(boolean isVisible){
//        vBox.setVisible(isVisible);
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize");
    }

    public void onSelectDirectory(ActionEvent event) {
        System.out.println("onSelectDir");
    }

    void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
//        generateTreeButton.setVisible(isVisible);
//        generateTreeButton.setManaged(isVisible);

    }
}
