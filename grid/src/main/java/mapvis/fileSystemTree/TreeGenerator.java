package mapvis.fileSystemTree;

import mapvis.common.datatype.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/8/2015.
 */
public class TreeGenerator {

    private int id = 0;

    private int getNewID(){
        return id++;
    }

    private Node genSubTree(ITreeNode iTreeNode) {
        Node node = new Node(Integer.toString(getNewID()), iTreeNode.getName());
        List<ITreeNode> children = iTreeNode.getChildren();

        for (ITreeNode child : children) {
            ++id;
            node.getChildren().add(genSubTree(child));
        }
        node.setVal("size", (double) children.size());
        return node;
    }

    public Node genTree(ITreeNode root) {
        return genSubTree(root);
    }
}
