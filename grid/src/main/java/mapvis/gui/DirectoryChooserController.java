package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dacc on 10/9/2015.
 */
public class DirectoryChooserController implements Initializable {

    @FXML
    private TextField selectedDirectoryTextfield;

    @FXML
    private VBox vBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialize");
    }

    public void onSelectDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the Directory to visualize");
        File selectedFolder = directoryChooser.showDialog(vBox.getScene().getWindow());
        System.out.printf("Path:" + selectedFolder.getPath());
        selectedDirectoryTextfield.setText(selectedFolder.getPath());
    }

    void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
    }
}
