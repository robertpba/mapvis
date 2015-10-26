package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dacc on 10/23/2015.
 */
public class TreeGeneratorSizeOneLeaves implements ITreeGenerator {

    private int id = 0;
    private INode rootNode;

    private int getNewID(){
        return id++;
    }

    private void processSizeOfINode(final INode iNode) {
        iNode.setId(Integer.toString(getNewID()));
        if(iNode.getNodeType() == INode.NodeType.Leaf){
            iNode.setSize(1);
            iNode.setNodeState(INode.NodeState.connectedToTree);
            return;
        }
        double leafCounter = 0;

        for (INode child : iNode.getChildren()) {
            // for directories the sum of the size of the subfolders is used
            processSizeOfINode(child);
            leafCounter += (double) child.getVal("size");
        }

        iNode.setVal("size", leafCounter);
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
