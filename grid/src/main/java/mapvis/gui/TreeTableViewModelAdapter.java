package mapvis.gui;

import javafx.scene.control.TreeItem;
import mapvis.models.TreeModel;
import utils.Node;

public class TreeTableViewModelAdapter {
    TreeModel<utils.Node> tree;

    public TreeTableViewModelAdapter(TreeModel<utils.Node> tree){
        this.tree = tree;
    }

    public TreeItem<utils.Node> getRoot(){

        TreeItem<utils.Node> root = translateTree(tree.getRoot());
        return root;
    }

    private TreeItem<utils.Node> translateTree(utils.Node p){
        TreeItem<utils.Node> item = new TreeItem<>(p);

        for (utils.Node child : tree.getChildren(p)) {
            item.getChildren().add(translateTree(child));
        }
        return item;
    }
}
