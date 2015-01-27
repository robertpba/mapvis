package mapvis.gui;

import javafx.scene.control.TreeItem;
import mapvis.common.datatype.Tree2;
import utils.Node;

public class TreeTableViewModelAdapter {
    Tree2<Node> tree;

    public TreeTableViewModelAdapter(Tree2<Node> tree){
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
