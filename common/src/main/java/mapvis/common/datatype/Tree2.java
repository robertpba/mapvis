package mapvis.common.datatype;

import java.util.List;
import java.util.Set;

public interface Tree2<T> extends Tree<T>
{
    Set<T> getChildren(T obj);
    T getParent(T node);
    Set<T> getNodes();
    T getRoot();
    Set<T> getLeaves();

    int getDepth(T elem);

    int getWeight(T elem);

    // lowest common ancestor
    T getLCA(T o1, T o2);

    List<T> getPathToNode(T elem);

    boolean isAncestorOf(T ancestor, T decedent);

    boolean isSibling(T o1, T o2);


}
