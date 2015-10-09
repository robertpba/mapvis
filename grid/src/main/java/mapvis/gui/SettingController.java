package mapvis.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

public class SettingController implements Initializable {
    @FXML
    public TextField weightField;
    @FXML
    public TextField depthField;
    @FXML
    public TextField spanField;
    @FXML
    public TextField seedField;
    @FXML
    public TextArea infoArea;
    @FXML
    public VBox vBox;

    public ObjectProperty<Tree2<Node>> tree = new SimpleObjectProperty<>();
    public ObjectProperty<Grid<Node>> grid = new SimpleObjectProperty<>();
    public ObjectProperty<Method1<Node>> method1 = new SimpleObjectProperty<>();

    public HexagonalTilingView chart;

    @FXML
    public Button generateTreeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @FXML
    public void begin(ActionEvent event) {

        long startTime = System.currentTimeMillis();

        method1.get().Begin();

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("mm: %d",estimatedTime);


        chart.updateHexagons();
    }

    @FXML
    public void generateTree(ActionEvent event) {
        int span = 10, weight = 100, depth = 3, seed = 1;
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

        RandomTreeGenerator gen = new RandomTreeGenerator(seed);
        MPTreeImp<Node> genTree = gen.getTree(depth, span, weight);

        long startTime = System.currentTimeMillis();
        tree.set(genTree);
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.printf("gen: %d",estimatedTime);




        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<Node> leaves = tree.get().getLeaves();
        infoArea.setText(String.format("%d leaves\n", leaves.size()));

    }

    @FXML
    public void loadLinuxKernel(ActionEvent event) throws FileNotFoundException {

        TreeGenerator gen = new TreeGenerator();
        Node node = gen.genTree(new FileSystemNode("D:\\downloads\\datasets\\linux-4.2.3"));

        MPTreeImp<Node> treeModel = MPTreeImp.from(node);

        tree.set(treeModel);

        grid.set(new HashMapGrid<>());
        method1.set(new Method1<>(tree.get(), grid.get()));

        Set<Node> leaves = tree.get().getLeaves();
        infoArea.setText(String.format("%d leaves\n", leaves.size()));
    }

    @FXML
    public void loadFile(ActionEvent event) throws FileNotFoundException {
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

    void setVisible(boolean isVisible){
        vBox.setVisible(isVisible);
        vBox.setManaged(isVisible);
//        generateTreeButton.setVisible(isVisible);
//        generateTreeButton.setManaged(isVisible);

    }

}
