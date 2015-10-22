package mapvis.fileSystemTree;

import mapvis.common.datatype.ITreeNode;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by dacc on 10/22/2015.
 */
public class FilesystemNode extends File implements ITreeNode {

    public FilesystemNode(String pathname) {
        super(pathname);
    }

    @Override
    public List<ITreeNode> getDirectChildren() {
        List<ITreeNode> result = Arrays.asList(this.listFiles())
                .stream()
                .map(file -> new FilesystemNode(file.getPath()))
                .collect(Collectors.<ITreeNode>toList());
        return result;
    }

    @Override
    public NodeType getNodeType() {
        if(this.isFile()){
            return NodeType.Leaf;
        }else{
            if(this.isDirectory()){
                return NodeType.Node;
            }
        }
        return NodeType.Undefined;
    }
}
