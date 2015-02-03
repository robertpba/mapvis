package mapvis.gui;

import javafx.scene.control.TreeItem;
import mapvis.common.datatype.Node;
import mapvis.common.datatype.Tree2;

public class TreeTableViewModelAdapter {
    Tree2<Node> tree;

    public TreeTableViewModelAdapter(Tree2<Node> tree){
        this.tree = tree;
    }

    public TreeItem<Node> getRoot(){

        TreeItem<Node> root = translateTree(tree.getRoot());
        return root;
    }

    private TreeItem<Node> translateTree(Node p){
        TreeItem<Node> item = new TreeItem<>(p);

        for (Node child : tree.getChildren(p)) {
            item.getChildren().add(translateTree(child));
        }
        return item;
    }
}
