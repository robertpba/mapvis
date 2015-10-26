package mapvis.treeGenerator;

import mapvis.common.datatype.INode;
import mapvis.common.datatype.Node;

import java.util.List;
import java.util.Map;

/**
 * Created by dacc on 10/23/2015.
 */
public class UDCNode implements INode {

    INode node;
    public UDCNode(String ID, String label){
        node = new Node(ID, label);
    }

    @Override
    public Map<Object, Object> getData() {
        return node.getData();
    }

    @Override
    public void setData(Map<Object, Object> data) {
        node.setData(data);
    }

    @Override
    public void setLabel(String label) {
        node.setLabel(label);
    }

    @Override
    public String getId() {
        return node.getId();
    }

    @Override
    public void setId(String id) {
        node.setId(id);
    }

    @Override
    public double getSize() {
        return node.getSize();
    }

    @Override
    public void setSize(double size) {
        node.setSize(size);
    }

    @Override
    public Object getVal(String key) {
        return node.getVal(key);
    }

    @Override
    public void setVal(String key, Object val) {
        node.setVal(key, val);
    }

    @Override
    public List<INode> getChildren() {
        return node.getChildren();
    }

    @Override
    public void setChildren(List<INode> children) {
        node.setChildren(children);
    }

    @Override
    public String getLabel() {
        return node.getLabel();
    }

    @Override
    public NodeType getNodeType() {
        if(node.getChildren() == null)
            return NodeType.Leaf;
        return null;
    }

    @Override
    public void setNodeState(NodeState newState) {

    }

    @Override
    public NodeState getNodeState() {
        return null;
    }
//
//    public UDCNode(String conceptName, String englishLabel) {
//        node = new Node(conceptName, englishLabel);
//    }
//
//    @Override
//    public List<? extends ITreeNode> getDirectChildren() {
//        return (List<? extends ITreeNode>) getChildren();
//    }
//
//    @Override
//    public String getLabel() {
//        return getLabel();
//    }
//
//    @Override
//    public NodeType getNodeType() {
////        if(getDirectChildren() == null)
////            return NodeType.Undefined;
////        if(getDirectChildren().size() == 0)
////            return NodeType.Leaf;
//        if(getDirectChildren() == null)
//            return NodeType.Leaf;
//        List<UDCNode> node = new ArrayList<>();
//        setChildren(node);
//        return NodeType.Node;
//    }
//
//    public void setChildren(List<UDCNode> children) {
//        List<INode> node = (List<INode>) children;
//        super.setChildren(children);
//    }
}
