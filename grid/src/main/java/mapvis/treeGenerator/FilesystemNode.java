package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dacc on 10/22/2015.
 */
public class FilesystemNode extends Node {

    private File file;

    public FilesystemNode(String pathname) {
        file = new File(pathname);
        this.setLabel(file.getName());
    }

    @Override
    public List<INode> getChildren() {
        if(getNodeState() == NodeState.created) {
            List<INode> result = Arrays.asList(file.listFiles())
                    .stream()
                    .map(file -> new FilesystemNode(file.getPath()))
                    .collect(Collectors.<INode>toList());
            return result;
        }
        return super.getChildren();
    }

    @Override
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    public INode.NodeType getNodeType() {
        if(file.isFile()){
            return INode.NodeType.Leaf;
        }else{
            if(file.isDirectory()){
                return INode.NodeType.Node;
            }
        }
        return INode.NodeType.Undefined;
    }
}
