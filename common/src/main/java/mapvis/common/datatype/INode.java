package mapvis.common.datatype;

import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 10/23/2015.
 */
public interface INode {

    Map<Object, Object> getData();
    void setData(Map<Object, Object> data);

    String getLabel();
    void setLabel(String label);

    String getId();
    void setId(String id);

    double getSize();
    void setSize(double size);

    Object getVal(String key);
    void setVal(String key, Object val);

    List<INode> getChildren();
    void setChildren(List<INode> children);

    enum NodeType{
        Node,
        Leaf,
        Undefined
    }
    NodeType getNodeType();
}
