package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

/**
 * Created by dacc on 10/22/2015.
 */
public class TreeGenerator {

    private int id = 0;
    private INode rootNode;

    public void configure(INode rootNode){
        this.rootNode = rootNode;
    }

    private int getNewID(){
        return id++;
    }

    private Node createSingleNodeFromINode(INode iNode){
        Node node = new Node(Integer.toString(getNewID()), iNode.getLabel());
        return node;
    }

    private Node createNodeFromINode(final INode iNode) {
        Node node = createSingleNodeFromINode(iNode);

        double countDirectLeafsOfNode = 0;
        double countChildLeafsOfNode = 0;

        for (INode child : iNode.getChildren()) {
            // for directories the sum of the size of the subfolders is used
            if(child.getNodeType() == INode.NodeType.Node){
                Node childNode = createNodeFromINode(child);
                node.getChildren().add(childNode);
                countChildLeafsOfNode += (double) childNode.getVal("size");
                // files are counted
            }else if(child.getNodeType() == INode.NodeType.Leaf){
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
        if(rootNode == null || rootNode.getNodeType() == INode.NodeType.Undefined){
            return new Node(Integer.toString(getNewID()), "undefined");
        }
        Node rootTreeNode = createNodeFromINode(rootNode);
        return rootTreeNode;
    }
}
