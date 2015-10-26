package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/22/2015.
 */
public class TreeGeneratorSummedLeaves implements ITreeGenerator {

    private int id = 0;
    private INode rootNode;

    private int getNewID(){
        return id++;
    }

    private void processSizeOfINode(final INode iNode) {
        iNode.setId(Integer.toString(getNewID()));

        double countDirectLeafsOfNode = 0;
        double countChildLeafsOfNode = 0;
        List<INode> filteredChildren = new ArrayList<>();
        for (INode child : iNode.getChildren()) {
            // for directories the sum of the size of the subfolders is used
            if(child.getNodeType() == INode.NodeType.Node){
                processSizeOfINode(child);
                filteredChildren.add(child);
                countChildLeafsOfNode += (double) child.getVal("size");
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
            filteredChildren.add(dummyChildForDirectFilesInFolder);
        }
        iNode.setChildren(filteredChildren);
        iNode.setVal("size", countDirectLeafsOfNode + countChildLeafsOfNode);
        iNode.setNodeState(INode.NodeState.connectedToTree);
    }

    @Override
    public void configure(INode rootNode){
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
