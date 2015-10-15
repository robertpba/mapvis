package mapvis.fileSystemTree;

import mapvis.common.datatype.Node;

import java.util.List;

/**
 * Created by dacc on 10/14/2015.
 */
public class TreeGeneratorWithCustomNodeSize {
    private int id = 0;

    private int getNewID(){
        return id++;
    }

    private void createChildrenForNode(final ITreeNodeWithCustomSize iTreeNodeWithSize, Node node) {

        List<ITreeNodeWithCustomSize> children = iTreeNodeWithSize.getChildren();
        for (ITreeNodeWithCustomSize child : children) {
            Node childNode = new Node(Integer.toString(getNewID()), child.getName());
            createChildrenForNode(child, childNode);
            node.getChildren().add(childNode);
        }

        node.setVal("size", iTreeNodeWithSize.getNodeSize());
    }



    public Node genTree(final ITreeNodeWithCustomSize root) {
        Node rootNode = new Node(Integer.toString(getNewID()), root.getName());
        createChildrenForNode(root, rootNode);
        rootNode.setVal("size", root.getNodeSize());
        return rootNode;
    }
}
