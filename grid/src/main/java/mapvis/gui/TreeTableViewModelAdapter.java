package mapvis.gui;

import javafx.scene.control.TreeItem;
import mapvis.Impl.TreeModel;

public class TreeTableViewModelAdapter {
    TreeModel<Integer> tree;

    public TreeTableViewModelAdapter(TreeModel<Integer> tree){
        this.tree = tree;
    }

    public TreeItem<Node> getRoot(){
        TreeItem<Node> root = translateTree(tree.getRoot());
        return root;
    }

    private TreeItem<Node> translateTree(Integer p){
        Node model = new Node(p);
        TreeItem<Node> item = new TreeItem<>(model);

        for (Integer integer : tree.getChildren(p)) {
            item.getChildren().add(translateTree(integer));
        }
        return item;
    }

    public class Node {
        private final int nid;

        public Node(int nid){
            this.nid = nid;
        }

        public int getId() { return nid; }

        public int getSize() { return tree.getWeight(nid); }
    }
}
