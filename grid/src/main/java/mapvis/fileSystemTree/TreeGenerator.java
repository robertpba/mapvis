package mapvis.fileSystemTree;

import mapvis.common.datatype.ITreeNode;
import mapvis.common.datatype.Node;

/**
 * Created by dacc on 10/22/2015.
 */
public class TreeGenerator {

    private int id = 0;
    private ITreeNode rootNode;

    public void configure(ITreeNode rootNode){
        this.rootNode = rootNode;
    }

    private int getNewID(){
        return id++;
    }

    private Node createSingleNodeFromITreeNode(ITreeNode iTreeNode){
        Node node = new Node(Integer.toString(getNewID()), iTreeNode.getName());
        return node;
    }

    private Node createNodeFromITreeNode(final ITreeNode iTreeNode) {
        Node node = createSingleNodeFromITreeNode(iTreeNode);

        double countDirectLeafsOfNode = 0;
        double countChildLeafsOfNode = 0;

        for (ITreeNode child : iTreeNode.getDirectChildren()) {
            // for directories the sum of the size of the subfolders is used
            if(child.getNodeType() == ITreeNode.NodeType.Node){
                Node childNode = createNodeFromITreeNode(child);
                node.getChildren().add(childNode);
                countChildLeafsOfNode += (double) childNode.getVal("size");
                // files are counted
            }else if(child.getNodeType() == ITreeNode.NodeType.Leaf){
                countDirectLeafsOfNode += 1.0;
            }
        }
        // if there are leafs and subnodes, create a node which holds all the leafs
        // to make sure the current node has the size of all subnodes including the leafs
        // which are direct children
        if(countDirectLeafsOfNode > 0 && countChildLeafsOfNode != 0){
            Node dummyChildForDirectFilesInFolder = new Node(Integer.toString(getNewID()), "*");
            dummyChildForDirectFilesInFolder.setSize(countDirectLeafsOfNode);
            node.getChildren().add(dummyChildForDirectFilesInFolder);
        }
        node.setVal("size", countDirectLeafsOfNode + countChildLeafsOfNode);
        return node;
    }


    public Node genTree() {
        if(rootNode == null || rootNode.getNodeType() == ITreeNode.NodeType.Undefined){
            return new Node(Integer.toString(getNewID()), "undefined");
        }
        Node rootTreeNode = createNodeFromITreeNode(rootNode);
        return rootTreeNode;
    }
}
