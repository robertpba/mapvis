package mapvis.tree;

import java.util.*;
import java.util.stream.Collectors;

// TODO: not thread-safe
public class MPTT<T> {

    static public class MPTTNode<T> {
        public int left;
        public int right;
        public int depth;
        public T element;

        public MPTTNode<T> parent;
        public List<MPTTNode<T>> children = new ArrayList<>();
    }

    public MPTTNode<T> root;
    // object to node mapping
    private Map<T, MPTTNode<T>> o2n = new HashMap<>();


    public Set<T> getChildren(T obj){
        Set<T> set = o2n.get(obj).children.stream().map(n -> n.element)
                .collect(Collectors.toSet());
        return set;
    }

    public T getParent(T node){
        return o2n.get(node).parent.element;
    }

    public Set<T> getNodes(){
        return Collections.unmodifiableSet(o2n.keySet());
    }

    public void setRoot(T obj){
        // TODO: what if the root is already in the tree
        if (o2n.containsKey(obj))
            throw new RuntimeException("the child already exists");
        if (root == null) {
            this.root = new MPTTNode<>();
            this.root.element = obj;
        }
        else
            throw new RuntimeException("can't change the root");
        o2n.put(obj, root);
    }

    public void addChild(T parent, T child){
        if (o2n.containsKey(child))
            throw new RuntimeException("the child already exists");
        if (!o2n.containsKey(parent))
            throw new RuntimeException("the parent doesn't exist");

        MPTTNode<T> nParent = o2n.get(parent);

        MPTTNode<T> nChild = new MPTTNode<>();
        nChild.element = child;
        nParent.children.add(nChild);
        nChild.parent = nParent;

        o2n.put(child, nChild);
    }

    public T getRoot() {
        if (root == null)
            return null;
        return root.element;
    }


    // recalculates all cached information after data changed
    public void refresh(){
        populate(1, 0, root);
        refreshLeafCache();
    }

    // recalculate the left and right value of all nodes.
    /// @return: right
    public int populate(int left, int depth, MPTTNode<T> node){
        node.left = left;
        node.depth = depth;
        left ++;
        for (MPTTNode<T> child : node.children) {
            left = populate(left, depth + 1, child) + 1;
        }
        node.right = left;
        return left;
    }

    public int getDepth(T elem){
        MPTTNode<T> node = o2n.get(elem);
        return node.depth;
    }

    //public  T root;
    private Set<T> leaves = new HashSet<>();
    public Set<T> getLeaves(){
        return Collections.unmodifiableSet(leaves);
    }
    private void refreshLeafCache(){
        leaves.clear();
        refreshLeafCache(root);
    }
    private void refreshLeafCache(MPTTNode<T> node){
        if (node.children.size() == 0)
            leaves.add(node.element);
        else {
            node.children.forEach(this::refreshLeafCache);
        }
    }

    // lowest common ancestor
    public T getLCA(T o1, T o2){
        MPTTNode<T> n1 = o2n.get(o1);
        MPTTNode<T> n2 = o2n.get(o2);
        MPTTNode<T> lca = getLCA(n1, n2);
        if (lca != null)
            return lca.element;
        return null;
    }

    private MPTTNode<T> getLCA(MPTTNode<T> n1, MPTTNode<T> n2) {
        if (n1 == null)
            return null;
        if (n1.left < n2.left && n1.right > n2.right) {
            return n1;
        }
        return getLCA(n1.parent, n2);
    }

    public List<T> getPathToNode(T elem){
        ArrayList<T> list = new ArrayList<>();
        MPTTNode<T> node = o2n.get(elem);
        list.add(elem);

        while (node.parent != null){
            node = node.parent;
            list.add(node.element);
        }

        Collections.reverse(list);
        return list;
    }

    public boolean isAncestorOf(T ancestor, T decedent){
        MPTTNode<T> na = o2n.get(ancestor);
        MPTTNode<T> nd = o2n.get(decedent);
        if (na == null)
            return false;
        if (nd == null)
            return false;

        return na.left < nd.left && na.right > nd.right;
    }
    public boolean isSibling(T o1, T o2){
        MPTTNode<T> n1 = o2n.get(o1);
        MPTTNode<T> n2 = o2n.get(o2);
        return n1.parent == n2.parent;
    }
}
