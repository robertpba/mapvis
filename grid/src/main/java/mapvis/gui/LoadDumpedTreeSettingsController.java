package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.treeGenerator.FilesystemNode;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dacc on 10/22/2015.
 */
public class LoadDumpedTreeSettingsController implements Initializable, IDatasetGeneratorController {

    @FXML
    private VBox vBox;
    @FXML
    private TextField selectedFileTextfield;

    private File selectedFile;

    private FileChooser fileChooser;
    private Yaml yaml;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public MPTreeImp<INode> generateTree(ActionEvent event) throws FileNotFoundException {
        if(selectedFile == null || !selectedFile.exists())
            return MPTreeImp.from(new Node("", "error reading file"));

        FileInputStream fileInputStream = new FileInputStream(selectedFile);
        Node node = yaml.loadAs(fileInputStream, Node.class);
        MPTreeImp<INode> treeModel = MPTreeImp.from(node);
        return treeModel;
    }

    @FXML
    private void onSelectDumpfile(ActionEvent event) {
        selectedFile = fileChooser.showOpenDialog(vBox.getScene().getWindow());
        //Choosing file was aborted?
        if(selectedFile == null || !selectedFile.exists()){
            return;
        }

        System.out.printf("Path:" + selectedFile.getPath());
        selectedFileTextfield.setText(selectedFile.getPath());
    }

    @Override
    public String toString() {
        return "Tree Dump File";
    }


    @Override
    public void setVisible(boolean isVisible) {
        this.vBox.setVisible(isVisible);
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle("Select the dumped Tree to visualize");
        this.selectedFile = null;
        this.yaml = new Yaml();
    }
}
