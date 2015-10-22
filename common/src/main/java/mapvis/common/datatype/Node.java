package mapvis.common.datatype;

import java.util.*;


public class Node{

    String id = "";
    String label = "";
    private List<Node> children = new ArrayList<>();
    private Map<Object,Object> data = new HashMap<>();

    protected Node(){

    }

    public Node(String id, String label) {
        setId(id);
        setLabel(label==null?"":label);
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

    public List<Node> getChildren() {
        return children;
    }
    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
