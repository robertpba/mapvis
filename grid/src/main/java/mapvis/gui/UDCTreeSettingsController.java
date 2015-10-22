package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.fileSystemTree.TreeGenerator;
import mapvis.udcTree.UDCParser;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by dacc on 10/20/2015.
 */
public class UDCTreeSettingsController implements Initializable, IDatasetGeneratorController {

    @FXML
    private VBox vBox;
    private UDCParser parser;
    private TreeGenerator treeGenerator;

    public UDCTreeSettingsController(){
        parser = new UDCParser();
        treeGenerator = new TreeGenerator();
    }

    @Override
    public void setVisible(boolean isVisible) {
        vBox.setVisible(isVisible);
    }

    @Override
    public MPTreeImp<Node> generateTree(ActionEvent event) {
//        Node udcDree = ParseUDC.createUDCTree();
        parser.configure("D:/downloads/datasets/Libraries/UDC/udcsummary-skos.rdf");
        Node udcNodes = parser.generateUDCCathegories();

        treeGenerator.configure(udcNodes);
        Node connectedUDCTree = treeGenerator.genTree();

        return MPTreeImp.from(connectedUDCTree);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init FilesystemTreeSettingsController");
    }

    @Override
    public String toString() {
        return "UDC Tree Generator";
    }
}
