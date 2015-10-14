package mapvis.fileSystemTree;

import mapvis.common.datatype.Node;

import java.util.List;

/**
 * Created by dacc on 10/8/2015.
 */
public class TreeGenerator {

    private int id = 0;

    private int getNewID(){
        return id++;
    }

    private int createChildrenForNode(final ITreeNode iTreeNode, Node node) {
        int numOfChildren = 0;
        List<ITreeNode> children = iTreeNode.getChildren();
        for (ITreeNode child : children) {
            Node childNode = new Node(Integer.toString(getNewID()), child.getName());
            numOfChildren++;
            numOfChildren += createChildrenForNode(child, childNode);
            node.getChildren().add(childNode);
        }

        node.setVal("size", (double) numOfChildren);
        return numOfChildren;
    }



    public Node genTree(ITreeNode root) {
        Node rootNode = new Node(Integer.toString(getNewID()), root.getName());
        int numOfChildren = createChildrenForNode(root, rootNode);
        rootNode.setVal("size", (double) numOfChildren);
        return rootNode;
    }
}
