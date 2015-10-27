package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mapvis.Impl.HashMapGrid;
import mapvis.algo.Method1;
import mapvis.common.datatype.*;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.Grid;
import mapvis.models.Region;
import mapvis.models.Tile;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class DatasetSelectionController implements Initializable {
    private static final String INFO_AREA_PROCESS_SEPARATOR = "-------------";
    public TextArea infoArea;
    public Button generateTreeButton;

    public ObjectProperty<Tree2<INode>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<INode>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<INode>> method1 = new SimpleObjectProperty<>();

    public HexagonalTilingView chart;

    IDatasetGeneratorController activeDatasetGenerator;

    @FXML
    private TextField dropLevelsTextField;

    @FXML
    private ComboBox<IDatasetGeneratorController> inputSourceComboBox;

    @FXML
    private RandomTreeSettingsController randomTreeSettingsController;

    @FXML
    private FilesystemTreeSettingsController filesystemTreeSettingsController;

    @FXML
    private UDCTreeSettingsController udcTreeSettingsController;

    @FXML
    private LoadDumpedTreeSettingsController loadDumpedTreeSettingsController;

    public SimpleObjectProperty<TreeStatistics> lastTreeStatistics;
//    private TreeStatistics lastTreeStatistics;

    public DatasetSelectionController() {
        System.out.println("Creating: " + this.getClass().getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init DatasetSelectionController");
        inputSourceComboBox.getItems().addAll(
                filesystemTreeSettingsController, randomTreeSettingsController,
                udcTreeSettingsController, loadDumpedTreeSettingsController);
        inputSourceComboBox.getSelectionModel().select(filesystemTreeSettingsController);

        dropLevelsTextField.textProperty().addListener((observable1, oldValue, newValue) -> {
            System.out.println("first: " + !("".equals(newValue)) + " " + !newValue.matches("[0-9]"));
            if (!"".equals(newValue) && !newValue.matches("[0-9]*")) {
                dropLevelsTextField.setText(oldValue);
            }
        });
        lastTreeStatistics = new SimpleObjectProperty<>();
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
        logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
        logTextToInfoArea("generating map..");
        Region<INode> world = method1.get().Begin();


        long estimatedTime = System.currentTimeMillis() - startTime;
        logTextToInfoArea("generation finished: mm: "+ estimatedTime);
        logTextToInfoArea("rendering map");
        chart.setWorld(world);
//        chart.updateHexagonsWithCoastCache(world);
        chart.updateHexagons();
        logTextToInfoArea("rendering finished");
    }

    @FXML
    private void generateTree(ActionEvent event) {
        logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
        logTextToInfoArea("reading data..");
        if(activeDatasetGenerator == null){
            return;
        }
        MPTreeImp<INode> generatedTree = null;
        try {
            generatedTree = activeDatasetGenerator.generateTree(event);
        } catch (FileNotFoundException e) {
            logTextToInfoArea("reading data failed: " + e);
            return;
        }
        setTreeModel(generatedTree);
        logTextToInfoArea("reading data finished");
        lastTreeStatistics.set(NodeUtils.getTreeDepthStatistics(generatedTree.getRoot()));
        logTextToInfoArea(lastTreeStatistics.get() != null ?
                lastTreeStatistics.get().createStatisticsOverview(true)
                : "error reading statistics");
    }


    private void setTreeModel(MPTreeImp<INode> treeModel)
    {
        tree.set(treeModel);

        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<INode> leaves = tree.get().getLeaves();
//        logTextToInfoArea(String.format("%d leaves", leaves.size()));
    }

    @FXML
    private void onDropLevelsPressed(ActionEvent event) {
        try {
            int levelsToDrop = Integer.parseInt(dropLevelsTextField.getText());
            logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
            logTextToInfoArea("Dropping levels > " + levelsToDrop);
            Node filteredTree = NodeUtils.filterByDepth(tree.get().getRoot(), levelsToDrop);
            TreeStatistics cappedTreeStatistics = NodeUtils.getTreeDepthStatistics(filteredTree);
            TreeStatistics diffTreeStatistics = NodeUtils.diffTreeStatistics(lastTreeStatistics.get(), cappedTreeStatistics);
            MPTreeImp<INode> cappedTreeModel = MPTreeImp.from(filteredTree);
            setTreeModel(cappedTreeModel);
            lastTreeStatistics.set(cappedTreeStatistics);
            if(lastTreeStatistics == null || lastTreeStatistics.get() == null){
                logTextToInfoArea("Error dropping levels");
                return;
            }
            logTextToInfoArea("Dropping finished");
            logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
            logTextToInfoArea("New Tree:");
            logTextToInfoArea(lastTreeStatistics.get().createStatisticsOverview(false));
            logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
            logTextToInfoArea("Diff to last tree:");
            logTextToInfoArea(diffTreeStatistics.createStatisticsOverview(true));
        }catch (NumberFormatException ex){
            return;
        }
    }

    private void logTextToInfoArea(String text){
        infoArea.appendText("\n" + text);
    }
}
