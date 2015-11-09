package mapvis.common.datatype;


import java.util.*;


public class Node implements INode {

    String id = "";
    String label = "";
    private List<INode> children = new ArrayList<>();
    private Map<Object, Object> data = new HashMap<>();
    private NodeState nodeState;

    private NodeState state;

    protected Node(){
        nodeState = NodeState.created;
    }

    public Node(String id, String label) {
        setId(id);
        setLabel(label==null?"":label);
        nodeState = NodeState.created;
    }

    public Map<Object, Object> getData() {
        return data;
    }
    public void setData(Map<Object, Object> data) {
        this.data = data;
    }



    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public double getSize() {
        Object size = data.get("size");
        if(size == null){
            return 0.0;
        }
        return (double) size;
    }
    public void setSize(double size) {
        data.put("size", size);
    }

    public Object getVal(String key) {
        return data.get(key);
    }
    public void setVal(String key, Object val) {
        data.put(key, val);
    }

    public List<INode> getChildren() {
        return children;
    }
    public void setChildren(List<INode> children) {
        this.children = children;
    }


    @Override
    public NodeType getNodeType() {
        if(children == null || children.size() == 0)
            return NodeType.Leaf;
        return NodeType.Node;
    }

    @Override
    public void setNodeState(NodeState newState) {
        nodeState = newState;
    }

    @Override
    public NodeState getNodeState() {
        return nodeState;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || obj.getClass() != this.getClass())
            return false;
        Node node = (Node) obj;

        return node.getId().equals(this.getId());
//        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return id.hashCode() * label.hashCode();
    }
}
