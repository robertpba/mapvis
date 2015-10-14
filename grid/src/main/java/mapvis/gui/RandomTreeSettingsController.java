package mapvis.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import mapvis.Impl.HashMapGrid;
import mapvis.algo.Method1;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import org.yaml.snakeyaml.Yaml;
import utils.RandomTreeGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class RandomTreeSettingsController implements Initializable, IDatasetGeneratorController {
    @FXML
    private TextField weightField;
    @FXML
    private TextField depthField;
    @FXML
    private TextField spanField;
    @FXML
    private TextField seedField;
    @FXML
    private TextArea infoArea;
    @FXML
    private VBox vBox;
    @FXML
    private Button generateTreeButton;

    private static final int DEFAULT_SEED = 1;
    private static final int DEFAULT_WEIGHT = 100;
    private static final int DEFAULT_DEPTH = 3;

    public RandomTreeSettingsController() {
        System.out.println("Creating: " + this.getClass().getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init DatesetSelectionController");
    }

    public MPTreeImp<Node> generateTree(ActionEvent event) {
        int seed = DEFAULT_SEED, span = DEFAULT_SEED, weight = DEFAULT_WEIGHT, depth = DEFAULT_DEPTH;
        try {
            span = Integer.parseInt(spanField.getText());
        }
        catch (NumberFormatException ignored) { }
        try {
            weight = Integer.parseInt(weightField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            depth = Integer.parseInt(depthField.getText());
        }
        catch (NumberFormatException ignored) {  }
        try {
            seed = Integer.parseInt(seedField.getText());
        }
        catch (NumberFormatException ignored) {  }

        RandomTreeGenerator randomTreeGenerator = new RandomTreeGenerator(seed);
        MPTreeImp<Node> genTree = randomTreeGenerator.getTree(depth, span, weight);
        return genTree;
    }

    public void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
    }

    @Override
    public String toString() {
        return "Random Tree Generator";
    }
}
