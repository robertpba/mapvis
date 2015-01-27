package mapvis.common.datatype;

import java.util.*;

public class TreeImp<T> implements Tree<T> {
    private T root;
    private Set<T> nodes = new HashSet<>();
    private Set<T> leaves = new HashSet<>();
    private Map<T, Set<T>> p2c = new HashMap<>();
    private Map<T, T> c2p = new HashMap<>();

    @Override
    public Set<T> getChildren(T node){
        Set<T> children = p2c.get(node);
        if (children == null)
            return Collections.EMPTY_SET;

        return Collections.unmodifiableSet(children);
    }
    @Override
    public T getParent(T node){
        return c2p.get(node);
    }
    @Override
    public Set<T> getNodes(){
        return Collections.unmodifiableSet(nodes);

    }
    @Override
    public Set<T> getLeaves(){
        return Collections.unmodifiableSet(leaves);
    }

    public void setRoot(T root){
        if (root != null)
            this.root = root;
        else
            throw new RuntimeException("can't change the root");
        nodes.add(root);
    }
    public void addChild(T parent, T child){
        if (nodes.contains(child))
            throw new RuntimeException("the child already exists");
        if (!nodes.contains(parent))
            throw new RuntimeException("the parent doesn't exist");

        Set<T> children = p2c.get(parent);
        if (children == null)
            p2c.put(parent, children = new HashSet<>());
        children.add(child);
        c2p.put(child, parent);
        nodes.add(child);
    }

    @Override
    public T getRoot() {
        return root;
    }
}
