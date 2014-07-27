package mapvis.tree;

import java.util.List;
import java.util.Set;

public interface TreeModel<T> {
    Set<T> getChildren(T obj);

    T getParent(T node);

    Set<T> getNodes();

    T getRoot();

    int getDepth(T elem);

    int getWeight(T elem);

    Set<T> getLeaves();

    // lowest common ancestor
    T getLCA(T o1, T o2);

    List<T> getPathToNode(T elem);

    boolean isAncestorOf(T ancestor, T decedent);

    boolean isSibling(T o1, T o2);
}
