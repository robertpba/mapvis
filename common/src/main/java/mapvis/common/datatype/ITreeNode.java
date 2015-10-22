package mapvis.common.datatype;



import java.util.List;

/**
 * Created by dacc on 10/8/2015.
 */
public interface ITreeNode {
    enum NodeType{
        Node,
        Leaf,
        Undefined
    }

    List<? extends ITreeNode> getDirectChildren();

    String getName();

    NodeType getNodeType();
}
