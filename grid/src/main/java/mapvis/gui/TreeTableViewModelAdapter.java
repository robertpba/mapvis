package mapvis.gui;

import javafx.scene.control.TreeItem;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Tree2;

public class TreeTableViewModelAdapter {
    private Tree2<INode> tree;

    public TreeTableViewModelAdapter(Tree2<INode> tree){
        this.tree = tree;
    }

    public TreeItem<INode> getRoot(){
        TreeItem<INode> root = translateTree(tree.getRoot());
        return root;
    }

    private TreeItem<INode> translateTree(INode p){
        TreeItem<INode> item = new TreeItem<>(p);
        for (INode child : tree.getChildren(p)) {
            item.getChildren().add(translateTree(child));
        }
        return item;
    }
}
