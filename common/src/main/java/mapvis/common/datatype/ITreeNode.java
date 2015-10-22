package mapvis.common.datatype;

import mapvis.udcTree.UDCCathegory;

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

    List<UDCCathegory> getDirectChildren();

    String getName();

    NodeType getNodeType();
}
