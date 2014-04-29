package mapvis.liquidvis.model;

import java.util.*;

public class Tree<V> {
    public Set<V> vertices = new HashSet<>();
    public Map<V, Set<V>> childMap = new HashMap<>();
    public Map<V, V> parentMap = new HashMap<>();

    public Map<V, Map<String, Object>> data = new HashMap<>();

    public V root;

    public void setRoot(V vertex){
        root = vertex;
    }

    public boolean addVertex(V vertex){
        return vertices.add(vertex);
    }

    public boolean addEdge(V parent, V child){
        Set<V> children = childMap.get(parent);
        if (children == null)
            childMap.put(parent, children = new HashSet<>());
        if (!children.contains(child)) {
            children.add(child);
        }
        return true;
    }

    public void removeEdge(V parent, V child){
        Set<V> children = childMap.get(parent);
        parentMap.remove(child);
        children.remove(parent);
    }


    public void addChild(V parent, V child){
        addVertex(child);
        addEdge(parent,child);
    }

    public V getParent(V child){
        return parentMap.get(child);
    }

    public Collection<V> getChildren(V parent){
        Collection<V> children = childMap.get(parent);
        return children == null? Collections.emptySet()
                : Collections.unmodifiableCollection(children);
    }

    public Object getValue(V vertex, String key ){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            return null;

        return map.get(key);
    }

    public Object setValue(V vertex, String key, Object value){
        Map<String, Object> map = data.get(vertex);

        if (map == null)
            data.put(vertex , map = new HashMap<>());

        return map.put(key, value);
    }
}
