package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.util.List;

/**
 * Created by dacc on 10/23/2015.
 * This TreeGenerator creates a tree in which the leaves contain node
 * of size one. The size of the parents of these leaves corresponds to
 * the sum of the size its children.
 */
public class TreeGeneratorSizeOneLeaves implements ITreeGenerator {

    private int id = 0;
    private INode rootNode;

    private void processSizeOfINode(final INode iNode) {
        //set id
        iNode.setId(Integer.toString(getNewID()));

        if(iNode.getNodeType() == INode.NodeType.Leaf){
            //leaves have size 1
            iNode.setSize(1);
            iNode.setNodeState(INode.NodeState.connectedToTree);
            return;
        }

        double leafCounter = 0;
        List<INode> children = iNode.getChildren();
        for (INode child : children) {
            // for directories/inner nodes
            // the sum of the size of the children/subfolders is used
            processSizeOfINode(child);
            leafCounter += child.getSize();
        }

        iNode.setSize(leafCounter);
        iNode.setChildren(children);
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

    private int getNewID(){
        return id++;
    }
}
