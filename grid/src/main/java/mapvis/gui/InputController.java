package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

    public TextArea infoArea;
    public Button generateTreeButton;

    public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<Node>> method1 = new SimpleObjectProperty<>();

    public HexagonalTilingView chart;

    @FXML
    private VBox inputSourceContainer;

    @FXML
    private ComboBox<IDatasetGeneratorController> inputSourceComboBox;

    @FXML
    private Pane inputSourcePane;

    @FXML
    private SettingController settingController;

    @FXML
    private DirectoryChooserController directoryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init InputController");
        inputSourceComboBox.getItems().addAll(directoryController, settingController);
        inputSourceComboBox.getSelectionModel().select(directoryController);
    }

    private IDatasetGeneratorController getActiveDatasetGenerator() {
        return inputSourceComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onSelectionChanged(ActionEvent event) {
        IDatasetGeneratorController activeDatasetGenerator = getActiveDatasetGenerator();
        inputSourceComboBox.getItems().stream().forEach(iDatasetGeneratorController ->{
            if(activeDatasetGenerator.equals(iDatasetGeneratorController)){
                iDatasetGeneratorController.setVisible(true);
            }else{
                iDatasetGeneratorController.setVisible(false);
            }
        });
    }

    @FXML
    private void begin(ActionEvent event) {
        long startTime = System.currentTimeMillis();

        method1.get().Begin();

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("mm: %d",estimatedTime);

        chart.updateHexagons();
    }

    @FXML
    private void generateTree(ActionEvent event) {
        IDatasetGeneratorController activeDatasetGenerator = getActiveDatasetGenerator();
        MPTreeImp<Node> generatedTree = activeDatasetGenerator.generateTree(event);

        tree.set(generatedTree);

        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<Node> leaves = tree.get().getLeaves();
        infoArea.setText(String.format("%d leaves\n", leaves.size()));
    }

    @FXML
    private void loadFile(ActionEvent event) throws FileNotFoundException {
        //TreeLoader loader = new TreeLoader();
        //loader.load("data/simple.txt");

//        TreeLoader2 loader = new TreeLoader2();
//        loader.load("data/university_data_tree.csv");
//        Tree2<Node> treemodel = loader.convertToTreeModel();

        Yaml yaml = new Yaml();
        FileInputStream fileInputStream = new FileInputStream("io/data/rand01.yaml");
        Node node = yaml.loadAs(fileInputStream, Node.class);

        MPTreeImp<Node> treemodel = MPTreeImp.from(node);

        tree.set(treemodel);

        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<Node> leaves = tree.get().getLeaves();
        infoArea.setText(String.format("%d leaves\n", leaves.size()));
    }

}
