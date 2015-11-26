package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * This TreeGenerator creates a tree by recursively querying the children of the specified rootNode.
 * The size of a node is the sum of its children. INodes of type INode.NodeType.Leaf are counted.
 * However, in the resulting tree, leaves are those INodes whose direct children are only of type INode.NodeType.Leaf.
 * In case an INode has children of type INode.NodeType.Leaf and INode.NodeType.Node, this node created as
 * an inner node which has two types as its children: First, the children of type INode.NodeType.Node.
 * Second, one LeafNode with label *, which has the count of INodes returning INode.NodeType.Leaf as its size.
 * By the example of an directory tree, this TreeGenerator creates a tree which consists one INode for each directory.
 * An INode referring to a directory, which contains only files, has the count of the files as its size and is a Leaf in the
 * resulting tree. For directories containing files and directories: Subdirectories added as common children.
 * The files are summarized in one Leaf with the label * and the number of files as its size.
 */
public class TreeGeneratorSummedLeaves implements ITreeGenerator {

    private int id = 0;
    private INode rootNode;

    private int getNewID(){
        return id++;
    }

    private void processSizeOfINode(final INode iNode) {
        iNode.setId(Integer.toString(getNewID()));

        double countDirectLeavesOfNode = 0;
        double countChildLeavesOfNode = 0;
        List<INode> filteredChildren = new ArrayList<>();
        for (INode child : iNode.getChildren()) {
            // for directories the sum of the size of the subfolders is used
            if(child.getNodeType() == INode.NodeType.Node){
                processSizeOfINode(child);
                filteredChildren.add(child);
                countChildLeavesOfNode += child.getSize();
                // files are counted
            }else if(child.getNodeType() == INode.NodeType.Leaf){
                countDirectLeavesOfNode += 1.0;
            }
        }
        // if there are leaves and subnodes, create a node which holds all the leafs
        // to make sure the current node has the size of all subnodes including the leafs
        // which are direct children
        if(countDirectLeavesOfNode > 0 && countChildLeavesOfNode != 0){
            //create * node of size of direct leaves
            Node dummyChildForDirectFilesInFolder = new Node(Integer.toString(getNewID()), "*");
            dummyChildForDirectFilesInFolder.setSize(countDirectLeavesOfNode);
            filteredChildren.add(dummyChildForDirectFilesInFolder);
        }

        iNode.setChildren(filteredChildren);
        iNode.setSize(countDirectLeavesOfNode + countChildLeavesOfNode);
        iNode.setNodeState(INode.NodeState.connectedToTree);
    }

    @Override
    public void setRootNode(INode rootNode){
        this.rootNode = rootNode;
    }

    @Override
    public INode genTree() {
        if(rootNode == null || rootNode.getNodeType() == INode.NodeType.Undefined){
            return new Node(Integer.toString(getNewID()), "undefined");
        }
        processSizeOfINode(rootNode);
        return rootNode;
    }
}
