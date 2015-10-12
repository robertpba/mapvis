package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.fileSystemTree.FileSystemNode;
import mapvis.fileSystemTree.TreeGenerator;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dacc on 10/9/2015.
 */
public class DirectoryChooserController implements Initializable, IDatasetGeneratorController {

    @FXML
    private TextField selectedDirectoryTextfield;

    @FXML
    private VBox vBox;

    private TreeGenerator treeGenerator;
    private FileSystemNode fileSystemNode;

    public DirectoryChooserController() {
        this.treeGenerator = new TreeGenerator();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init DirectoryChooserController");
    }

    @FXML
    private void onSelectDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select the Directory to visualize");
        File selectedFolder = directoryChooser.showDialog(vBox.getScene().getWindow());
        System.out.printf("Path:" + selectedFolder.getPath());
        selectedDirectoryTextfield.setText(selectedFolder.getPath());
        fileSystemNode = new FileSystemNode(selectedFolder.getPath());
    }

    public void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
    }

    @Override
    public MPTreeImp<Node> generateTree(ActionEvent event) {

        if(fileSystemNode == null || !fileSystemNode.exists())
            return MPTreeImp.from(new Node(Integer.toString(0), "root"));

        Node generatedTree = treeGenerator.genTree(fileSystemNode);
        MPTreeImp<Node> treeModel = MPTreeImp.from(generatedTree);
        return treeModel;
    }

    @Override
    public String toString() {
        return "Directory Random Generator";
    }
}
