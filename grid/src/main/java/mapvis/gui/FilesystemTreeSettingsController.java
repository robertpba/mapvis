package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.fileSystemTree.FilesystemNode;
import mapvis.fileSystemTree.TreeGenerator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dacc on 10/9/2015.
 */
public class FilesystemTreeSettingsController implements Initializable, IDatasetGeneratorController {

    @FXML
    private TextField selectedDirectoryTextfield;

    @FXML
    private VBox vBox;

    private TreeGenerator treeGenerator;
    private FilesystemNode filesystemNode;

    public FilesystemTreeSettingsController() {
        System.out.println("Creating: " + this.getClass().getName());
        this.treeGenerator = new TreeGenerator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init FilesystemTreeSettingsController");
    }

    @FXML
    private void onSelectDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the Directory to visualize");
        File selectedFolder = directoryChooser.showDialog(vBox.getScene().getWindow());
        //Choosing directory was aborted?
        if(selectedFolder == null){
            return;
        }
        System.out.printf("Path:" + selectedFolder.getPath());
        selectedDirectoryTextfield.setText(selectedFolder.getPath());
        filesystemNode = new FilesystemNode(selectedFolder.getPath());
    }

    public void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
    }

    @Override
    public MPTreeImp<Node> generateTree(ActionEvent event) {

        if(filesystemNode == null || !filesystemNode.exists())
            return MPTreeImp.from(new Node(Integer.toString(0), "root"));

        Node generatedTree = treeGenerator.genTree(filesystemNode);
        MPTreeImp<Node> treeModel = MPTreeImp.from(generatedTree);
        return treeModel;
    }

    @Override
    public String toString() {
        return "Directory Tree Generator";
    }
}
