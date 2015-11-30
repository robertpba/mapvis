package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mapvis.Impl.HashMapGrid;
import mapvis.Impl.Region.BorderCreator;
import mapvis.algo.Method1;
import mapvis.common.datatype.*;
import mapvis.graphic.HexagonalTilingView;
import mapvis.models.ConfigurationConstants;
import mapvis.models.Grid;
import mapvis.models.Region;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This Controller is controls the selection of the TreeGenerators and delegates
 * the generation of the tree to the responsible @IDatasetGeneratorController
 */
public class DatasetSelectionController implements Initializable {
    private static final String INFO_AREA_PROCESS_SEPARATOR = "-------------";

    //data objects
    public ObjectProperty<Tree2<INode>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<INode>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<INode>> method1 = new SimpleObjectProperty<>();
    public SimpleObjectProperty<TreeStatistics> lastTreeStatistics;

    public HexagonalTilingView chart;

    private IDatasetGeneratorController selectedDatasetGenerator;

    @FXML
    private TextArea infoArea;
    @FXML
    private Button generateTreeButton;
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

    public DatasetSelectionController() {
        System.out.println("Creating: " + this.getClass().getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init DatasetSelectionController");
        //add all controller which allow for tree generation to the combo box
        inputSourceComboBox.getItems().addAll(
                filesystemTreeSettingsController, randomTreeSettingsController,
                udcTreeSettingsController, loadDumpedTreeSettingsController
        );
        inputSourceComboBox.getSelectionModel().select(filesystemTreeSettingsController);

        //only allow numerical values for the "drop Levels" button
        dropLevelsTextField.textProperty().addListener((observable1, oldValue, newValue) -> {
            if (!"".equals(newValue) && !newValue.matches("[0-9]*")) {
                dropLevelsTextField.setText(oldValue);
            }
        });

        lastTreeStatistics = new SimpleObjectProperty<>();
    }

    private IDatasetGeneratorController getSelectedDatasetGenerator() {
        return inputSourceComboBox.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onSelectionChanged(ActionEvent event) {
        selectedDatasetGenerator = getSelectedDatasetGenerator();
        if(selectedDatasetGenerator == null){
            return;
        }
        inputSourceComboBox.getItems().stream().forEach(iDatasetGeneratorController ->{
            //enable/disable all controllers according to the selection, so only the
            //UI of the recent selected datasetcontroller is shown
            if(selectedDatasetGenerator.equals(iDatasetGeneratorController)){
                iDatasetGeneratorController.setVisible(true);
            }else{
                iDatasetGeneratorController.setVisible(false);
            }
        });
    }

    @FXML
    private void generateTree(ActionEvent event) {
        logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
        logTextToInfoArea("generating tree..");
        if(selectedDatasetGenerator == null){
            return;
        }
        MPTreeImp<INode> generatedTree = null;
        try {
            generatedTree = selectedDatasetGenerator.generateTree(event);
        } catch (Exception e) {
            logTextToInfoArea("tenerating tree failed: " + e);
            return;
        }
        setTreeModel(generatedTree);
        logTextToInfoArea("tree generation finished");
        lastTreeStatistics.set(NodeUtils.getTreeDepthStatistics(generatedTree.getRoot()));
        logTextToInfoArea(lastTreeStatistics.get() != null ?
                lastTreeStatistics.get().createStatisticsOverview(true)
                : "error reading statistics");
    }


    @FXML
    private void renderTree(ActionEvent event) {
        long startTime = System.currentTimeMillis();
        logTextToInfoArea(INFO_AREA_PROCESS_SEPARATOR);
        logTextToInfoArea("generating map..");

        //generate regions of visualization
        Region<INode> rootRegion = method1.get().Begin();

        //reconstruct the borders if using region rendering
        if(ConfigurationConstants.USE_REGION_RENDERING){
            BorderCreator<INode> borderCreator = new BorderCreator<>(method1.get());
            borderCreator.createBorders();

            grid.get().resetGrid();
            chart.setRootRegion(rootRegion);
        }

        long estimatedTime = System.currentTimeMillis() - startTime;
        logTextToInfoArea("generation finished: mm: "+ estimatedTime);
        logTextToInfoArea("rendering map");

        chart.updateHexagons();

        logTextToInfoArea("rendering finished");
    }

    private void setTreeModel(MPTreeImp<INode> treeModel)
    {
        tree.set(treeModel);

        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

//        Set<INode> leaves = tree.get().getLeaves();
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
