package mapvis.common.datatype;

import java.util.Set;

public interface Tree<T> {
    Set<T> getChildren(T node);

    T getParent(T node);

    Set<T> getNodes();

    Set<T> getLeaves();

    T getRoot();
}
