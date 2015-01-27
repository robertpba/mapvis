package mapvis.common.datatype;

import java.util.*;

public class MapNodeImp implements Node {

    public MapNodeImp(String id, String label) {
        setId(id);
        setLabel(label==null?"":label);
    }

    private final HashMap<Object,Object> fieldmap = new HashMap<>();

    @Override
    public String getId() {
        return (String) fieldmap.get("id");
    }

    @Override
    public void setId(String id) {
        fieldmap.put("id",id);
    }

    @Override
    public String getLabel() {
        return (String) fieldmap.get("label");
    }

    @Override
    public void setLabel(String name) {
        fieldmap.put("label",name);

    }

    @Override
    public Tree getTree() {
        return (Tree) fieldmap.get("tree");
    }

    @Override
    public void setTree(Tree name) {
        fieldmap.put("tree",name);

    }



    public int size() {
        return fieldmap.size();
    }

    public boolean isEmpty() {
        return fieldmap.isEmpty();
    }

    public boolean containsKey(Object key) {
        return fieldmap.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return fieldmap.containsValue(value);
    }

    public Object get(Object key) {
        return fieldmap.get(key);
    }

    public Object put(Object key, Object value) {
        return fieldmap.put(key, value);
    }

    public Object remove(Object key) {
        return fieldmap.remove(key);
    }

    public void putAll(Map<?, ?> m) {
        fieldmap.putAll(m);
    }

    public void clear() {
        fieldmap.clear();
    }

    public Set<Object> keySet() {
        return fieldmap.keySet();
    }

    public Collection<Object> values() {
        return fieldmap.values();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return fieldmap.entrySet();
    }

}
