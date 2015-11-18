package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dacc on 10/22/2015.
 * FilesystemNode is represents a file or directory
 * in a common filesystem tree. This class can be used
 * by @ITreeGenerator to construct the filesystem tree
 * in memory
 */
public class FilesystemNode extends Node {

    private File file;
    private NodeState nodeState;

    /**
     * constructor
     * @param pathname the path of the file/directory this node
     *                 refers to
     */
    public FilesystemNode(String pathname) {
        this.file = new File(pathname);
        this.setLabel(file.getName());
        this.nodeState = NodeState.created;
    }

    @Override
    public List<INode> getChildren() {
        if(getNodeState() == NodeState.created) {
            //in case the Node was just created the children of this
            //node are not yet cached in memory => create children
            //according to filesystem
            List<INode> result = Arrays.asList(file.listFiles())
                    .stream()
                    .map(file -> new FilesystemNode(file.getPath()))
                    .collect(Collectors.<INode>toList());
            return result;
        }
        //use the pre-cached children
        return super.getChildren();
    }

    @Override
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    public INode.NodeType getNodeType() {
        //files are leaves; directories are nodes
        if(file.isFile()){
            return INode.NodeType.Leaf;
        }else{
            if(file.isDirectory()){
                return INode.NodeType.Node;
            }
        }
        return INode.NodeType.Undefined;
    }

    public void setNodeState(NodeState newState) {
        nodeState = newState;
    }

    public NodeState getNodeState() {
        return nodeState;
    }
}
