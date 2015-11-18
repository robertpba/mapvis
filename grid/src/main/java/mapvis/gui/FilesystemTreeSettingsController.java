package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.treeGenerator.FilesystemNode;
import mapvis.treeGenerator.ITreeGenerator;
import mapvis.treeGenerator.TreeGeneratorSummedLeaves;

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

    private ITreeGenerator treeGenerator;

    public File selectedDirectory;

    DirectoryChooser directoryChooser;

    public FilesystemTreeSettingsController() {
        System.out.println("Creating: " + this.getClass().getName());
        this.treeGenerator = new TreeGeneratorSummedLeaves();
        this.directoryChooser = new DirectoryChooser();
        this.directoryChooser.setTitle("Select the Directory to visualize");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init FilesystemTreeSettingsController");
    }

    @FXML
    private void onSelectDirectory(ActionEvent event) {
        File selectedFolder = directoryChooser.showDialog(vBox.getScene().getWindow());
        //Choosing directory was aborted?
        if(selectedFolder == null){
            return;
        }
        selectedDirectory = selectedFolder;
        treeGenerator.setRootNode(new FilesystemNode(selectedFolder.getPath()));
        System.out.printf("Path:" + selectedFolder.getPath());
        selectedDirectoryTextfield.setText(selectedFolder.getPath());
    }

    public void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
    }

    @Override
    public MPTreeImp<INode> generateTree(ActionEvent event) {
        if(selectedDirectory == null || !selectedDirectory.exists())
            return MPTreeImp.from(new Node(Integer.toString(0), "root"));

        INode generatedTree = treeGenerator.genTree();
        MPTreeImp<INode> treeModel = MPTreeImp.from(generatedTree);
        return treeModel;
    }

    @Override
    public String toString() {
        return "Directory Tree Generator";
    }
}
