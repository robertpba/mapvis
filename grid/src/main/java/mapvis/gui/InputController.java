package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import mapvis.Impl.HashMapGrid;
import mapvis.algo.Method1;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;
import mapvis.fileSystemTree.FileSystemNode;
import mapvis.fileSystemTree.TreeGenerator;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.Grid;
import org.yaml.snakeyaml.Yaml;
import utils.RandomTreeGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class InputController implements Initializable {

    @FXML
    private VBox inputSourceContainer;

    @FXML
    private ComboBox<String> inputSourceComboBox;

    @FXML
    private Pane inputSourcePane;

    @FXML
    private SettingController settingController;

    @FXML
    private DirectoryChooserController directoryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputSourceComboBox.getItems().addAll("Test1", "Test2");
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("setting.fxml"));
//        settingController = loader.getController();

    }

    public void onSelectionChanged(ActionEvent event) {
        System.out.println("selection changed to " + inputSourceComboBox.getSelectionModel().getSelectedItem());
        if(inputSourceComboBox.getSelectionModel().getSelectedItem().equals("Test1")){
            settingController.setVisible(true);
            directoryController.setVisible(false);
//            inputSourceContainer.getChildren().clear();
//            inputSourceContainer.getChildren().add(settingController);

//            settingController.setVisible(true);
//            directoryController.setVisible(false);
        }else{
            settingController.setVisible(false);
            directoryController.setVisible(true);
//            inputSourceContainer.getChildren().clear();
//            inputSourceContainer.getChildren().add(directoryController);
//            inputSourceContainer.setVisible(false);

//            settingController.setVisible(false);
//            directoryController.setVisible(true);
        }
    }

    //
    @FXML
    public void begin(ActionEvent event) {

    }

}
