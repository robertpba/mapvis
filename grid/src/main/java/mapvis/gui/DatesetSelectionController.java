package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mapvis.Impl.HashMapGrid;
import mapvis.algo.Method1;
import mapvis.common.datatype.MPTreeImp;
import mapvis.common.datatype.Node;
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

    public TextArea infoArea;
    public Button generateTreeButton;
    public Button beginTreeButton;
    public HexagonalTilingView chart;

    public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<Node>> method1 = new SimpleObjectProperty<>();

    @FXML
    private ComboBox<IDatasetGeneratorController> inputSourceComboBox;

    @FXML
    private RandomTreeSettingsController randomTreeSettingsController;

    @FXML
    private FilesystemTreeSettingsController filsystemTreeSettingsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init DatesetSelectionController");
        inputSourceComboBox.getItems().addAll(filsystemTreeSettingsController, randomTreeSettingsController);
        inputSourceComboBox.getSelectionModel().select(filsystemTreeSettingsController);
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
        renderTreeService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
//                        generateTreeButton.setDisable(true);
//                        beginTreeButton.setDisable(true);
//                        logToInfoArea("Rendering...");
                        long startTime = System.currentTimeMillis();

                        method1.get().Begin();

                        long estimatedTime = System.currentTimeMillis() - startTime;
                        System.out.printf("mm: %d",estimatedTime);
//                        logToInfoArea("Begin finished");
//                        chart.updateHexagons();
                        System.out.printf("Rendering Finished!");
//                        logToInfoArea("Rendering finished");
//                        logToInfoArea("mm: " + estimatedTime);
//                        generateTreeButton.setDisable(false);
//                        beginTreeButton.setDisable(false);
                        return null;
                    }
                };
            }
        };
        renderTreeService.setOnSucceeded(event1 -> {
            chart.updateHexagons();
        });
        renderTreeService.restart();
//        Task task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                generateTreeButton.setDisable(true);
//                beginTreeButton.setDisable(true);
//                logToInfoArea("Rendering...");
//                long startTime = System.currentTimeMillis();
//
//                method1.get().Begin();
//
//                long estimatedTime = System.currentTimeMillis() - startTime;
////            System.out.printf("mm: %d",estimatedTime);
//                logToInfoArea("Begin finished");
//                chart.updateHexagons();
//
//                logToInfoArea("Rendering finished");
//                logToInfoArea("mm: " + estimatedTime);
//                generateTreeButton.setDisable(false);
//                beginTreeButton.setDisable(false);
//                return null;
//            }
//        };
//        new Thread(task).start();
//        new Thread(() -> {
//            logToInfoArea("Rendering...");
//            long startTime = System.currentTimeMillis();
//
//            method1.get().Begin();
//
//            long estimatedTime = System.currentTimeMillis() - startTime;
////            System.out.printf("mm: %d",estimatedTime);
//            logToInfoArea("Begin finished");
//            chart.updateHexagons();
//
//            logToInfoArea("Rendering finished");
//            logToInfoArea("mm: " + estimatedTime);
//
//        }).run();

    }
    private Service<Void> generateTreeService;
    private Service<Void> renderTreeService;
    @FXML
    private void generateTree(ActionEvent event) {
        generateTreeService = new Service<Void>() {
            public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
//                        generateTreeButton.setDisable(true);
//                        beginTreeButton.setDisable(true);
                        IDatasetGeneratorController activeDatasetGenerator = getActiveDatasetGenerator();
                        MPTreeImp<Node> generatedTree = activeDatasetGenerator.generateTree(event);

                        tree.set(generatedTree);
                        //        grid.set(new HashMapGrid<>());
                        grid.get().resetGrid();
                        method1.set(new Method1<>(tree.get(), grid.get()));

                        Set<Node> leaves = tree.get().getLeaves();
//                        logToInfoArea("Generation Finished");
//                        logToInfoArea(String.format("%d leaves", leaves.size()));
                        System.out.println("Generation Finished");
                        System.out.println(String.format("%d leaves", leaves.size()));
//                        generateTreeButton.setDisable(false);
//                        beginTreeButton.setDisable(false);
                        return null;
                    }
                };
            }
        };
        generateTreeService.restart();
//
//        Task task = new Task<Void>() {
//            public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
//            public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
//            public ObjectProperty<Method1<Node>> method1 = new SimpleObjectProperty<>();
//            @Override
//            protected Void call() throws Exception {
////                generateTreeButton.setDisable(true);
////                beginTreeButton.setDisable(true);
////                logToInfoArea("Generating Tree...");
//
//                IDatasetGeneratorController activeDatasetGenerator = getActiveDatasetGenerator();
//                MPTreeImp<Node> generatedTree = activeDatasetGenerator.generateTree(event);
//
//                tree.set(generatedTree);
//
////        grid.set(new HashMapGrid<>());
//                grid.get().resetGrid();
//                method1.set(new Method1<>(tree.get(), grid.get()));
//
//                Set<Node> leaves = tree.get().getLeaves();
////                logToInfoArea("Generation Finished");
////                logToInfoArea(String.format("%d leaves", leaves.size()));
//                System.out.println("Generation Finished");
//                System.out.println(String.format("%d leaves", leaves.size()));
////                generateTreeButton.setDisable(false);
////                beginTreeButton.setDisable(false);
//                return null;
//            }
//        };

//        new Thread(task).start();

//        new Thread(() -> {
//            logToInfoArea("Generating Tree...");
//
//            IDatasetGeneratorController activeDatasetGenerator = getActiveDatasetGenerator();
//            MPTreeImp<Node> generatedTree = activeDatasetGenerator.generateTree(event);
//
//            tree.set(generatedTree);
//
////        grid.set(new HashMapGrid<>());
//            grid.get().resetGrid();
//            method1.set(new Method1<>(tree.get(), grid.get()));
//
//            Set<Node> leaves = tree.get().getLeaves();
//            logToInfoArea("Generation Finished");
//            logToInfoArea(String.format("%d leaves", leaves.size()));
//
//        }).run();

    }
    private void logToInfoArea(String logMessage)
    {
        infoArea.setText(infoArea.getText() + "\n" + logMessage );
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
