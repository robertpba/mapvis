package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mapvis.Impl.HashMapGrid;
import mapvis.algo.Method1;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.NodeUtils;
import mapvis.common.datatype.Tree2;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.Grid;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class DatesetSelectionController implements Initializable {
    private static final String INFO_AREA_PROCESS_SEPARATOR = "-------------";
    public TextArea infoArea;
    public Button generateTreeButton;

    public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<Node>> method1 = new SimpleObjectProperty<>();

    public HexagonalTilingView chart;

    IDatasetGeneratorController activeDatasetGenerator;

    @FXML
    private TextField dropLevelsTextField;

    @FXML
    private ComboBox<IDatasetGeneratorController> inputSourceComboBox;

    @FXML
    private RandomTreeSettingsController randomTreeSettingsController;

    @FXML
    private FilesystemTreeSettingsController filsystemTreeSettingsController;
    private NodeUtils.TreeStatistics lastTreeStatistics;

    public DatesetSelectionController() {
        System.out.println("Creating: " + this.getClass().getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init DatesetSelectionController");

        inputSourceComboBox.getItems().addAll(filsystemTreeSettingsController, randomTreeSettingsController);
//        activeDatasetGenerator.setValue(filsystemTreeSettingsController);
//        activeDatasetGenerator.unbindBidirectional(inputSourceComboBox.valueProperty());
//        inputSourceComboBox.valueProperty().bind(activeDatasetGenerator);
        inputSourceComboBox.getSelectionModel().select(filsystemTreeSettingsController);

        dropLevelsTextField.textProperty().addListener((observable1, oldValue, newValue) -> {
            System.out.println("first: " + !("".equals(newValue)) + " " + !newValue.matches("[0-9]"));
            if (!"".equals(newValue) && !newValue.matches("[0-9]*")) {
                dropLevelsTextField.setText(oldValue);
            }
        });
    }

    private IDatasetGeneratorController getActiveDatasetGenerator() {
        return inputSourceComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onSelectionChanged(ActionEvent event) {
        activeDatasetGenerator = getActiveDatasetGenerator();
        if(activeDatasetGenerator == null){
            return;
        }
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
        logTextToInfoArea("generating map..");
        method1.get().Begin();

        long estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.printf("mm: %d",estimatedTime);
        logTextToInfoArea("generation finished: mm: "+ estimatedTime);
        logTextToInfoArea("rendering map");
        chart.updateHexagons();
        logTextToInfoArea("rendering finished");
    }

    @FXML
    private void generateTree(ActionEvent event) {
//        System.out.println("generate Tree");
        logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
        logTextToInfoArea("reading data..");
        if(activeDatasetGenerator == null){
            return;
        }
        MPTreeImp<Node> generatedTree = activeDatasetGenerator.generateTree(event);
        setTreeModel(generatedTree);
        logTextToInfoArea("reading data finished");
        lastTreeStatistics = NodeUtils.getTreeDepthStatistics(generatedTree.getRoot());
        logTextToInfoArea(lastTreeStatistics != null ? lastTreeStatistics.toString() : "error reading statistics");
//        logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
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

        MPTreeImp<Node> treeModel = MPTreeImp.from(node);
        setTreeModel(treeModel);
    }

    private void setTreeModel(MPTreeImp<Node> treeModel)
    {
        tree.set(treeModel);

        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<Node> leaves = tree.get().getLeaves();
//        logTextToInfoArea(String.format("%d leaves", leaves.size()));
    }

    @FXML
    private void onDropLevelsPressed(ActionEvent event) {
        System.out.println("onDropLevelsPressed");
        try {
            int levelsToDrop = Integer.parseInt(dropLevelsTextField.getText());
            logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
            logTextToInfoArea("Dropping levels > " + levelsToDrop);
            Node filteredTree = NodeUtils.filterByDepth(tree.get().getRoot(), levelsToDrop);
            NodeUtils.TreeStatistics cappedTreeStatistics = NodeUtils.getTreeDepthStatistics(filteredTree);
            NodeUtils.TreeStatistics diffTreeStatistics = NodeUtils.diffTreeStatistics(lastTreeStatistics, cappedTreeStatistics);
            MPTreeImp<Node> cappedTreeModel = MPTreeImp.from(filteredTree);
            setTreeModel(cappedTreeModel);
            lastTreeStatistics = cappedTreeStatistics;
            if(lastTreeStatistics == null || lastTreeStatistics == null){
                logTextToInfoArea("Error dropping levels");
                return;
            }
            logTextToInfoArea("Dropping finished");
            logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
            logTextToInfoArea("New Tree:");
            logTextToInfoArea(lastTreeStatistics.toString());
            logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
            logTextToInfoArea("Diff to last tree:");
            logTextToInfoArea(diffTreeStatistics.toString());
        }catch (NumberFormatException ex){
            return;
        }
    }

    private void logTextToInfoArea(String text){
        infoArea.appendText("\n" + text);
    }
}
